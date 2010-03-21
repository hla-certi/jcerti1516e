// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2009-2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi.rti.impl;

import certi.communication.*;
import certi.communication.messages.*;
import certi.logging.HtmlFormatter;
import hla.rti.*;
import hla.rti.jlc.RTIambassadorEx;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CertiRtiAmbassador implements RTIambassadorEx {

    private final static Logger LOGGER = Logger.getLogger(CertiRtiAmbassador.class.getName());
    private FederateAmbassador federateAmbassador;
    private Socket socket;
    private ServerSocket serverSocket;
    private MessageBuffer messageBuffer = new MessageBuffer();

    class JavaMachineHook extends Thread {

        private CertiRtiAmbassador rtia;

        public JavaMachineHook(CertiRtiAmbassador rtia) {
            super("Certi Java machine hook");
            this.rtia = rtia;
        }

        @Override
        public void run() {
            super.run();
            rtia.closeConnexion();
        }
    }

    class SocketListener extends Thread {

        private CertiRtiAmbassador rtia;

        public SocketListener(CertiRtiAmbassador rtia) {
            super("Socket listener");
            this.rtia = rtia;
        }

        @Override
        public void run() {
            super.run();
            try {
                rtia.socket = rtia.serverSocket.accept();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Socket listener error", ex);
            }
        }
    }

    public CertiRtiAmbassador() throws IOException {
        /////////////////////
        // Prepare logging //
        /////////////////////

        //FileHandler fileTxt = new FileHandler("rti.log");
        //fileTxt.setFormatter(new SimpleFormatter());
        //LOGGER.addHandler(fileTxt);

        FileHandler fileHtml = new FileHandler("rti-ambassador.html");
        fileHtml.setFormatter(new HtmlFormatter());
        LOGGER.addHandler(fileHtml);
        LOGGER.setLevel(Level.ALL);

        ////////////////////
        // Prepare socket //
        ////////////////////
        serverSocket = new ServerSocket(0);

        LOGGER.info("Listening with server socket on port " + serverSocket.getLocalPort());

        SocketListener socketListener = new SocketListener(this);
        socketListener.start();

        Process rtiaProcess = Runtime.getRuntime().exec("rtia -p " + serverSocket.getLocalPort());

        try {
            socketListener.join();
            LOGGER.info("Successfully connected.");
        } catch (InterruptedException ex) {
            LOGGER.severe("System was unable to receive connection on socket.");
        }

        ///////////////////////////////
        // Prepare Java runtime hook //
        ///////////////////////////////
        Runtime.getRuntime().addShutdownHook(new JavaMachineHook(this));
    }

    public boolean tick(double min, double max) throws RTIinternalError, ConcurrentAccessAttempted {
        try {
            return tickKernel(true, min, max);
        } catch (SpecifiedSaveLabelDoesNotExist ex) {
            LOGGER.severe("SpecifiedSaveLabelDoesNotExist in tick");
            throw new RTIinternalError("SpecifiedSaveLabelDoesNotExist in tick");
        }
    }

    public boolean tick2() throws SpecifiedSaveLabelDoesNotExist, ConcurrentAccessAttempted, RTIinternalError {
        tickKernel(false, Double.MAX_VALUE, 0.0);
        return false;
    }

    private boolean tickKernel(boolean multiple, double minimum, double maximum) throws SpecifiedSaveLabelDoesNotExist, ConcurrentAccessAttempted, RTIinternalError {
        //TODO Code is hard to read - rewrite
        TickRequest tickRequest = null;
        CertiMessage tickResponse = null;

        LOGGER.fine("Request callback(s) from the local RTIA");
        tickRequest = new TickRequest();
        tickRequest.setMultiple(multiple);
        tickRequest.setMinTickTime(minimum);
        tickRequest.setMaxTickTime(maximum);

        tickRequest.writeMessage(this.messageBuffer);

        try {
            this.messageBuffer.send(this.socket.getOutputStream());
        } catch (IOException ex) {
            throw new RTIinternalError("NetworkError in tick() while sending TICK_REQUEST: " + ex.getMessage());
        }

        LOGGER.fine("Reading response(s) from the local RTIA");
        while (true) { //TODO maybe restructuralize -> remove while true
            try {
                InputStream in = this.socket.getInputStream();

                tickResponse = this.messageBuffer.receive(in);
                LOGGER.info("Received: " + tickResponse.toString() + "\n");
            } catch (IOException ex) {
                throw new RTIinternalError("NetworkError in tick() while receiving response: " + ex.getMessage());
            } catch (CertiException certiException) {
                // tick() may only throw exceptions defined in the HLA standard
                // the RTIA is responsible for sending 'allowed' exceptions only
                try {
                    translateException(certiException);
                } catch (SpecifiedSaveLabelDoesNotExist ex) {
                    throw ex;
                } catch (ConcurrentAccessAttempted ex) {
                    throw ex;
                } catch (RTIinternalError ex) {
                    throw ex;
                } catch (RuntimeException ex) {
                    throw ex;
                } catch (Exception ex) {
                    LOGGER.severe("Tick() error");
                }
            }

            // If the type is TICK_REQUEST, the tickKernel() has terminated.
            if (tickResponse.getType() == CertiMessageType.TICK_REQUEST) {
                LOGGER.fine("TICK_REQUEST is received - ending tick");
                return ((TickRequest) tickResponse).isMultiple();
            } else {
                try {
                    // Otherwise, the RTI calls a FederateAmbassador service.
                    callFederateAmbassador(tickResponse);
                } catch (RTIinternalError ex) {
                    // RTIA awaits TICK_REQUEST_NEXT, terminate the tick() processing
                    TickRequestStop tickRequestStop = new TickRequestStop();
                    try {
                        processRequest(tickRequestStop);
                    } catch (Exception tickResponseException) {
                        // ignore the response, ignore exceptions
                        // rep.type == TICK_REQUEST;
                    }
                    // ignore the response and re-throw the original exception
                    throw ex;
                }

                LOGGER.fine("Sending TICK_NEXT");
                CertiMessage tickNext = new TickRequestNext();
                tickNext.writeMessage(this.messageBuffer);

                try {
                    this.messageBuffer.send(this.socket.getOutputStream());
                } catch (IOException ex) {
                    throw new RTIinternalError("NetworkError in tick() while sending TICK_REQUEST_NEXT: " + ex.getMessage());
                }
            }
        }
    }

    public void closeConnexion() {
        CloseConnexion request = new CloseConnexion();
        try {
            LOGGER.info("Closing connection to RTIA");
            processRequest(request);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error with closing connection", ex);
        }
    }

    public void createFederationExecution(String executionName, URL fed) throws FederationExecutionAlreadyExists, CouldNotOpenFED, ErrorReadingFED, RTIinternalError, ConcurrentAccessAttempted {
        if (executionName == null || executionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty execution name");
        }
        if (fed == null) {
            throw new RTIinternalError("Incorrect or empty fed file name");
        }

        CreateFederationExecution request = new CreateFederationExecution();
        request.setFederationName(executionName);

        try {
            request.setFedId(new File(fed.toURI()).getCanonicalPath());
        } catch (URISyntaxException ex) {
            Logger.getLogger(CertiRtiAmbassador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CertiRtiAmbassador.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            CertiMessage response = processRequest(request);
        } catch (FederationExecutionAlreadyExists ex) {
            throw ex;
        } catch (CouldNotOpenFED ex) {
            throw ex;
        } catch (ErrorReadingFED ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void destroyFederationExecution(String executionName) throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, RTIinternalError, ConcurrentAccessAttempted {
        if (executionName == null || executionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty execution name");
        }

        DestroyFederationExecution request = new DestroyFederationExecution();

        request.setFederationName(executionName);

        try {
            CertiMessage response = processRequest(request);
        } catch (FederatesCurrentlyJoined ex) {
            throw ex;
        } catch (FederationExecutionDoesNotExist ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int joinFederationExecution(String federateName, String federationExecutionName, FederateAmbassador federateReference) throws FederateAlreadyExecutionMember, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (federateName == null || federateName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federate name");
        }
        if (federationExecutionName == null || federationExecutionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federation name");
        }
        if (federateReference == null) {
            throw new RTIinternalError("Federate reference is null");
        }
        federateAmbassador = federateReference;

        JoinFederationExecution request = new JoinFederationExecution();

        request.setFederationName(federationExecutionName);
        request.setFederateName(federateName);

        try {
            JoinFederationExecution response = (JoinFederationExecution) processRequest(request);

            return response.getFederate();
        } catch (FederateAlreadyExecutionMember ex) {
            throw ex;
        } catch (FederationExecutionDoesNotExist ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int joinFederationExecution(String federateType, String federationExecutionName, FederateAmbassador federateReference, MobileFederateServices serviceReferences) throws FederateAlreadyExecutionMember, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resignFederationExecution(int resignAction) throws FederateOwnsAttributes, FederateNotExecutionMember, InvalidResignAction, RTIinternalError, ConcurrentAccessAttempted {
        ResignFederationExecution request = new ResignFederationExecution();
        request.setResignAction(resignAction);

        try {
            ResignFederationExecution response = (ResignFederationExecution) processRequest(request);
        } catch (FederateOwnsAttributes ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (InvalidResignAction ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (synchronizationPointLabel == null || synchronizationPointLabel.length() == 0) {
            throw new RTIinternalError("Incorrect or empty suncrhonization point label");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling registerFederationSynchronizationPoint with Tag NULL");
        }

        RegisterFederationSynchronizationPoint request = new RegisterFederationSynchronizationPoint();

        request.setLabel(synchronizationPointLabel);
        request.setTag(userSuppliedTag);
        request.setBooleanValue(false); //without set of federates

        try {
            RegisterFederationSynchronizationPoint response = (RegisterFederationSynchronizationPoint) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag, FederateHandleSet synchronizationSet) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (synchronizationPointLabel == null || synchronizationPointLabel.length() == 0) {
            throw new RTIinternalError("Incorrect or empty suncrhonization point label");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling registerFederationSynchronizationPoint with synchronizationSet NULL");
        }
        if (synchronizationSet == null) {
            throw new RTIinternalError("Calling registerFederationSynchronizationPoint with synchronizationSet NULL");
        }


        RegisterFederationSynchronizationPoint request = new RegisterFederationSynchronizationPoint();

        request.setLabel(synchronizationPointLabel);
        request.setTag(userSuppliedTag);

        if (synchronizationSet.isEmpty()) {
            request.setBooleanValue(false);
        } else {
            request.setBooleanValue(true);
            request.setAttributes(synchronizationSet);
        }

        try {
            RegisterFederationSynchronizationPoint response = (RegisterFederationSynchronizationPoint) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void synchronizationPointAchieved(String synchronizationPointLabel) throws SynchronizationLabelNotAnnounced, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (synchronizationPointLabel == null || synchronizationPointLabel.length() == 0) {
            throw new RTIinternalError("Incorrect or empty suncrhonization point label");
        }

        SynchronizationPointAchieved request = new SynchronizationPointAchieved();

        request.setLabel(synchronizationPointLabel);

        try {
            SynchronizationPointAchieved response = (SynchronizationPointAchieved) processRequest(request);
        }// catch (SynchronizationLabelNotAnnounced ex) {            throw ex;        }
        catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void requestFederationSave(String label, LogicalTime theTime) throws FederationTimeAlreadyPassed, InvalidFederationTime, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (label == null || label.length() == 0) {
            throw new RTIinternalError("Incorrect or empty label");
        }
        if (theTime == null) {
            throw new RTIinternalError("Incorrect time parameter supplied");
        }

        RequestFederationSave request = new RequestFederationSave();

        request.setFederationTime((CertiLogicalTime) theTime);
        request.setLabel(label);
        request.setBooleanValue(true);

        try {
            RequestFederationSave response = (RequestFederationSave) processRequest(request);
        } catch (FederationTimeAlreadyPassed ex) {
            throw ex;
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void requestFederationSave(String label) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (label == null || label.length() == 0) {
            throw new RTIinternalError("Incorrect or empty label");
        }

        RequestFederationSave request = new RequestFederationSave();

        request.setLabel(label);
        request.setBooleanValue(false); // without time

        try {
            RequestFederationSave response = (RequestFederationSave) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void federateSaveBegun() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        FederateSaveBegun request = new FederateSaveBegun();

        try {
            FederateSaveBegun response = (FederateSaveBegun) processRequest(request);
        } catch (SaveNotInitiated ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void federateSaveComplete() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        FederateSaveComplete request = new FederateSaveComplete();

        try {
            FederateSaveComplete response = (FederateSaveComplete) processRequest(request);
        } catch (SaveNotInitiated ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void federateSaveNotComplete() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        FederateSaveNotComplete request = new FederateSaveNotComplete();

        try {
            FederateSaveNotComplete response = (FederateSaveNotComplete) processRequest(request);
        } catch (SaveNotInitiated ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void requestFederationRestore(String label) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (label == null || label.length() == 0) {
            throw new RTIinternalError("Incorrect or empty label");
        }

        RequestFederationRestore request = new RequestFederationRestore();

        request.setLabel(label);

        try {
            RequestFederationRestore response = (RequestFederationRestore) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void federateRestoreComplete() throws RestoreNotRequested, FederateNotExecutionMember, SaveInProgress, RTIinternalError, ConcurrentAccessAttempted {
        FederateRestoreComplete request = new FederateRestoreComplete();

        try {
            FederateRestoreComplete response = (FederateRestoreComplete) processRequest(request);
        } catch (RestoreNotRequested ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void federateRestoreNotComplete() throws RestoreNotRequested, FederateNotExecutionMember, SaveInProgress, RTIinternalError, ConcurrentAccessAttempted {
        FederateRestoreNotComplete request = new FederateRestoreNotComplete();

        try {
            FederateRestoreNotComplete response = (FederateRestoreNotComplete) processRequest(request);
        } catch (RestoreNotRequested ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void publishObjectClass(int theClass, AttributeHandleSet attributeList) throws ObjectClassNotDefined, AttributeNotDefined, OwnershipAcquisitionPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (attributeList == null || attributeList.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributeList");
        }

        PublishObjectClass request = new PublishObjectClass();

        request.setObjectClass(theClass);
        request.setAttributes(attributeList);

        try {
            processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (OwnershipAcquisitionPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void unpublishObjectClass(int theClass) throws ObjectClassNotDefined, ObjectClassNotPublished, OwnershipAcquisitionPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        UnpublishObjectClass request = new UnpublishObjectClass();

        request.setObjectClass(theClass);

        try {
            UnpublishObjectClass response = (UnpublishObjectClass) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (OwnershipAcquisitionPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void publishInteractionClass(int theInteraction) throws InteractionClassNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        PublishInteractionClass request = new PublishInteractionClass();

        request.setInteractionClass(theInteraction);

        try {
            PublishInteractionClass response = (PublishInteractionClass) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void unpublishInteractionClass(int theInteraction) throws InteractionClassNotDefined, InteractionClassNotPublished, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        UnpublishInteractionClass request = new UnpublishInteractionClass();

        request.setInteractionClass(theInteraction);

        try {
            UnpublishInteractionClass response = (UnpublishInteractionClass) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeObjectClassAttributes(int theClass, AttributeHandleSet attributeList) throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (attributeList == null || attributeList.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributeList");
        }

        SubscribeObjectClassAttributes request = new SubscribeObjectClassAttributes();

        request.setObjectClass(theClass);
        request.setAttributes(attributeList);

        try {
            SubscribeObjectClassAttributes response = (SubscribeObjectClassAttributes) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeObjectClassAttributesPassively(int theClass, AttributeHandleSet attributeList) throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unsubscribeObjectClass(int theClass) throws ObjectClassNotDefined, ObjectClassNotSubscribed, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        UnsubscribeObjectClass request = new UnsubscribeObjectClass();

        request.setObjectClass(theClass);

        try {
            UnsubscribeObjectClass response = (UnsubscribeObjectClass) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (ObjectClassNotSubscribed ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeInteractionClass(int theClass) throws InteractionClassNotDefined, FederateNotExecutionMember, FederateLoggingServiceCalls, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        SubscribeInteractionClass request = new SubscribeInteractionClass();

        request.setInteractionClass(theClass);

        try {
            SubscribeInteractionClass response = (SubscribeInteractionClass) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (FederateLoggingServiceCalls ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeInteractionClassPassively(int theClass) throws InteractionClassNotDefined, FederateNotExecutionMember, FederateLoggingServiceCalls, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unsubscribeInteractionClass(int theClass) throws InteractionClassNotDefined, InteractionClassNotSubscribed, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        UnsubscribeInteractionClass request = new UnsubscribeInteractionClass();

        request.setInteractionClass(theClass);

        try {
            UnsubscribeInteractionClass response = (UnsubscribeInteractionClass) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotSubscribed ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int registerObjectInstance(int theClass) throws ObjectClassNotDefined, ObjectClassNotPublished, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        RegisterObjectInstance request = new RegisterObjectInstance();

        request.setObjectClass(theClass);

        try {
            RegisterObjectInstance response = (RegisterObjectInstance) processRequest(request);

            return (int) response.getObject();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int registerObjectInstance(int theClass, String theObjectName) throws ObjectClassNotDefined, ObjectClassNotPublished, ObjectAlreadyRegistered, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theObjectName == null || theObjectName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty object name");
        }

        RegisterObjectInstance request = new RegisterObjectInstance();
        request.setObjectClass(theClass);
        request.setName(theObjectName);

        try {
            RegisterObjectInstance response = (RegisterObjectInstance) processRequest(request);
            return (int) response.getObject();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (ObjectAlreadyRegistered ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public void updateAttributeValues(int theObject, SuppliedAttributes theAttributes, byte[] userSuppliedTag) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling updateAttributeValues with Tag NULL");
        }

        UpdateAttributeValues request = new UpdateAttributeValues();
        request.setObject(theObject);
        request.setSuppliedAttributes((CertiHandleValuePairCollection) theAttributes);
        request.setTag(userSuppliedTag);

        try {
            UpdateAttributeValues response = (UpdateAttributeValues) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public EventRetractionHandle updateAttributeValues(int theObject, SuppliedAttributes theAttributes, byte[] userSuppliedTag, LogicalTime theTime) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidFederationTime, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling updateAttributeValues with Tag NULL");
        }

        UpdateAttributeValues request = new UpdateAttributeValues();

        request.setFederationTime((CertiLogicalTime) theTime);

        request.setObject(theObject);
        request.setTag(userSuppliedTag);
        request.setSuppliedAttributes((CertiHandleValuePairCollection) theAttributes);
        request.setIsMessageTimestamped(true);

        try {
            UpdateAttributeValues response = (UpdateAttributeValues) processRequest(request);

            return response.getEventRetraction();
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void sendInteraction(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag) throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theParameters == null || theParameters.size() == 0) {
            throw new RTIinternalError("Incorrect or empty supplied parameters");
        }

        SendInteraction request = new SendInteraction();

        request.setInteractionClass(theInteraction);
        request.setSuppliedParameters((CertiHandleValuePairCollection) theParameters);
        request.setTag(userSuppliedTag);


        try {
            SendInteraction response = (SendInteraction) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public EventRetractionHandle sendInteraction(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag, LogicalTime theTime) throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined, InvalidFederationTime, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theParameters == null || theParameters.size() == 0) {
            throw new RTIinternalError("Incorrect or empty supplied parameters");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling updateAttributeValues with Tag NULL");
        }

        SendInteraction request = new SendInteraction();

        request.setInteractionClass(theInteraction);
        request.setSuppliedParameters((CertiHandleValuePairCollection) theParameters);
        request.setTag(userSuppliedTag);

        request.setRegion(0);
        request.setBooleanValue(true); //With time

        try {
            SendInteraction response = (SendInteraction) processRequest(request);

            return response.getEventRetraction();
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void deleteObjectInstance(int ObjectHandle, byte[] userSuppliedTag) throws ObjectNotKnown, DeletePrivilegeNotHeld, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling updateAttributeValues with Tag NULL");
        }

        DeleteObjectInstance request = new DeleteObjectInstance();

        request.setObject(ObjectHandle);
        request.setTag(userSuppliedTag);
        request.setBooleanValue(false); //No time

        try {
            DeleteObjectInstance response = (DeleteObjectInstance) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (DeletePrivilegeNotHeld ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public EventRetractionHandle deleteObjectInstance(int ObjectHandle, byte[] userSuppliedTag, LogicalTime theTime) throws ObjectNotKnown, DeletePrivilegeNotHeld, InvalidFederationTime, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Tag attribute is NULL");
        }
        if (theTime == null) {
            throw new RTIinternalError("Wrong time attribute");
        }

        DeleteObjectInstance request = new DeleteObjectInstance();

        request.setObject(ObjectHandle);
        request.setFederationTime((CertiLogicalTime) theTime);
        request.setTag(userSuppliedTag);
        request.setBooleanValue(true);//With time

        try {
            DeleteObjectInstance response = (DeleteObjectInstance) processRequest(request);

            return response.getEventRetraction();
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (DeletePrivilegeNotHeld ex) {
            throw ex;
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void localDeleteObjectInstance(int ObjectHandle) throws ObjectNotKnown, FederateOwnsAttributes, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        LocalDeleteObjectInstance request = new LocalDeleteObjectInstance();

        request.setObject(ObjectHandle);

        try {
            LocalDeleteObjectInstance response = (LocalDeleteObjectInstance) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (FederateOwnsAttributes ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void changeAttributeTransportationType(int theObject, AttributeHandleSet theAttributes, int theType) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidTransportationHandle, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        ChangeAttributeTransportationType request = new ChangeAttributeTransportationType();

        request.setObject(theObject);
        request.setAttributes(theAttributes);
        request.setTransport(theType);

        try {
            ChangeAttributeTransportationType response = (ChangeAttributeTransportationType) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (InvalidTransportationHandle ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void changeInteractionTransportationType(int theClass, int theType) throws InteractionClassNotDefined, InteractionClassNotPublished, InvalidTransportationHandle, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        ChangeInteractionTransportationType request = new ChangeInteractionTransportationType();

        request.setInteractionClass(theClass);
        request.setTransport(theType);

        try {
            ChangeInteractionTransportationType response = (ChangeInteractionTransportationType) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InvalidTransportationHandle ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void requestObjectAttributeValueUpdate(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        RequestObjectAttributeValueUpdate request = new RequestObjectAttributeValueUpdate();

        request.setObject(theObject);
        request.setAttributes(theAttributes);

        try {
            RequestObjectAttributeValueUpdate response = (RequestObjectAttributeValueUpdate) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void requestClassAttributeValueUpdate(int theClass, AttributeHandleSet theAttributes) throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        RequestClassAttributeValueUpdate request = new RequestClassAttributeValueUpdate();

        request.setObjectClass(theClass);
        request.setAttributes(theAttributes);

        try {
            RequestClassAttributeValueUpdate response = (RequestClassAttributeValueUpdate) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void unconditionalAttributeOwnershipDivestiture(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        UnconditionalAttributeOwnershipDivestiture request = new UnconditionalAttributeOwnershipDivestiture();

        request.setObject(theObject);
        request.setAttributes(theAttributes);

        try {
            UnconditionalAttributeOwnershipDivestiture response = (UnconditionalAttributeOwnershipDivestiture) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void negotiatedAttributeOwnershipDivestiture(int theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeAlreadyBeingDivested, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Tag attribute is NULL");
        }

        NegotiatedAttributeOwnershipDivestiture request = new NegotiatedAttributeOwnershipDivestiture();

        request.setObject(theObject);
        request.setAttributes(theAttributes);
        request.setTag(userSuppliedTag);

        try {
            NegotiatedAttributeOwnershipDivestiture response = (NegotiatedAttributeOwnershipDivestiture) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (AttributeAlreadyBeingDivested ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void attributeOwnershipAcquisition(int theObject, AttributeHandleSet desiredAttributes, byte[] userSuppliedTag) throws ObjectNotKnown, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished, FederateOwnsAttributes, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (desiredAttributes == null || desiredAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Tag attribute is NULL");
        }

        AttributeOwnershipAcquisition request = new AttributeOwnershipAcquisition();

        request.setObject(theObject);
        request.setAttributes(desiredAttributes);
        request.setTag(userSuppliedTag);

        try {
            AttributeOwnershipAcquisition response = (AttributeOwnershipAcquisition) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotPublished ex) {
            throw ex;
        } catch (FederateOwnsAttributes ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void attributeOwnershipAcquisitionIfAvailable(int theObject, AttributeHandleSet desiredAttributes) throws ObjectNotKnown, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished, FederateOwnsAttributes, AttributeAlreadyBeingAcquired, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (desiredAttributes == null || desiredAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        AttributeOwnershipAcquisitionIfAvailable request = new AttributeOwnershipAcquisitionIfAvailable();

        request.setObject(theObject);
        request.setAttributes(desiredAttributes);

        try {
            AttributeOwnershipAcquisitionIfAvailable response = (AttributeOwnershipAcquisitionIfAvailable) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotPublished ex) {
            throw ex;
        } catch (FederateOwnsAttributes ex) {
            throw ex;
        } catch (AttributeAlreadyBeingAcquired ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public AttributeHandleSet attributeOwnershipReleaseResponse(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, FederateWasNotAskedToReleaseAttribute, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        AttributeOwnershipReleaseResponse request = new AttributeOwnershipReleaseResponse();

        request.setObject(theObject);
        request.setAttributes(theAttributes);

        try {
            AttributeOwnershipReleaseResponse response = (AttributeOwnershipReleaseResponse) processRequest(request);

            return response.getAttributes();
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (FederateWasNotAskedToReleaseAttribute ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public void cancelNegotiatedAttributeOwnershipDivestiture(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeDivestitureWasNotRequested, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        CancelNegotiatedAttributeOwnershipDivestiture request = new CancelNegotiatedAttributeOwnershipDivestiture();

        request.setObject(theObject);
        request.setAttributes(theAttributes);

        try {
            CancelNegotiatedAttributeOwnershipDivestiture response = (CancelNegotiatedAttributeOwnershipDivestiture) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (AttributeDivestitureWasNotRequested ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void cancelAttributeOwnershipAcquisition(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotDefined, AttributeAlreadyOwned, AttributeAcquisitionWasNotRequested, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        CancelAttributeOwnershipAcquisition request = new CancelAttributeOwnershipAcquisition();

        request.setObject(theObject);
        request.setAttributes(theAttributes);

        try {
            CancelAttributeOwnershipAcquisition response = (CancelAttributeOwnershipAcquisition) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeAlreadyOwned ex) {
            throw ex;
        } catch (AttributeAcquisitionWasNotRequested ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void queryAttributeOwnership(int theObject, int theAttribute) throws ObjectNotKnown, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        QueryAttributeOwnership request = new QueryAttributeOwnership();

        request.setObject(theObject);
        request.setAttribute((short) theAttribute);

        try {
            QueryAttributeOwnership response = (QueryAttributeOwnership) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public boolean isAttributeOwnedByFederate(int theObject, int theAttribute) throws ObjectNotKnown, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        IsAttributeOwnedByFederate request = new IsAttributeOwnedByFederate();

        request.setObject(theObject);
        request.setAttribute((short) theAttribute);

        try {
            IsAttributeOwnedByFederate response = (IsAttributeOwnedByFederate) processRequest(request);

            return (new String(response.getTag())).equals("RTI_TRUE"); //TODO Check
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void enableTimeRegulation(LogicalTime theFederateTime, LogicalTimeInterval theLookahead) throws TimeRegulationAlreadyEnabled, EnableTimeRegulationPending, TimeAdvanceAlreadyInProgress, InvalidFederationTime, InvalidLookahead, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theFederateTime == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the federate time");
        }
        if (theLookahead == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the lookahead");
        }

        EnableTimeRegulation request = new EnableTimeRegulation();

        request.setFederationTime((CertiLogicalTime) theFederateTime);

        request.setLookahead(theLookahead);
        request.setBooleanValue(true);


        try {
            EnableTimeRegulation response = (EnableTimeRegulation) processRequest(request);
        } catch (TimeRegulationAlreadyEnabled ex) {
            throw ex;
        } catch (EnableTimeRegulationPending ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (InvalidLookahead ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableTimeRegulation() throws TimeRegulationWasNotEnabled, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        DisableTimeRegulation request = new DisableTimeRegulation();

        request.setBooleanValue(false);

        try {
            DisableTimeRegulation response = (DisableTimeRegulation) processRequest(request);
        } catch (TimeRegulationWasNotEnabled ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void enableTimeConstrained() throws TimeConstrainedAlreadyEnabled, EnableTimeConstrainedPending, TimeAdvanceAlreadyInProgress, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        EnableTimeConstrained request = new EnableTimeConstrained();

        request.setBooleanValue(true);

        try {
            EnableTimeConstrained response = (EnableTimeConstrained) processRequest(request);
        } catch (TimeConstrainedAlreadyEnabled ex) {
            throw ex;
        } catch (EnableTimeConstrainedPending ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableTimeConstrained() throws TimeConstrainedWasNotEnabled, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        DisableTimeConstrained request = new DisableTimeConstrained();

        request.setBooleanValue(false);

        try {
            DisableTimeConstrained response = (DisableTimeConstrained) processRequest(request);
        } catch (TimeConstrainedWasNotEnabled ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void timeAdvanceRequest(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed, TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theTime == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the time");
        }

        TimeAdvanceRequest request = new TimeAdvanceRequest();

        request.setFederationTime((CertiLogicalTime) theTime);

        try {
            TimeAdvanceRequest response = (TimeAdvanceRequest) processRequest(request);
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederationTimeAlreadyPassed ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (EnableTimeRegulationPending ex) {
            throw ex;
        } catch (EnableTimeConstrainedPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void timeAdvanceRequestAvailable(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed, TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theTime == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the time");
        }

        TimeAdvanceRequestAvailable request = new TimeAdvanceRequestAvailable();

        request.setFederationTime((CertiLogicalTime) theTime);

        try {
            TimeAdvanceRequestAvailable response = (TimeAdvanceRequestAvailable) processRequest(request);
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederationTimeAlreadyPassed ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (EnableTimeRegulationPending ex) {
            throw ex;
        } catch (EnableTimeConstrainedPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void nextEventRequest(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed, TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theTime == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the time");
        }

        NextEventRequest request = new NextEventRequest();

        request.setFederationTime((CertiLogicalTime) theTime);

        try {
            NextEventRequest response = (NextEventRequest) processRequest(request);
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederationTimeAlreadyPassed ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (EnableTimeRegulationPending ex) {
            throw ex;
        } catch (EnableTimeConstrainedPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void nextEventRequestAvailable(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed, TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theTime == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the time");
        }

        NextEventRequestAvailable request = new NextEventRequestAvailable();

        request.setFederationTime((CertiLogicalTime) theTime);

        try {
            NextEventRequestAvailable response = (NextEventRequestAvailable) processRequest(request);
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederationTimeAlreadyPassed ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (EnableTimeRegulationPending ex) {
            throw ex;
        } catch (EnableTimeConstrainedPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void flushQueueRequest(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed, TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theTime == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the time");
        }

        FlushQueueRequest request = new FlushQueueRequest();

        request.setFederationTime((CertiLogicalTime) theTime);

        try {
            FlushQueueRequest response = (FlushQueueRequest) processRequest(request);
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (FederationTimeAlreadyPassed ex) {
            throw ex;
        } catch (TimeAdvanceAlreadyInProgress ex) {
            throw ex;
        } catch (EnableTimeRegulationPending ex) {
            throw ex;
        } catch (EnableTimeConstrainedPending ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        EnableAsynchronousDelivery request = new EnableAsynchronousDelivery();
        try {
            EnableAsynchronousDelivery response = (EnableAsynchronousDelivery) processRequest(request);
        } catch (AsynchronousDeliveryAlreadyEnabled ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        DisableAsynchronousDelivery request = new DisableAsynchronousDelivery();
        try {
            DisableAsynchronousDelivery response = (DisableAsynchronousDelivery) processRequest(request);
        } catch (AsynchronousDeliveryAlreadyDisabled ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public LogicalTime queryLBTS() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        QueryLbts request = new QueryLbts();
        try {
            QueryLbts response = (QueryLbts) processRequest(request);

            return response.getFederationTime();
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public LogicalTime queryFederateTime() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        QueryFederateTime request = new QueryFederateTime();

        try {
            QueryFederateTime response = (QueryFederateTime) processRequest(request);

            return response.getFederationTime();
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public LogicalTime queryMinNextEventTime() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        QueryMinNextEventTime request = new QueryMinNextEventTime();
        try {
            QueryMinNextEventTime response = (QueryMinNextEventTime) processRequest(request);

            return response.getFederationTime();
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public void modifyLookahead(LogicalTimeInterval theLookahead) throws InvalidLookahead, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theLookahead == null) {
            throw new RTIinternalError("Incorrect supplied parameter - the lookahed");
        }

        ModifyLookahead request = new ModifyLookahead();

        request.setLookahead(theLookahead);

        try {
            ModifyLookahead response = (ModifyLookahead) processRequest(request);
        } catch (InvalidLookahead ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public LogicalTimeInterval queryLookahead() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        QueryLookahead request = new QueryLookahead();
        try {
            QueryLookahead response = (QueryLookahead) processRequest(request);

            return response.getLookahead();
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public void retract(EventRetractionHandle theHandle) throws InvalidRetractionHandle, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        throw new UnsupportedOperationException("Unimplemented Service retract");
    }

    public void changeAttributeOrderType(int theObject, AttributeHandleSet theAttributes, int theType) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidOrderingHandle, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }

        ChangeAttributeOrderType request = new ChangeAttributeOrderType();

        request.setObject(theObject);
        request.setOrder(theType);
        request.setAttributes(theAttributes);

        try {
            ChangeAttributeOrderType response = (ChangeAttributeOrderType) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotOwned ex) {
            throw ex;
        } catch (InvalidOrderingHandle ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void changeInteractionOrderType(int theClass, int theType) throws InteractionClassNotDefined, InteractionClassNotPublished, InvalidOrderingHandle, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        ChangeInteractionOrderType request = new ChangeInteractionOrderType();

        request.setInteractionClass(theClass);
        request.setOrder(theType);

        try {
            ChangeInteractionOrderType response = (ChangeInteractionOrderType) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InvalidOrderingHandle ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public Region createRegion(int spaceHandle, int numberOfExtents) throws SpaceNotDefined, InvalidExtents, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        DdmCreateRegion request = new DdmCreateRegion();

        request.setSpace(spaceHandle);
        request.setNumber(numberOfExtents);

        try {
            DdmCreateRegion response = (DdmCreateRegion) processRequest(request);

            return new CertiRegion(response.getRegion(), (int) response.getSpace(), numberOfExtents);
        } catch (SpaceNotDefined ex) {
            throw ex;
        } catch (InvalidExtents ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void notifyOfRegionModification(Region modifiedRegionInstance) throws RegionNotKnown, InvalidExtents, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (modifiedRegionInstance == null) {
            throw new RTIinternalError("Incorrect supplied parameter - modified region instance");
        }

        DdmModifyRegion request = new DdmModifyRegion();

        request.setRegion(((CertiRegion) modifiedRegionInstance).getHandle());
        request.setExtents(((CertiRegion) modifiedRegionInstance).getExtents());

        try {
            DdmModifyRegion response = (DdmModifyRegion) processRequest(request);

//TODO region.commit(); ->effectiveExtends
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidExtents ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void deleteRegion(Region theRegion) throws RegionNotKnown, RegionInUse, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmDeleteRegion request = new DdmDeleteRegion();

        request.setRegion(((CertiRegion) theRegion).getHandle());
        try {
            DdmDeleteRegion response = (DdmDeleteRegion) processRequest(request);
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (RegionInUse ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int registerObjectInstanceWithRegion(int theClass, int[] theAttributes, Region[] theRegions) throws ObjectClassNotDefined, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theAttributes == null || theAttributes.length==0) {
            throw new RTIinternalError("Incorrect supplied parameter - the attributes");
        }
        if (theRegions == null || theRegions.length == 0) {
            throw new RTIinternalError("Incorrect supplied parameter - regions (null or empty)");
        }

        DdmRegisterObject request = new DdmRegisterObject();

        request.setObjectClass(theClass);
//TODO Convert attributes
        request.setAttributes(null);
//TODO Convert regions
        request.setRegions(null);


        try {
            DdmRegisterObject response = (DdmRegisterObject) processRequest(request);

            return (int) response.getObject();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotPublished ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int registerObjectInstanceWithRegion(int theClass, String theObject, int[] theAttributes, Region[] theRegions) throws ObjectClassNotDefined, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished, RegionNotKnown, InvalidRegionContext, ObjectAlreadyRegistered, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
    if (theAttributes == null || theAttributes.length==0) {
            throw new RTIinternalError("Incorrect supplied parameter - the attributes");
        }
        if (theRegions == null) {
            throw new RTIinternalError("Incorrect supplied parameter - regions");
        }

        DdmRegisterObject request = new DdmRegisterObject();

        request.setObjectClass(theClass);
        request.setTag(theObject.getBytes());
//TODO Convert attributes
        request.setAttributes(null);
//TODO Convert regions
        request.setRegions(null);

        try {
            DdmRegisterObject response = (DdmRegisterObject) processRequest(request);

            return (int) response.getObject();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (AttributeNotPublished ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (ObjectAlreadyRegistered ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void associateRegionForUpdates(Region theRegion, int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotDefined, InvalidRegionContext, RegionNotKnown, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmAssociateRegion request = new DdmAssociateRegion();
        request.setObject(theObject);
        request.setRegion(((CertiRegion) theRegion).getHandle());
        request.setAttributes(theAttributes);

        try {
            DdmAssociateRegion response = (DdmAssociateRegion) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void unassociateRegionForUpdates(Region theRegion, int theObject) throws ObjectNotKnown, InvalidRegionContext, RegionNotKnown, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmUnassociateRegion request = new DdmUnassociateRegion();

        request.setObject(theObject);
        request.setRegion(((CertiRegion) theRegion).getHandle());

        try {
            DdmUnassociateRegion response = (DdmUnassociateRegion) processRequest(request);
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeObjectClassAttributesWithRegion(int theClass, Region theRegion, AttributeHandleSet attributeList) throws ObjectClassNotDefined, AttributeNotDefined, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmSubscribeAttributes request = new DdmSubscribeAttributes();

        request.setObjectClass(theClass);
        request.setRegion(((CertiRegion) theRegion).getHandle());
        request.setAttributes(attributeList);
        request.setBooleanValue(false); //Not passive

        try {
            DdmSubscribeAttributes response = (DdmSubscribeAttributes) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeObjectClassAttributesPassivelyWithRegion(int theClass, Region theRegion, AttributeHandleSet attributeList) throws ObjectClassNotDefined, AttributeNotDefined, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }
        if (attributeList == null || attributeList.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributeList parameter");
        }

        DdmSubscribeAttributes request = new DdmSubscribeAttributes();

        request.setObjectClass(theClass);
        request.setRegion(((CertiRegion) theRegion).getHandle());
        request.setAttributes(attributeList);
        request.setBooleanValue(true); //Passive

        try {
            DdmSubscribeAttributes response = (DdmSubscribeAttributes) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void unsubscribeObjectClassWithRegion(int theClass, Region theRegion) throws ObjectClassNotDefined, RegionNotKnown, FederateNotSubscribed, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmUnsubscribeAttributes request = new DdmUnsubscribeAttributes();

        request.setObjectClass(theClass);
        request.setRegion(((CertiRegion) theRegion).getHandle());

        try {
            DdmUnsubscribeAttributes response = (DdmUnsubscribeAttributes) processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } //TODO catch (FederateNotSubscribed ex) {            throw ex;        }
        catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeInteractionClassWithRegion(int theClass, Region theRegion) throws InteractionClassNotDefined, RegionNotKnown, InvalidRegionContext, FederateLoggingServiceCalls, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmSubscribeInteraction request = new DdmSubscribeInteraction();

        request.setInteractionClass(theClass);
        request.setRegion(((CertiRegion) theRegion).getHandle());
        request.setBooleanValue(false); //Not passive

        try {
            DdmSubscribeInteraction response = (DdmSubscribeInteraction) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateLoggingServiceCalls ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void subscribeInteractionClassPassivelyWithRegion(int theClass, Region theRegion) throws InteractionClassNotDefined, RegionNotKnown, InvalidRegionContext, FederateLoggingServiceCalls, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect supplied parameter - region");
        }

        DdmSubscribeInteraction request = new DdmSubscribeInteraction();

        request.setInteractionClass(theClass);
        request.setRegion(((CertiRegion) theRegion).getHandle());
        request.setBooleanValue(true); //Passive

        try {
            DdmSubscribeInteraction response = (DdmSubscribeInteraction) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateLoggingServiceCalls ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void unsubscribeInteractionClassWithRegion(int theClass, Region theRegion) throws InteractionClassNotDefined, InteractionClassNotSubscribed, RegionNotKnown, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        DdmUnsubscribeInteraction request = new DdmUnsubscribeInteraction();

        request.setInteractionClass(theClass);
        request.setRegion(((CertiRegion) theRegion).getHandle());

        try {
            DdmUnsubscribeInteraction response = (DdmUnsubscribeInteraction) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotSubscribed ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void sendInteractionWithRegion(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag, Region theRegion) throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        SendInteraction request = new SendInteraction();

        request.setInteractionClass(theInteraction);
        request.setSuppliedParameters((CertiHandleValuePairCollection) theParameters);
        request.setTag(userSuppliedTag);
        request.setRegion(((CertiRegion) theRegion).getHandle());

        try {
            SendInteraction response = (SendInteraction) processRequest(request);
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public EventRetractionHandle sendInteractionWithRegion(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag, Region theRegion, LogicalTime theTime) throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined, InvalidFederationTime, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        if (theParameters == null || theParameters.size() == 0) {
            throw new RTIinternalError("Incorrect or empty supplied parameters");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Tag is NULL");
        }
        if (theRegion == null) {
            throw new RTIinternalError("Incorrect region parameter");
        }
        if (theTime == null) {
            throw new RTIinternalError("Incorrect time parameter");
        }

        SendInteraction request = new SendInteraction();

        request.setInteractionClass(theInteraction);
        request.setSuppliedParameters((CertiHandleValuePairCollection) theParameters);
        request.setFederationTime((CertiLogicalTime) theTime);
        request.setTag(userSuppliedTag);
        request.setRegion(((CertiRegion) theRegion).getHandle());

        try {
            SendInteraction response = (SendInteraction) processRequest(request);

            return response.getEventRetraction();
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (InvalidFederationTime ex) {
            throw ex;
        } catch (RegionNotKnown ex) {
            throw ex;
        } catch (InvalidRegionContext ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (ConcurrentAccessAttempted ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void requestClassAttributeValueUpdateWithRegion(int theClass, AttributeHandleSet theAttributes, Region theRegion) throws ObjectClassNotDefined, AttributeNotDefined, RegionNotKnown, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted {
        throw new RTIinternalError("unimplemented service requestClassAttributeValueUpdateWithRegion");
    }

    public int getObjectClassHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty object class name");
        }

        GetObjectClassHandle request = new GetObjectClassHandle();
        request.setName(theName);

        try {
            GetObjectClassHandle response = (GetObjectClassHandle) processRequest(request);
            return (int) response.getObjectClass(); //hack handles maju rozdielne velkosti
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public String getObjectClassName(int theHandle) throws ObjectClassNotDefined, FederateNotExecutionMember, RTIinternalError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getAttributeHandle(String theName, int whichClass) throws ObjectClassNotDefined, NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetAttributeHandle request = new GetAttributeHandle();
        request.setName(theName);
        request.setObjectClass(whichClass);

        try {
            GetAttributeHandle response = (GetAttributeHandle) processRequest(request);
            return response.getAttribute();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public String getAttributeName(int theHandle, int whichClass) throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetAttributeName request = new GetAttributeName();

        request.setAttribute((short) theHandle);
        request.setObjectClass(whichClass);

        try {
            GetAttributeName response = (GetAttributeName) processRequest(request);

            return response.getName();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int getInteractionClassHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetInteractionClassHandle request = new GetInteractionClassHandle();

        request.setName(theName);

        try {
            GetInteractionClassHandle response = (GetInteractionClassHandle) processRequest(request);

            return (int) response.getInteractionClass();
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public String getInteractionClassName(int theHandle) throws InteractionClassNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetInteractionClassName request = new GetInteractionClassName();

        request.setInteractionClass(theHandle);

        try {
            GetInteractionClassName response = (GetInteractionClassName) processRequest(request);

            return response.getName();
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public int getParameterHandle(String theName, int whichClass) throws InteractionClassNotDefined, NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetParameterHandle request = new GetParameterHandle();

        request.setName(theName);
        request.setInteractionClass(whichClass);

        try {
            GetParameterHandle response = (GetParameterHandle) processRequest(request);

            return response.getParameter();
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public String getParameterName(int theHandle, int whichClass) throws InteractionClassNotDefined, InteractionParameterNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetParameterName request = new GetParameterName();

        request.setParameter((short) theHandle);
        request.setInteractionClass(whichClass);

        try {
            GetParameterName response = (GetParameterName) processRequest(request);

            return response.getName();
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int getObjectInstanceHandle(String theName) throws ObjectNotKnown, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetObjectInstanceHandle request = new GetObjectInstanceHandle();

        request.setName(theName);

        try {
            GetObjectInstanceHandle response = (GetObjectInstanceHandle) processRequest(request);

            return (int) response.getObject();
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public String getObjectInstanceName(int theHandle) throws ObjectNotKnown, FederateNotExecutionMember, RTIinternalError {
        GetObjectInstanceName request = new GetObjectInstanceName();

        request.setObject(theHandle);

        try {
            GetObjectInstanceName response = (GetObjectInstanceName) processRequest(request);

            return response.getName();
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public int getRoutingSpaceHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetSpaceHandle request = new GetSpaceHandle();

        request.setName(theName);

        try {
            GetSpaceHandle response = (GetSpaceHandle) processRequest(request);

            return (int) response.getSpace();
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public String getRoutingSpaceName(int theHandle) throws SpaceNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetSpaceName request = new GetSpaceName();

        request.setSpace(theHandle);

        try {
            GetSpaceName response = (GetSpaceName) processRequest(request);

            return response.getName();
        } catch (SpaceNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public int getDimensionHandle(String theName, int whichSpace) throws SpaceNotDefined, NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetDimensionHandle request = new GetDimensionHandle();

        request.setName(theName);
        request.setDimension(whichSpace);

        try {
            GetDimensionHandle response = (GetDimensionHandle) processRequest(request);

            return (int) response.getDimension();
        } catch (SpaceNotDefined ex) {
            throw ex;
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public String getDimensionName(int theHandle, int whichClass) throws SpaceNotDefined, DimensionNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetDimensionName request = new GetDimensionName();

        request.setDimension(theHandle);
        request.setSpace(whichClass);

        try {
            GetDimensionName response = (GetDimensionName) processRequest(request);

            return response.getName();
        } catch (SpaceNotDefined ex) {
            throw ex;
        } catch (DimensionNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public int getAttributeRoutingSpaceHandle(int theHandle, int whichClass) throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetAttributeSpaceHandle request = new GetAttributeSpaceHandle();

        request.setAttribute(theHandle);
        request.setObjectClass(whichClass);

        try {
            GetAttributeSpaceHandle response = (GetAttributeSpaceHandle) processRequest(request);

            return (int) response.getSpace();
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (AttributeNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public int getObjectClass(int theObject) throws ObjectNotKnown, FederateNotExecutionMember, RTIinternalError {
        GetObjectClass request = new GetObjectClass();

        request.setObject(theObject);

        try {
            GetObjectClass response = (GetObjectClass) processRequest(request);

            return (int) response.getObjectClass();
        } catch (ObjectNotKnown ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }


    }

    public int getInteractionRoutingSpaceHandle(int theHandle) throws InteractionClassNotDefined, FederateNotExecutionMember, RTIinternalError {
        GetInteractionSpaceHandle request = new GetInteractionSpaceHandle();

        request.setInteractionClass(theHandle);

        try {
            GetInteractionSpaceHandle response = (GetInteractionSpaceHandle) processRequest(request);

            return (int) response.getSpace();
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public int getTransportationHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError {
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetTransportationHandle request = new GetTransportationHandle();

        request.setName(theName);

        try {
            GetTransportationHandle response = (GetTransportationHandle) processRequest(request);

            return (int) response.getTransport();
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public String getTransportationName(int theHandle) throws InvalidTransportationHandle, FederateNotExecutionMember, RTIinternalError {
        GetTransportationName request = new GetTransportationName();

        request.setTransport(theHandle);

        try {
            GetTransportationName response = (GetTransportationName) processRequest(request);

            return response.getName();
        } catch (InvalidTransportationHandle ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public int getOrderingHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError {
        GetOrderingHandle request = new GetOrderingHandle();

        request.setName(theName);

        try {
            GetOrderingHandle response = (GetOrderingHandle) processRequest(request);

            return (int) response.getOrder();
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public String getOrderingName(int theHandle) throws InvalidOrderingHandle, FederateNotExecutionMember, RTIinternalError {
        GetOrderingName request = new GetOrderingName();

        request.setOrder(theHandle);

        try {
            GetOrderingName response = (GetOrderingName) processRequest(request);

            return response.getName();
        } catch (InvalidOrderingHandle ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    public void enableClassRelevanceAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        EnableClassRelevanceAdvisorySwitch request = new EnableClassRelevanceAdvisorySwitch();
        try {
            EnableClassRelevanceAdvisorySwitch response = (EnableClassRelevanceAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableClassRelevanceAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        DisableClassRelevanceAdvisorySwitch request = new DisableClassRelevanceAdvisorySwitch();
        try {
            DisableClassRelevanceAdvisorySwitch response = (DisableClassRelevanceAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void enableAttributeRelevanceAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        EnableAttributeRelevanceAdvisorySwitch request = new EnableAttributeRelevanceAdvisorySwitch();
        try {
            EnableAttributeRelevanceAdvisorySwitch response = (EnableAttributeRelevanceAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableAttributeRelevanceAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        DisableAttributeRelevanceAdvisorySwitch request = new DisableAttributeRelevanceAdvisorySwitch();
        try {
            DisableAttributeRelevanceAdvisorySwitch response = (DisableAttributeRelevanceAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void enableAttributeScopeAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        EnableAttributeScopeAdvisorySwitch request = new EnableAttributeScopeAdvisorySwitch();
        try {
            EnableAttributeScopeAdvisorySwitch response = (EnableAttributeScopeAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableAttributeScopeAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        DisableAttributeScopeAdvisorySwitch request = new DisableAttributeScopeAdvisorySwitch();
        try {
            DisableAttributeScopeAdvisorySwitch response = (DisableAttributeScopeAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void enableInteractionRelevanceAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        EnableInteractionRelevanceAdvisorySwitch request = new EnableInteractionRelevanceAdvisorySwitch();
        try {
            EnableInteractionRelevanceAdvisorySwitch response = (EnableInteractionRelevanceAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public void disableInteractionRelevanceAdvisorySwitch() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError {
        DisableInteractionRelevanceAdvisorySwitch request = new DisableInteractionRelevanceAdvisorySwitch();
        try {
            DisableInteractionRelevanceAdvisorySwitch response = (DisableInteractionRelevanceAdvisorySwitch) processRequest(request);
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    public Region getRegion(int regionToken) throws FederateNotExecutionMember, ConcurrentAccessAttempted, RegionNotKnown, RTIinternalError {
        throw new UnsupportedOperationException("unimplemented service getRegion");
    }

    public int getRegionToken(Region region) throws FederateNotExecutionMember, ConcurrentAccessAttempted, RegionNotKnown, RTIinternalError {
        return region.getSpaceHandle();
    }

    public void tick() throws RTIinternalError, ConcurrentAccessAttempted {
        try {
            tickKernel(true, 0.0, Double.MAX_VALUE);
        } catch (SpecifiedSaveLabelDoesNotExist ex) {
            throw new RTIinternalError("SpecifiedSaveLabelDoesNotExist in tick");
        }
    }

    private CertiMessage processRequest(CertiMessage request) throws ArrayIndexOutOfBounds, AsynchronousDeliveryAlreadyEnabled, AsynchronousDeliveryAlreadyDisabled, AttributeAlreadyOwned, AttributeAlreadyBeingAcquired, AttributeAlreadyBeingDivested, AttributeDivestitureWasNotRequested, AttributeAcquisitionWasNotRequested, AttributeNotDefined, AttributeNotKnown, AttributeNotOwned, AttributeNotPublished, RTIinternalError, ConcurrentAccessAttempted, CouldNotDiscover, CouldNotOpenFED, CouldNotRestore, DeletePrivilegeNotHeld, ErrorReadingFED, EventNotKnown, FederateAlreadyExecutionMember, FederateInternalError, FederateNotExecutionMember, FederateOwnsAttributes, FederatesCurrentlyJoined, FederateWasNotAskedToReleaseAttribute, FederationExecutionAlreadyExists, FederationExecutionDoesNotExist, FederationTimeAlreadyPassed, RegionNotKnown, InteractionClassNotDefined, InteractionClassNotKnown, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionParameterNotKnown, InvalidExtents, InvalidFederationTime, InvalidLookahead, InvalidOrderingHandle, InvalidResignAction, InvalidRetractionHandle, InvalidTransportationHandle, NameNotFound, ObjectClassNotDefined, ObjectClassNotKnown, ObjectClassNotPublished, ObjectClassNotSubscribed, ObjectNotKnown, ObjectAlreadyRegistered, RestoreInProgress, RestoreNotRequested, SpaceNotDefined, SaveInProgress, SaveNotInitiated, SpecifiedSaveLabelDoesNotExist, TimeAdvanceAlreadyInProgress, TimeAdvanceWasNotInProgress, UnableToPerformSave, DimensionNotDefined, OwnershipAcquisitionPending, FederateLoggingServiceCalls, InteractionClassNotSubscribed, EnableTimeRegulationPending, TimeRegulationAlreadyEnabled, TimeRegulationWasNotEnabled, TimeConstrainedWasNotEnabled, EnableTimeConstrainedPending, TimeConstrainedAlreadyEnabled, RegionInUse, InvalidRegionContext {
        request.writeMessage(this.messageBuffer);

        try {
            LOGGER.info("Sending message (" + request.toString() + ")");
            this.messageBuffer.send(this.socket.getOutputStream());
        } catch (IOException ex) {
            LOGGER.severe("libRTI: exception: NetworkError (write)");
            throw new RTIinternalError("libRTI: Network Write Error");
        }

        CertiMessage response = null;
        try {
            InputStream in = this.socket.getInputStream();

            response = this.messageBuffer.receive(in);
            LOGGER.info("Received message (" + response.toString() + ")\n");

            if (request.getType() != (response.getType())) {
                throw new RTIinternalError("RTI Ambassador Process request: request type != response type");
            }
        } catch (IOException ex) {
            LOGGER.severe("libRTI: exception: NetworkError (read)");
            throw new RTIinternalError("libRTI: Network Read Error");
        } catch (CertiException ex) {
            translateException(ex);
        }

        return response;
    }

    private void translateException(CertiException ex) throws ArrayIndexOutOfBounds, AsynchronousDeliveryAlreadyEnabled, AsynchronousDeliveryAlreadyDisabled, AttributeAlreadyOwned, AttributeAlreadyBeingAcquired, AttributeAlreadyBeingDivested, AttributeDivestitureWasNotRequested, AttributeAcquisitionWasNotRequested, AttributeNotDefined, AttributeNotKnown, AttributeNotOwned, AttributeNotPublished, RTIinternalError, ConcurrentAccessAttempted, CouldNotDiscover, CouldNotOpenFED, CouldNotRestore, DeletePrivilegeNotHeld, ErrorReadingFED, EventNotKnown, FederateAlreadyExecutionMember, FederateInternalError, FederateNotExecutionMember, FederateOwnsAttributes, FederatesCurrentlyJoined, FederateWasNotAskedToReleaseAttribute, FederationExecutionAlreadyExists, FederationExecutionDoesNotExist, FederationTimeAlreadyPassed, RegionNotKnown, InteractionClassNotDefined, InteractionClassNotKnown, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionParameterNotKnown, InvalidExtents, InvalidFederationTime, InvalidLookahead, InvalidOrderingHandle, InvalidResignAction, InvalidRetractionHandle, InvalidTransportationHandle, NameNotFound, ObjectClassNotDefined, ObjectClassNotKnown, ObjectClassNotPublished, ObjectClassNotSubscribed, ObjectNotKnown, ObjectAlreadyRegistered, RestoreInProgress, RestoreNotRequested, SpaceNotDefined, SaveInProgress, SaveNotInitiated, SpecifiedSaveLabelDoesNotExist, TimeAdvanceAlreadyInProgress, TimeAdvanceWasNotInProgress, UnableToPerformSave, DimensionNotDefined, OwnershipAcquisitionPending, FederateLoggingServiceCalls, InteractionClassNotSubscribed, EnableTimeRegulationPending, TimeRegulationAlreadyEnabled, TimeRegulationWasNotEnabled, TimeConstrainedWasNotEnabled, EnableTimeConstrainedPending, TimeConstrainedAlreadyEnabled, RegionInUse, InvalidRegionContext {
        switch (ex.getExceptionType()) {
            case NoException:
                break;
            case ArrayIndexOutOfBounds: {
                LOGGER.warning("Throwing ArrayIndexOutOfBounds exception.");
                throw new ArrayIndexOutOfBounds(ex.getReason());
            }
            case AsynchronousDeliveryAlreadyEnabled: {
                LOGGER.warning("Throwing AsynchronousDeliveryAlreadyEnabled exception.");
                throw new AsynchronousDeliveryAlreadyEnabled(ex.getReason());
            }
            case AsynchronousDeliveryAlreadyDisabled: {
                LOGGER.warning("Throwing AsynchronousDeliveryAlreadyDisabled exception.");
                throw new AsynchronousDeliveryAlreadyDisabled(ex.getReason());
            }
            case AttributeAlreadyOwned: {
                LOGGER.warning("Throwing AttributeAlreadyOwned exception.");
                throw new AttributeAlreadyOwned(ex.getReason());
            }
            case AttributeAlreadyBeingAcquired: {
                LOGGER.warning("Throwing AttributeAlreadyBeingAcquired exception.");
                throw new AttributeAlreadyBeingAcquired(ex.getReason());
            }
            case AttributeAlreadyBeingDivested: {
                LOGGER.warning("Throwing AttributeAlreadyBeingDivested exception.");
                throw new AttributeAlreadyBeingDivested(ex.getReason());
            }
            case AttributeDivestitureWasNotRequested: {
                LOGGER.warning("Throwing AttributeDivestitureWasNotRequested exception.");
                throw new AttributeDivestitureWasNotRequested(ex.getReason());
            }
            case AttributeAcquisitionWasNotRequested: {
                LOGGER.warning("Throwing AttributeAcquisitionWasNotRequested exception.");
                throw new AttributeAcquisitionWasNotRequested(ex.getReason());
            }
            case AttributeNotDefined: {
                LOGGER.warning("Throwing AttributeNotDefined exception.");
                throw new AttributeNotDefined(ex.getReason());
            }
            case AttributeNotKnown: {
                LOGGER.warning("Throwing AttributeNotKnown exception.");
                throw new AttributeNotKnown(ex.getReason());
            }
            case AttributeNotOwned: {
                LOGGER.warning("Throwing AttributeNotOwned exception.");
                throw new AttributeNotOwned(ex.getReason());
            }
            case AttributeNotPublished: {
                LOGGER.warning("Throwing AttributeNotPublished exception.");
                throw new AttributeNotPublished(ex.getReason());
            }
            case AttributeNotSubscribed: {
                LOGGER.warning("Throwing AttributeNotSubscribed exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case ConcurrentAccessAttempted: {
                LOGGER.warning("Throwing ConcurrentAccessAttempted exception.");
                throw new ConcurrentAccessAttempted(ex.getReason());
            }
            case CouldNotDiscover: {
                LOGGER.warning("Throwing CouldNotDiscover exception.");
                throw new CouldNotDiscover(ex.getReason());
            }
            case CouldNotOpenFED: {
                LOGGER.warning("Throwing CouldNotOpenFED exception.");
                throw new CouldNotOpenFED(ex.getReason());
            }
            case CouldNotOpenRID: {
                LOGGER.warning("Throwing CouldNotOpenRID exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case CouldNotRestore: {
                LOGGER.warning("Throwing CouldNotRestore exception.");
                throw new CouldNotRestore(ex.getReason());
            }
            case DeletePrivilegeNotHeld: {
                LOGGER.warning("Throwing DeletePrivilegeNotHeld exception.");
                throw new DeletePrivilegeNotHeld(ex.getReason());
            }
            case ErrorReadingRID: {
                LOGGER.warning("Throwing ErrorReadingRID exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case ErrorReadingFED: {
                LOGGER.warning("Throwing ErrorReadingFED exception.");
                throw new ErrorReadingFED(ex.getReason());
            }
            case EventNotKnown: {
                LOGGER.warning("Throwing EventNotKnown exception.");
                throw new EventNotKnown(ex.getReason());
            }
            case FederateAlreadyPaused: {
                LOGGER.warning("Throwing FederateAlreadyPaused exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederateAlreadyExecutionMember: {
                LOGGER.warning("Throwing FederateAlreadyExecutionMember exception.");
                throw new FederateAlreadyExecutionMember(ex.getReason());
            }
            case FederateDoesNotExist: {
                LOGGER.warning("Throwing FederateDoesNotExist exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederateInternalError: {
                LOGGER.warning("Throwing FederateInternalError exception.");
                throw new FederateInternalError(ex.getReason());
            }
            case FederateNameAlreadyInUse: {
                LOGGER.warning("Throwing FederateNameAlreadyInUse exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederateNotExecutionMember: {
                LOGGER.warning("Throwing FederateNotExecutionMember exception.");
                throw new FederateNotExecutionMember(ex.getReason());
            }
            case FederateNotPaused: {
                LOGGER.warning("Throwing FederateNotPaused exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederateOwnsAttributes: {
                LOGGER.warning("Throwing FederateOwnsAttributes exception.");
                throw new FederateOwnsAttributes(ex.getReason());
            }
            case FederatesCurrentlyJoined: {
                LOGGER.warning("Throwing FederatesCurrentlyJoined exception.");
                throw new FederatesCurrentlyJoined(ex.getReason());
            }
            case FederateWasNotAskedToReleaseAttribute: {
                LOGGER.warning("Throwing FederateWasNotAskedToReleaseAttribute exception.");
                throw new FederateWasNotAskedToReleaseAttribute(ex.getReason());
            }
            case FederationAlreadyPaused: {
                LOGGER.warning("Throwing FederationAlreadyPaused exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederationExecutionAlreadyExists:
                LOGGER.warning("Throwing FederationExecutionAlreadyExists excep.");
                throw new FederationExecutionAlreadyExists(ex.getReason());
            case FederationExecutionDoesNotExist: {
                LOGGER.warning("Throwing FederationExecutionDoesNotExist except.");
                throw new FederationExecutionDoesNotExist(ex.getReason());
            }
            case FederationNotPaused: {
                LOGGER.warning("Throwing FederationNotPaused exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederationTimeAlreadyPassed: {
                LOGGER.warning("Throwing FederationTimeAlreadyPassed exception.");
                throw new FederationTimeAlreadyPassed(ex.getReason());
            }
            case FederateNotPublishing: {
                LOGGER.warning("Throwing FederateNotPublishing exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case FederateNotSubscribing: {
                LOGGER.warning("Throwing FederateNotSubscribing exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case RegionNotKnown: {
                LOGGER.warning("Throwing RegionNotKnown exception.");
                throw new RegionNotKnown(ex.getReason());
            }
            case IDsupplyExhausted: {
                LOGGER.warning("Throwing IDsupplyExhausted exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case InteractionClassNotDefined: {
                LOGGER.warning("Throwing InteractionClassNotDefined exception.");
                throw new InteractionClassNotDefined(ex.getReason());
            }
            case InteractionClassNotKnown: {
                LOGGER.warning("Throwing InteractionClassNotKnown exception.");
                throw new InteractionClassNotKnown(ex.getReason());
            }
            case InteractionClassNotPublished: {
                LOGGER.warning("Throwing InteractionClassNotPublished exception.");
                throw new InteractionClassNotPublished(ex.getReason());
            }
            case InteractionParameterNotDefined: {
                LOGGER.warning("Throwing InteractionParameterNotDefined exception.");
                throw new InteractionParameterNotDefined(ex.getReason());
            }
            case InteractionParameterNotKnown: {
                LOGGER.warning("Throwing InteractionParameterNotKnown exception.");
                throw new InteractionParameterNotKnown(ex.getReason());
            }
            case InvalidDivestitureCondition: {
                LOGGER.warning("Throwing InvalidDivestitureCondition exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case InvalidExtents: {
                LOGGER.warning("Throwing InvalidExtents exception.");
                throw new InvalidExtents(ex.getReason());
            }
            case InvalidFederationTime: {
                LOGGER.warning("Throwing InvalidFederationTime exception.");
                throw new InvalidFederationTime(ex.getReason());
            }
            case InvalidFederationTimeDelta: {
                LOGGER.warning("Throwing InvalidFederationTimeDelta exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case InvalidLookahead: {
                LOGGER.warning("Throwing InvalidLookahead.");
                throw new InvalidLookahead(ex.getReason());
            }
            case InvalidObjectHandle: {
                LOGGER.warning("Throwing InvalidObjectHandle exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case InvalidOrderingHandle: {
                LOGGER.warning("Throwing InvalidOrderingHandle exception.");
                throw new InvalidOrderingHandle(ex.getReason());
            }
            case InvalidResignAction: {
                LOGGER.warning("Throwing InvalidResignAction exception.");
                throw new InvalidResignAction(ex.getReason());
            }
            case InvalidRetractionHandle: {
                LOGGER.warning("Throwing InvalidRetractionHandle exception.");
                throw new InvalidRetractionHandle(ex.getReason());
            }
            case InvalidRoutingSpace: {
                LOGGER.warning("Throwing InvalidRoutingSpace exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case InvalidTransportationHandle: {
                LOGGER.warning("Throwing InvalidTransportationHandle exception.");
                throw new InvalidTransportationHandle(ex.getReason());
            }
            case MemoryExhausted: {
                LOGGER.warning("Throwing MemoryExhausted exception.");
                throw new RTIinternalError("Memory Exhausted: " + ex.getReason());
            }
            case NameNotFound: {
                LOGGER.warning("Throwing NameNotFound exception.");
                throw new NameNotFound(ex.getReason());
            }
            case NoPauseRequested: {
                LOGGER.warning("Throwing NoPauseRequested exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case NoResumeRequested: {
                LOGGER.warning("Throwing NoResumeRequested exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case ObjectClassNotDefined: {
                LOGGER.warning("Throwing ObjectClassNotDefined exception.");
                throw new ObjectClassNotDefined(ex.getReason());
            }
            case ObjectClassNotKnown: {
                LOGGER.warning("Throwing ObjectClassNotKnown exception.");
                throw new ObjectClassNotKnown(ex.getReason());
            }
            case ObjectClassNotPublished: {
                LOGGER.warning("Throwing ObjectClassNotPublished exception.");
                throw new ObjectClassNotPublished(ex.getReason());
            }
            case ObjectClassNotSubscribed: {
                LOGGER.warning("Throwing ObjectClassNotSubscribed exception.");
                throw new ObjectClassNotSubscribed(ex.getReason());
            }
            case ObjectNotKnown: {
                LOGGER.warning("Throwing ObjectNotKnown exception.");
                throw new ObjectNotKnown(ex.getReason());
            }
            case ObjectAlreadyRegistered: {
                LOGGER.warning("Throwing ObjectAlreadyRegistered exception.");
                throw new ObjectAlreadyRegistered(ex.getReason());
            }
            case RestoreInProgress: {
                LOGGER.warning("Throwing RestoreInProgress exception.");
                throw new RestoreInProgress(ex.getReason());
            }
            case RestoreNotRequested: {
                LOGGER.warning("Throwing RestoreNotRequested exception.");
                throw new RestoreNotRequested(ex.getReason());
            }
            case RTIinternalError: {
                LOGGER.warning("Throwing RTIinternalError exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case SpaceNotDefined: {
                LOGGER.warning("Throwing SpaceNotDefined exception.");
                throw new SpaceNotDefined(ex.getReason());
            }
            case SaveInProgress: {
                LOGGER.warning("Throwing SaveInProgress exception.");
                throw new SaveInProgress(ex.getReason());
            }
            case SaveNotInitiated: {
                LOGGER.warning("Throwing SaveNotInitiated exception.");
                throw new SaveNotInitiated(ex.getReason());
            }
            case SecurityError: {
                LOGGER.warning("Throwing SecurityError exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case SpecifiedSaveLabelDoesNotExist: {
                LOGGER.warning("Throwing SpecifiedSaveLabelDoesNotExist exception.");
                throw new SpecifiedSaveLabelDoesNotExist(ex.getReason());
            }
            case TimeAdvanceAlreadyInProgress: {
                LOGGER.warning("Throwing TimeAdvanceAlreadyInProgress exception.");
                throw new TimeAdvanceAlreadyInProgress(ex.getReason());
            }
            case TimeAdvanceWasNotInProgress: {
                LOGGER.warning("Throwing TimeAdvanceWasNotInProgress exception.");
                throw new TimeAdvanceWasNotInProgress(ex.getReason());
            }
            case TooManyIDsRequested: {
                LOGGER.warning("Throwing TooManyIDsRequested exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case UnableToPerformSave: {
                LOGGER.warning("Throwing UnableToPerformSave exception.");
                throw new UnableToPerformSave(ex.getReason());
            }
            case UnimplementedService: {
                LOGGER.warning("Throwing UnimplementedService exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case UnknownLabel: {
                LOGGER.warning("Throwing UnknownLabel exception.");
                throw new RTIinternalError(ex.getReason());
            }
            case ValueCountExceeded: {
                LOGGER.warning("Throwing ValueCountExceeded exception.");
                throw new RTIinternalError("Value Count Exceeded " + ex.getReason());
            }
            case ValueLengthExceeded: {
                LOGGER.warning("Throwing ValueLengthExceeded exception.");
                throw new RTIinternalError("Value Length Exceeded " + ex.getReason());
            }
            case DimensionNotDefined: {
                LOGGER.warning("Throwing DimensionNotDefined exception.");
                throw new DimensionNotDefined(ex.getReason());
            }
            case OwnershipAcquisitionPending: {
                LOGGER.warning("Throwing OwnershipAcquisitionPending exception.");
                throw new OwnershipAcquisitionPending(ex.getReason());
            }
            case FederateLoggingServiceCalls: {
                LOGGER.warning("Throwing FederateLoggingServiceCalls exception.");
                throw new FederateLoggingServiceCalls(ex.getReason());
            }
            case InteractionClassNotSubscribed: {
                LOGGER.warning("Throwing InteractionClassNotSubscribed exception.");
                throw new InteractionClassNotSubscribed(ex.getReason());
            }
            case TimeRegulationAlreadyEnabled: {
                LOGGER.warning("Throwing TimeRegulationAlreadyEnabled exception.");
                throw new TimeRegulationAlreadyEnabled(ex.getReason());
            }
            case EnableTimeRegulationPending: {
                LOGGER.warning("Throwing EnableTimeRegulationPending exception.");
                throw new EnableTimeRegulationPending(ex.getReason());
            }
            case TimeRegulationWasNotEnabled: {
                LOGGER.warning("Throwing TimeRegulationWasNotEnabled exception.");
                throw new TimeRegulationWasNotEnabled(ex.getReason());
            }
            case TimeConstrainedWasNotEnabled: {
                LOGGER.warning("Throwing TimeConstrainedWasNotEnabled exception.");
                throw new TimeConstrainedWasNotEnabled(ex.getReason());
            }
            case EnableTimeConstrainedPending: {
                LOGGER.warning("Throwing EnableTimeConstrainedPending exception.");
                throw new EnableTimeConstrainedPending(ex.getReason());
            }
            case TimeConstrainedAlreadyEnabled: {
                LOGGER.warning("Throwing TimeConstrainedAlreadyEnabled exception.");
                throw new TimeConstrainedAlreadyEnabled(ex.getReason());
            }
            case RegionInUse: {
                LOGGER.warning("Throwing RegionInUse exception.");
                throw new RegionInUse(ex.getReason());
            }
            case InvalidRegionContext: {
                LOGGER.warning("Throwing InvalidRegionContext exception.");
                throw new InvalidRegionContext(ex.getReason());
            }
            default: {
                LOGGER.severe("Throwing unknown exception !");
                throw new RTIinternalError(ex.getReason());
            }
        }
    }

    private void callFederateAmbassador(CertiMessage message) throws RTIinternalError {
        try {
            switch (message.getType()) {
                case SYNCHRONIZATION_POINT_REGISTRATION_SUCCEEDED:
                    federateAmbassador.synchronizationPointRegistrationSucceeded(
                            ((SynchronizationPointRegistrationSucceeded) message).getLabel());
                    break;

                case ANNOUNCE_SYNCHRONIZATION_POINT:
                    federateAmbassador.announceSynchronizationPoint(
                            (((AnnounceSynchronizationPoint) message).getLabel()),
                            ((AnnounceSynchronizationPoint) message).getTag());
                    break;

                case FEDERATION_SYNCHRONIZED:
                    federateAmbassador.federationSynchronized(
                            ((FederationSynchronized) message).getLabel());
                    break;

                case INITIATE_FEDERATE_SAVE:
                    federateAmbassador.initiateFederateSave(
                            ((InitiateFederateSave) message).getLabel());
                    break;

                case FEDERATION_SAVED:
                    federateAmbassador.federationSaved();
                    break;

                case REQUEST_FEDERATION_RESTORE_SUCCEEDED:
                    federateAmbassador.requestFederationRestoreSucceeded(
                            ((RequestFederationRestoreSucceeded) message).getLabel());
                    break;

                case REQUEST_FEDERATION_RESTORE_FAILED:
                    federateAmbassador.requestFederationRestoreFailed(
                            (((RequestFederationRestoreFailed) message).getLabel()),
                            new String(((RequestFederationRestoreFailed) message).getTag()));
                    break;

                case FEDERATION_RESTORE_BEGUN:
                    federateAmbassador.federationRestoreBegun();
                    break;

                case INITIATE_FEDERATE_RESTORE:
                    federateAmbassador.initiateFederateRestore(
                            (((InitiateFederateRestore) message).getLabel()),
                            ((InitiateFederateRestore) message).getFederate());
                    break;

                case FEDERATION_RESTORED:
                    federateAmbassador.federationRestored();
                    break;

                case FEDERATION_NOT_RESTORED:
                    federateAmbassador.federationNotRestored();
                    break;

                case START_REGISTRATION_FOR_OBJECT_CLASS:
                    federateAmbassador.startRegistrationForObjectClass(
                            (int) ((StartRegistrationForObjectClass) message).getObjectClass());
                    break;

                case STOP_REGISTRATION_FOR_OBJECT_CLASS:
                    federateAmbassador.stopRegistrationForObjectClass(
                            (int) ((StopRegistrationForObjectClass) message).getObjectClass());
                    break;

                case TURN_INTERACTIONS_ON:
                    federateAmbassador.turnInteractionsOn(
                            (int) ((TurnInteractionsOn) message).getInteractionClass());
                    break;

                case TURN_INTERACTIONS_OFF:
                    federateAmbassador.turnInteractionsOff(
                            (int) ((TurnInteractionsOff) message).getInteractionClass());
                    break;

                case DISCOVER_OBJECT_INSTANCE:
                    federateAmbassador.discoverObjectInstance(
                            (int) ((DiscoverObjectInstance) message).getObject(),
                            (int) ((DiscoverObjectInstance) message).getObjectClass(),
                            ((DiscoverObjectInstance) message).getName());
                    break;

                case REFLECT_ATTRIBUTE_VALUES:
                    if (((ReflectAttributeValues) message).getBooleanValue()) {
                        federateAmbassador.reflectAttributeValues(
                                (int) ((ReflectAttributeValues) message).getObject(),
                                ((ReflectAttributeValues) message).getReflectedAttributes(),
                                ((ReflectAttributeValues) message).getTag(), message.getFederationTime(), message.getEventRetraction());
                    } else {
                        federateAmbassador.reflectAttributeValues(
                                (int) ((ReflectAttributeValues) message).getObject(),
                                ((ReflectAttributeValues) message).getReflectedAttributes(),
                                ((ReflectAttributeValues) message).getTag());
                    }

                    break;

                case RECEIVE_INTERACTION:
                    if (((ReceiveInteraction) message).getBooleanValue()) {
                        federateAmbassador.receiveInteraction(
                                (int) ((ReceiveInteraction) message).getInteractionClass(),
                                ((ReceiveInteraction) message).getReceivedInteraction(),
                                (((ReceiveInteraction) message).getTag()),
                                ((ReceiveInteraction) message).getFederationTime(),
                                ((ReceiveInteraction) message).getEventRetraction());
                    } else {
                        federateAmbassador.receiveInteraction(
                                (int) ((ReceiveInteraction) message).getInteractionClass(),
                                ((ReceiveInteraction) message).getReceivedInteraction(),
                                (((ReceiveInteraction) message).getTag()));
                    }
                    break;


                case REMOVE_OBJECT_INSTANCE:
                    if (((RemoveObjectInstance) message).getBooleanValue()) {
                        federateAmbassador.removeObjectInstance(
                                (int) ((RemoveObjectInstance) message).getObject(),
                                ((RemoveObjectInstance) message).getTag(),
                                ((RemoveObjectInstance) message).getFederationTime(),
                                ((RemoveObjectInstance) message).getEventRetraction());
                    } else {
                        federateAmbassador.removeObjectInstance(
                                (int) ((RemoveObjectInstance) message).getObject(),
                                ((RemoveObjectInstance) message).getTag());
                    }
                    break;


                case PROVIDE_ATTRIBUTE_VALUE_UPDATE:
                    federateAmbassador.provideAttributeValueUpdate(
                            (int) ((ProvideAttributeValueUpdate) message).getObject(),
                            ((ProvideAttributeValueUpdate) message).getAttributes());
                    break;

                case REQUEST_RETRACTION:
                    break;

                case REQUEST_ATTRIBUTE_OWNERSHIP_ASSUMPTION:
                    federateAmbassador.requestAttributeOwnershipAssumption(
                            (int) ((RequestAttributeOwnershipAssumption) message).getObject(),
                            ((RequestAttributeOwnershipAssumption) message).getAttributes(),
                            ((RequestAttributeOwnershipAssumption) message).getTag());
                    break;

                case REQUEST_ATTRIBUTE_OWNERSHIP_RELEASE:
                    federateAmbassador.requestAttributeOwnershipRelease(
                            (int) ((RequestAttributeOwnershipRelease) message).getObject(),
                            ((RequestAttributeOwnershipRelease) message).getAttributes(),
                            ((RequestAttributeOwnershipRelease) message).getTag());
                    break;

                case ATTRIBUTE_OWNERSHIP_UNAVAILABLE:
                    federateAmbassador.attributeOwnershipUnavailable(
                            (int) ((AttributeOwnershipUnavailable) message).getObject(),
                            (((AttributeOwnershipUnavailable) message).getAttributes()));
                    break;

                case ATTRIBUTE_OWNERSHIP_ACQUISITION_NOTIFICATION:
                    federateAmbassador.attributeOwnershipAcquisitionNotification(
                            (int) ((AttributeOwnershipAcquisitionNotification) message).getObject(),
                            ((AttributeOwnershipAcquisitionNotification) message).getAttributes());
                    break;

                case ATTRIBUTE_OWNERSHIP_DIVESTITURE_NOTIFICATION:
                    federateAmbassador.attributeOwnershipDivestitureNotification(
                            (int) ((AttributeOwnershipDivestitureNotification) message).getObject(),
                            ((AttributeOwnershipDivestitureNotification) message).getAttributes());
                    break;

                case CONFIRM_ATTRIBUTE_OWNERSHIP_ACQUISITION_CANCELLATION:
                    federateAmbassador.confirmAttributeOwnershipAcquisitionCancellation(
                            (int) ((ConfirmAttributeOwnershipAcquisitionCancellation) message).getObject(),
                            ((ConfirmAttributeOwnershipAcquisitionCancellation) message).getAttributes());
                    break;

                case INFORM_ATTRIBUTE_OWNERSHIP:
                    federateAmbassador.informAttributeOwnership(
                            (int) ((InformAttributeOwnership) message).getObject(),
                            ((InformAttributeOwnership) message).getAttribute(),
                            ((InformAttributeOwnership) message).getFederate());
                    break;

                case ATTRIBUTE_IS_NOT_OWNED:
                    federateAmbassador.attributeIsNotOwned(
                            (int) ((AttributeIsNotOwned) message).getObject(),
                            ((AttributeIsNotOwned) message).getAttribute());
                    break;

                case TIME_ADVANCE_GRANT:
                    federateAmbassador.timeAdvanceGrant(
                            message.getFederationTime());
                    break;

                case TIME_REGULATION_ENABLED:
                    federateAmbassador.timeRegulationEnabled(
                            message.getFederationTime());
                    break;

                case TIME_CONSTRAINED_ENABLED:
                    federateAmbassador.timeConstrainedEnabled(
                            message.getFederationTime());
                    break;

                default:
                    LOGGER.severe("RTI service requested by RTI is unknown.");
                    throw new RTIinternalError("RTI service requested by RTI is unknown.");
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error in " + message.getType() + ": " + ex.getMessage(), ex);
            throw new RTIinternalError(ex.getMessage());
        }
    }
}
