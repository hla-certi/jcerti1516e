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
import certi.logging.StreamListener;
import hla.rti.*;
import hla.rti.jlc.RTIambassadorEx;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CertiRtiAmbassador implements RTIambassadorEx {

    private final static Logger LOGGER = Logger.getLogger(CertiRtiAmbassador.class.getName());
    private FederateAmbassador federateAmbassador;
    private Socket socket;
    private MessageBuffer messageBuffer;

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

    public CertiRtiAmbassador() throws RTIinternalError {
        //////////////////////////
        // Load properties file //
        //////////////////////////       
        Properties properties = new Properties();

        try {
            InputStream propertyInsideJarStream = CertiRtiAmbassador.class.getClassLoader().getResourceAsStream("certi.properties");

            if (propertyInsideJarStream != null) {
                properties.load(propertyInsideJarStream);
            }
        } catch (IOException ex) {
            LOGGER.severe("Loading property file from JAR failed. " + ex.getLocalizedMessage());
        }

        try {
            properties.load(new FileInputStream("certi.properties"));
        } catch (FileNotFoundException ex) { //Property file not found - not a big deal
        } catch (IOException ex) {
            LOGGER.severe("Loading property file from outside JAR failed. " + ex.getLocalizedMessage());
        }


        /////////////////////
        // Prepare logging //
        /////////////////////
        Level logLevel = Level.parse(properties.getProperty("logLevel") != null ? properties.getProperty("logLevel") : "OFF");

        Logger rootLogger = Logger.getLogger("certi");
        rootLogger.setLevel(logLevel);

        if (logLevel != Level.OFF) {
            String enableHtmlLoggingString = properties.getProperty("enableHtmlLogging") != null ? properties.getProperty("enableHtmlLogging") : "false";
            if (Boolean.parseBoolean(enableHtmlLoggingString)) {
                try {
                    String htmlLogFileNamePropertyString = properties.getProperty("htmlLogFileName") != null ? properties.getProperty("htmlLogFileName") : "librti.html";
                    FileHandler fileHtml = new FileHandler(htmlLogFileNamePropertyString);
                    fileHtml.setFormatter(new HtmlFormatter());
                    rootLogger.addHandler(fileHtml);
                } catch (IOException exception) {
                    LOGGER.severe("Creating html log file failed. " + exception.getLocalizedMessage());
                }
            }

            String enableTextLoggingString = properties.getProperty("enableTextLogging") != null ? properties.getProperty("enableTextLogging") : "false";
            if (Boolean.parseBoolean(enableTextLoggingString)) {
                try {
                    String textLogFileNameString = properties.getProperty("textLogFileName") != null ? properties.getProperty("textLogFileName") : "librti.log";
                    FileHandler fileTxt = new FileHandler(textLogFileNameString);
                    fileTxt.setFormatter(new SimpleFormatter());
                    LOGGER.addHandler(fileTxt);
                } catch (IOException exception) {
                    LOGGER.severe("Creating text log file failed. " + exception.getLocalizedMessage());
                }
            }
        }

        ////////////////////
        // Prepare socket //
        ////////////////////
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(0, 1);
        } catch (IOException exception) {
            throw new RTIinternalError("Creating server socket failed. " + exception.getLocalizedMessage());
        }

        LOGGER.info("Using TCP socket server on port " + serverSocket.getLocalPort());

        try {
			int max_retry = 5;
			int nb_retry = 0;
            String rtiaPathString = properties.getProperty("rtiaPath") != null ? properties.getProperty("rtiaPath") : "";

            Process rtiaProcess = Runtime.getRuntime().exec(rtiaPathString + "rtia -p " + 
                                                            serverSocket.getLocalPort());
                                                            
			// This is a workaround for issue 53878 which allow to fiw timing issue
			// Here we wait for few milliseconds to be sure process is created
			while ( (rtiaProcess.isAlive() != true) && (nb_retry <  max_retry))
			{
				nb_retry = nb_retry + 1;
				// sleep for nb_retry times 20 milliseconds (see comment #7 in issue 53878)
				Thread.sleep(nb_retry*20);
			}
			LOGGER.info("RTIA process is seen as alive after " + nb_retry + " attemps");
            // Read error and output streams, so that in case debugging is enabled for RTIA
            // the process will not block because stream buffers are full 
            StreamListener outListener = new StreamListener(rtiaProcess.getInputStream());
            StreamListener errListener = new StreamListener(rtiaProcess.getErrorStream());
            outListener.start();
            errListener.start();
        } catch (IOException exception) {
            throw new RTIinternalError("RTI Ambassador executable not found. " + exception.getLocalizedMessage());
        }
        catch(InterruptedException ex) 
		{
			Thread.currentThread().interrupt();
		}
		
		LOGGER.info("Using TCP socket server on port " + serverSocket.getLocalPort());

        try {
            socket = serverSocket.accept();
            messageBuffer = new MessageBuffer(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException exception) {
            throw new RTIinternalError("Connection to RTIA failed. " + exception.getLocalizedMessage());
        }

        ////////////////////
        // Open connexion //
        ////////////////////
        try {
            OpenConnexion openConnexion = new OpenConnexion();
            openConnexion.setVersionMinor(0);
            openConnexion.setVersionMajor(1);
            OpenConnexion response = (OpenConnexion) processRequest(openConnexion);
        } catch (Exception exception) {
            throw new RTIinternalError("Connection to RTIA failed. " + exception.getLocalizedMessage());
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

    synchronized private boolean tickKernel(boolean multiple, double minimum, double maximum) throws SpecifiedSaveLabelDoesNotExist, ConcurrentAccessAttempted, RTIinternalError {
        //TODO Code is hard to read - rewrite
        TickRequest tickRequest;
        CertiMessage tickResponse = null;

        LOGGER.fine("Request callback(s) from the local RTIA");
        tickRequest = new TickRequest();
        tickRequest.setMultiple(multiple);
        tickRequest.setMinTickTime(minimum);
        tickRequest.setMaxTickTime(maximum);
        
        synchronized(this.messageBuffer) {

            tickRequest.writeMessage(this.messageBuffer);

            try {
                this.messageBuffer.send();
            } catch (IOException ex) {
                throw new RTIinternalError("NetworkError in tick() while sending TICK_REQUEST: " + ex.getMessage());
            }

            LOGGER.fine("Reading response(s) from the local RTIA");
            while (true) {
                try {
                    tickResponse = MessageFactory.readMessage(messageBuffer);

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
                        this.messageBuffer.send();
                    } catch (IOException ex) {
                        throw new RTIinternalError("NetworkError in tick() while sending TICK_REQUEST_NEXT: " + ex.getMessage());
                    }
                }
            }
        } // end of synchronized(this.messageBuffer)
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

     /** The argument that has to be passed to createFederationExecution() is,
     * according to the standard, the location of the FED file on a possibly
     * _remote_ file system, the file system where the RTIG is running.
     * - In Java, getCanonicalPath() resolves the path on the _local_ filesystem.
     * - getName():only work if the FED file can be found by the RTIG using
     *   only the filename (needs to be on the CERTI_FOM_PATH).
     * The following API is not compliant with the standard:
     * public void createFederationExecution(String executionName, URL fed)
     * The second argument needs to be a string, not a URL.
     * The way the standard is written, and the way the RTIG is implemented in
     * the C++ code, the FED file is not a network resource. It is a private
     * resource to the RTIG on the local filesystem where the RTIG is running.
     */
    
    /** Create a federation execution with the specified name and FED file.
     *  @param executionName The name of the execution.
     *  @param fed The file name or path and file name of the FED file on
     *   the machine that is executing the RTIG.
     */
    public void createFederationExecution(String executionName, String fed)
            throws FederationExecutionAlreadyExists, CouldNotOpenFED, ErrorReadingFED, RTIinternalError, ConcurrentAccessAttempted {
        if (executionName == null || executionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty execution name");
        }
        if (fed == null || fed.length() == 0) {
            throw new RTIinternalError("No fed file specified.");
        }
        
        CreateFederationExecution request = new CreateFederationExecution();
        request.setFederationName(executionName);
        
        request.setFEDid(fed);
        
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
    
    /** The following incorrect implementation is kept for backward compatibility.
     *  This version is incorrect because it turns the URL fed argument into a path
     *  with a path resolved the _current_ machine (the one running the federation),
     *  which is not necessarily the same as the one running the RTIG.
     */
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
            request.setFEDid(new File(fed.toURI()).getCanonicalPath());
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

        request.setFederateName(federateName);
        request.setFederationName(federationExecutionName);

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
        request.setResignAction((short) resignAction);

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
        //request.setBooleanValue(false); //without set of federates

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

        request.setFederateSet(synchronizationSet);

        /*
        if (synchronizationSet.isEmpty()) {
        //TODO Check request.setBooleanValue(false);
        } else {
        //TODO Check request.setBooleanValue(true);
        request.setFederateSet(synchronizationSet);
        }
         */

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
        //request.setTimestamped(true);

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
        //request.setTimestamped(false); // without time

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

            return response.getObject();
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
        request.setObjectName(theObjectName);

        try {
            RegisterObjectInstance response = (RegisterObjectInstance) processRequest(request);
            return response.getObject();
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
        //request.setTimestamped(true);

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
        //request.setTimestamped(true); //With time

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
        //request.setTimestamped(false); //No time

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
        //request.setTimestamped(true);//With time

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
        request.setTransportationType((short) theType);

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
        request.setTransportationType((short) theType);

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

        request.setLookAhead(theLookahead);
        //request.setBooleanValue(true);


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

        //request.setBooleanValue(false);

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

        //request.setBooleanValue(true);

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

        // request.setBooleanValue(false);

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

        request.setLookAhead(theLookahead);

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

            return response.getLookAhead();
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
        request.setOrder((short) theType);
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
        request.setOrder((short) theType);

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
//TODO        request.setNumber(numberOfExtents);

        try {
            DdmCreateRegion response = (DdmCreateRegion) processRequest(request);

            return new CertiRegion(response.getRegion(), response.getSpace(), numberOfExtents);
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
//TODO        request.setExtents(((CertiRegion) modifiedRegionInstance).getExtents());

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
        if (theAttributes == null || theAttributes.length == 0) {
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
//TODO        request.setRegions(null);


        try {
            DdmRegisterObject response = (DdmRegisterObject) processRequest(request);

            return response.getObject();
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
        if (theAttributes == null || theAttributes.length == 0) {
            throw new RTIinternalError("Incorrect supplied parameter - the attributes");
        }
        if (theRegions == null) {
            throw new RTIinternalError("Incorrect supplied parameter - regions");
        }

        DdmRegisterObject request = new DdmRegisterObject();

        request.setObjectClass(theClass);
        request.setTag(theObject.getBytes());

        CertiAttributeHandleSet attributeHandles = new CertiAttributeHandleSet();

        for (int i = 0; i < theAttributes.length; i++) {
            attributeHandles.add(theAttributes[i]);
        }

        request.setAttributes(attributeHandles);

        List<Integer> regions = new ArrayList<Integer>(theRegions.length);

        for (int i = 0; i < theRegions.length; i++) {
            regions.add(((CertiRegion) theRegions[i]).getHandle());
        }

//        request.setRegions(regions); TODO

        try {
            DdmRegisterObject response = (DdmRegisterObject) processRequest(request);

            return response.getObject();
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
        request.setPassive(false); //Not passive

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
        request.setPassive(true); //Passive

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
        request.setPassive(false); //Not passive

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
        request.setPassive(true); //Passive

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
        request.setClassName(theName);

        try {
            GetObjectClassHandle response = (GetObjectClassHandle) processRequest(request);
            return response.getObjectClass(); //hack handles maju rozdielne velkosti
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
        request.setAttributeName(theName);
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

            return response.getAttributeName();
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

        request.setClassName(theName);

        try {
            GetInteractionClassHandle response = (GetInteractionClassHandle) processRequest(request);

            return response.getInteractionClass();
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

            return response.getClassName();
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

        request.setParameterName(theName);
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

            return response.getParameterName();
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

        request.setObjectInstanceName(theName);

        try {
            GetObjectInstanceHandle response = (GetObjectInstanceHandle) processRequest(request);

            return response.getObject();
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

            return response.getObjectInstanceName();
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

        request.setSpaceName(theName);

        try {
            GetSpaceHandle response = (GetSpaceHandle) processRequest(request);

            return response.getSpace();
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

            return response.getSpaceName();
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

        request.setDimensionName(theName);
        request.setDimension(whichSpace);

        try {
            GetDimensionHandle response = (GetDimensionHandle) processRequest(request);

            return response.getDimension();
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

            return response.getDimensionName();
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

        request.setAttribute((short) theHandle);
        request.setObjectClass(whichClass);

        try {
            GetAttributeSpaceHandle response = (GetAttributeSpaceHandle) processRequest(request);

            return response.getSpace();
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

            return response.getObjectClass();
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

            return response.getSpace();
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

        request.setTransportationName(theName);

        try {
            GetTransportationHandle response = (GetTransportationHandle) processRequest(request);

            return (int) response.getTransportation();
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

        request.setTransportation((short) theHandle);

        try {
            GetTransportationName response = (GetTransportationName) processRequest(request);

            return response.getTransportationName();
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

        request.setOrderingName(theName);

        try {
            GetOrderingHandle response = (GetOrderingHandle) processRequest(request);

            return (int) response.getOrdering();
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

        request.setOrdering((short) theHandle);

        try {
            GetOrderingName response = (GetOrderingName) processRequest(request);

            return response.getOrderingName();
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

        CertiMessage response = null;
        synchronized(this.messageBuffer) {
            request.writeMessage(this.messageBuffer);

            try {
                LOGGER.info("Sending message (" + request.toString() + ")");
                this.messageBuffer.send();
            } catch (IOException ex) {
                LOGGER.severe("libRTI: exception: NetworkError (write)");
                throw new RTIinternalError("libRTI: Network Write Error");
            }

            try {
                response = MessageFactory.readMessage(messageBuffer);

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
        } // end of synchronized(this.messageBuffer)
        return response;
    }

    private void translateException(CertiException ex) throws ArrayIndexOutOfBounds, AsynchronousDeliveryAlreadyEnabled, AsynchronousDeliveryAlreadyDisabled, AttributeAlreadyOwned, AttributeAlreadyBeingAcquired, AttributeAlreadyBeingDivested, AttributeDivestitureWasNotRequested, AttributeAcquisitionWasNotRequested, AttributeNotDefined, AttributeNotKnown, AttributeNotOwned, AttributeNotPublished, RTIinternalError, ConcurrentAccessAttempted, CouldNotDiscover, CouldNotOpenFED, CouldNotRestore, DeletePrivilegeNotHeld, ErrorReadingFED, EventNotKnown, FederateAlreadyExecutionMember, FederateInternalError, FederateNotExecutionMember, FederateOwnsAttributes, FederatesCurrentlyJoined, FederateWasNotAskedToReleaseAttribute, FederationExecutionAlreadyExists, FederationExecutionDoesNotExist, FederationTimeAlreadyPassed, RegionNotKnown, InteractionClassNotDefined, InteractionClassNotKnown, InteractionClassNotPublished, InteractionParameterNotDefined, InteractionParameterNotKnown, InvalidExtents, InvalidFederationTime, InvalidLookahead, InvalidOrderingHandle, InvalidResignAction, InvalidRetractionHandle, InvalidTransportationHandle, NameNotFound, ObjectClassNotDefined, ObjectClassNotKnown, ObjectClassNotPublished, ObjectClassNotSubscribed, ObjectNotKnown, ObjectAlreadyRegistered, RestoreInProgress, RestoreNotRequested, SpaceNotDefined, SaveInProgress, SaveNotInitiated, SpecifiedSaveLabelDoesNotExist, TimeAdvanceAlreadyInProgress, TimeAdvanceWasNotInProgress, UnableToPerformSave, DimensionNotDefined, OwnershipAcquisitionPending, FederateLoggingServiceCalls, InteractionClassNotSubscribed, EnableTimeRegulationPending, TimeRegulationAlreadyEnabled, TimeRegulationWasNotEnabled, TimeConstrainedWasNotEnabled, EnableTimeConstrainedPending, TimeConstrainedAlreadyEnabled, RegionInUse, InvalidRegionContext {
        LOGGER.warning("Throwing exception: " + ex.getExceptionType().toString());

        switch (ex.getExceptionType()) {
            case NO_EXCEPTION:
                break;

            case ARRAY_INDEX_OUT_OF_BOUNDS:
                throw new ArrayIndexOutOfBounds(ex.getReason());

            case ASYNCHRONOUS_DELIVERY_ALREADY_ENABLED:
                throw new AsynchronousDeliveryAlreadyEnabled(ex.getReason());

            case ASYNCHRONOUS_DELIVERY_ALREADY_DISABLED:
                throw new AsynchronousDeliveryAlreadyDisabled(ex.getReason());

            case ATTRIBUTE_ALREADY_OWNED:
                throw new AttributeAlreadyOwned(ex.getReason());

            case ATTRIBUTE_ALREADY_BEING_ACQUIRED:
                throw new AttributeAlreadyBeingAcquired(ex.getReason());

            case ATTRIBUTE_ALREADY_BEING_DIVESTED:
                throw new AttributeAlreadyBeingDivested(ex.getReason());

            case ATTRIBUTE_DIVESTITURE_WAS_NOT_REQUESTED:
                throw new AttributeDivestitureWasNotRequested(ex.getReason());

            case ATTRIBUTE_ACQUISITION_WAS_NOT_REQUESTED:
                throw new AttributeAcquisitionWasNotRequested(ex.getReason());

            case ATTRIBUTE_NOT_DEFINED:
                throw new AttributeNotDefined(ex.getReason());

            case ATTRIBUTE_NOT_KNOWN:
                throw new AttributeNotKnown(ex.getReason());

            case ATTRIBUTE_NOT_OWNED:
                throw new AttributeNotOwned(ex.getReason());

            case ATTRIBUTE_NOT_PUBLISHED:
                throw new AttributeNotPublished(ex.getReason());

            case ATTRIBUTE_NOT_SUBSCRIBED:
                throw new RTIinternalError(ex.getReason());

            case CONCURRENT_ACCESS_ATTEMPTED:
                throw new ConcurrentAccessAttempted(ex.getReason());

            case COULD_NOT_DISCOVER:
                throw new CouldNotDiscover(ex.getReason());

            case COULD_NOT_OPEN_FED:
                throw new CouldNotOpenFED(ex.getReason());

            case COULD_NOT_OPEN_RID:
                throw new RTIinternalError(ex.getReason());

            case COULD_NOT_RESTORE:
                throw new CouldNotRestore(ex.getReason());

            case DELETE_PRIVILEGE_NOT_HELD:
                throw new DeletePrivilegeNotHeld(ex.getReason());

            case ERROR_READING_RID:
                throw new RTIinternalError(ex.getReason());

            case ERROR_READING_FED:
                throw new ErrorReadingFED(ex.getReason());

            case EVENT_NOT_KNOWN:
                throw new EventNotKnown(ex.getReason());

            case FEDERATE_ALREADY_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_ALREADY_EXECUTION_MEMBER:
                throw new FederateAlreadyExecutionMember(ex.getReason());

            case FEDERATE_DOES_NOT_EXIST:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_INTERNAL_ERROR:
                throw new FederateInternalError(ex.getReason());

            case FEDERATE_NAME_ALREADY_IN_USE:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_NOT_EXECUTION_MEMBER:
                throw new FederateNotExecutionMember(ex.getReason());

            case FEDERATE_NOT_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_OWNS_ATTRIBUTES:
                throw new FederateOwnsAttributes(ex.getReason());

            case FEDERATES_CURRENTLY_JOINED:
                throw new FederatesCurrentlyJoined(ex.getReason());

            case FEDERATE_WAS_NOT_ASKED_TO_RELEASE_ATTRIBUTE:
                throw new FederateWasNotAskedToReleaseAttribute(ex.getReason());

            case FEDERATION_ALREADY_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATION_EXECUTION_ALREADY_EXISTS:
                throw new FederationExecutionAlreadyExists(ex.getReason());

            case FEDERATION_EXECUTION_DOES_NOT_EXIST:
                throw new FederationExecutionDoesNotExist(ex.getReason());

            case FEDERATION_NOT_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATION_TIME_ALREADY_PASSED:
                throw new FederationTimeAlreadyPassed(ex.getReason());

            case FEDERATE_NOT_PUBLISHING:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_NOT_SUBSCRIBING:
                throw new RTIinternalError(ex.getReason());

            case REGION_NOT_KNOWN:
                throw new RegionNotKnown(ex.getReason());

            case ID_SUPPLY_EXHAUSTED:
                throw new RTIinternalError(ex.getReason());

            case INTERACTION_CLASS_NOT_DEFINED:
                throw new InteractionClassNotDefined(ex.getReason());

            case INTERACTION_CLASS_NOT_KNOWN:
                throw new InteractionClassNotKnown(ex.getReason());

            case INTERACTION_CLASS_NOT_PUBLISHED:
                throw new InteractionClassNotPublished(ex.getReason());

            case INTERACTION_PARAMETER_NOT_DEFINED:
                throw new InteractionParameterNotDefined(ex.getReason());

            case INTERACTION_PARAMETER_NOT_KNOWN:
                throw new InteractionParameterNotKnown(ex.getReason());

            case INVALID_DIVESTITURE_CONDITION:
                throw new RTIinternalError(ex.getReason());

            case INVALID_EXTENTS:
                throw new InvalidExtents(ex.getReason());

            case INVALID_FEDERATION_TIME:
                throw new InvalidFederationTime(ex.getReason());

            case INVALID_FEDERATION_TIME_DELTA:
                throw new RTIinternalError(ex.getReason());

            case INVALID_LOOKAHEAD:
                throw new InvalidLookahead(ex.getReason());

            case INVALID_OBJECT_HANDLE:
                throw new RTIinternalError(ex.getReason());

            case INVALID_ORDERING_HANDLE:
                throw new InvalidOrderingHandle(ex.getReason());

            case INVALID_RESIGN_ACTION:
                throw new InvalidResignAction(ex.getReason());

            case INVALID_RETRACTION_HANDLE:
                throw new InvalidRetractionHandle(ex.getReason());

            case INVALID_ROUTING_SPACE:
                throw new RTIinternalError(ex.getReason());

            case INVALID_TRANSPORTATION_HANDLE:
                throw new InvalidTransportationHandle(ex.getReason());

            case MEMORY_EXHAUSTED:
                throw new RTIinternalError("Memory Exhausted: " + ex.getReason());

            case NAME_NOT_FOUND:
                throw new NameNotFound(ex.getReason());

            case NO_PAUSE_REQUESTED:
                throw new RTIinternalError(ex.getReason());

            case NO_RESUME_REQUESTED:
                throw new RTIinternalError(ex.getReason());

            case OBJECT_CLASS_NOT_DEFINED:
                throw new ObjectClassNotDefined(ex.getReason());

            case OBJECT_CLASS_NOT_KNOWN:
                throw new ObjectClassNotKnown(ex.getReason());

            case OBJECT_CLASS_NOT_PUBLISHED:
                throw new ObjectClassNotPublished(ex.getReason());

            case OBJECT_CLASS_NOT_SUBSCRIBED:
                throw new ObjectClassNotSubscribed(ex.getReason());

            case OBJECT_NOT_KNOWN:
                throw new ObjectNotKnown(ex.getReason());

            case OBJECT_ALREADY_REGISTERED:
                throw new ObjectAlreadyRegistered(ex.getReason());

            case RESTORE_IN_PROGRESS:
                throw new RestoreInProgress(ex.getReason());

            case RESTORE_NOT_REQUESTED:
                throw new RestoreNotRequested(ex.getReason());

            case RTI_INTERNAL_ERROR:
                throw new RTIinternalError(ex.getReason());

            case SPACE_NOT_DEFINED:
                throw new SpaceNotDefined(ex.getReason());

            case SAVE_IN_PROGRESS:
                throw new SaveInProgress(ex.getReason());

            case SAVE_NOT_INITIATED:
                throw new SaveNotInitiated(ex.getReason());

            case SECURITY_ERROR:
                throw new RTIinternalError(ex.getReason());

            case SPECIFIED_SAVE_LABEL_DOES_NOT_EXIST:
                throw new SpecifiedSaveLabelDoesNotExist(ex.getReason());

            case TIME_ADVANCE_ALREADY_IN_PROGRESS:
                throw new TimeAdvanceAlreadyInProgress(ex.getReason());

            case TIME_ADVANCE_WAS_NOT_IN_PROGRESS:
                throw new TimeAdvanceWasNotInProgress(ex.getReason());

            case TOO_MANY_IDS_REQUESTED:
                throw new RTIinternalError(ex.getReason());

            case UNABLE_TO_PERFORM_SAVE:
                throw new UnableToPerformSave(ex.getReason());

            case UNIMPLEMENTED_SERVICE:
                throw new RTIinternalError(ex.getReason());

            case UNKNOWN_LABEL:
                throw new RTIinternalError(ex.getReason());

            case VALUE_COUNT_EXCEEDED:
                throw new RTIinternalError("Value Count Exceeded " + ex.getReason());

            case VALUE_LENGTH_EXCEEDED:
                throw new RTIinternalError("Value Length Exceeded " + ex.getReason());

            case DIMENSION_NOT_DEFINED:
                throw new DimensionNotDefined(ex.getReason());

            case OWNERSHIP_ACQUISITION_PENDING:
                throw new OwnershipAcquisitionPending(ex.getReason());

            case FEDERATE_LOGGING_SERVICE_CALLS:
                throw new FederateLoggingServiceCalls(ex.getReason());

            case INTERACTION_CLASS_NOT_SUBSCRIBED:
                throw new InteractionClassNotSubscribed(ex.getReason());

            case TIME_REGULATION_ALREADY_ENABLED:
                throw new TimeRegulationAlreadyEnabled(ex.getReason());

            case ENABLE_TIME_REGULATION_PENDING:
                throw new EnableTimeRegulationPending(ex.getReason());

            case TIME_REGULATION_WAS_NOT_ENABLED:
                throw new TimeRegulationWasNotEnabled(ex.getReason());

            case TIME_CONSTRAINED_WAS_NOT_ENABLED:
                throw new TimeConstrainedWasNotEnabled(ex.getReason());

            case ENABLE_TIME_CONSTRAINED_PENDING:
                throw new EnableTimeConstrainedPending(ex.getReason());

            case TIME_CONSTRAINED_ALREADY_ENABLED:
                throw new TimeConstrainedAlreadyEnabled(ex.getReason());

            case REGION_IN_USE:
                throw new RegionInUse(ex.getReason());

            case INVALID_REGION_CONTEXT:
                throw new InvalidRegionContext(ex.getReason());

            default:
                LOGGER.severe("Throwing unknown exception !");
                //throw new RTIinternalError(ex.getReason());
                throw new RuntimeException("Can not translate exception (it is probably not implemented yet).");
        }
    }

    private void callFederateAmbassador(CertiMessage message) throws RTIinternalError {
        try {
            switch (message.getType()) {
                case SYNCHRONIZATION_POINT_REGISTRATION_SUCCEEDED:
                    federateAmbassador.synchronizationPointRegistrationSucceeded(
                            message.getLabel());
                    break;

                case ANNOUNCE_SYNCHRONIZATION_POINT:
                    federateAmbassador.announceSynchronizationPoint(
                            (message.getLabel()),
                            message.getTag());
                    break;

                case FEDERATION_SYNCHRONIZED:
                    federateAmbassador.federationSynchronized(
                            message.getLabel());
                    break;

                case INITIATE_FEDERATE_SAVE:
                    federateAmbassador.initiateFederateSave(
                            message.getLabel());
                    break;

                case FEDERATION_SAVED:
                    federateAmbassador.federationSaved();
                    break;

                case REQUEST_FEDERATION_RESTORE_SUCCEEDED:
                    federateAmbassador.requestFederationRestoreSucceeded(
                            message.getLabel());
                    break;

                case REQUEST_FEDERATION_RESTORE_FAILED:
                    federateAmbassador.requestFederationRestoreFailed(
                            (message.getLabel()),
                            new String(message.getTag()));
                    break;

                case FEDERATION_RESTORE_BEGUN:
                    federateAmbassador.federationRestoreBegun();
                    break;

                case INITIATE_FEDERATE_RESTORE:
                    federateAmbassador.initiateFederateRestore(
                            (message.getLabel()),
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
                            ((StartRegistrationForObjectClass) message).getObjectClass());
                    break;

                case STOP_REGISTRATION_FOR_OBJECT_CLASS:
                    federateAmbassador.stopRegistrationForObjectClass(
                            ((StopRegistrationForObjectClass) message).getObjectClass());
                    break;

                case TURN_INTERACTIONS_ON:
                    federateAmbassador.turnInteractionsOn(
                            ((TurnInteractionsOn) message).getInteractionClass());
                    break;

                case TURN_INTERACTIONS_OFF:
                    federateAmbassador.turnInteractionsOff(
                            ((TurnInteractionsOff) message).getInteractionClass());
                    break;

                case DISCOVER_OBJECT_INSTANCE:
                    federateAmbassador.discoverObjectInstance(
                            ((DiscoverObjectInstance) message).getObject(),
                            ((DiscoverObjectInstance) message).getObjectClass(),
                            ((DiscoverObjectInstance) message).getObjectName());
                    break;

                case REFLECT_ATTRIBUTE_VALUES:
                    if (message.getFederationTime() != null) {
                        federateAmbassador.reflectAttributeValues(
                                ((ReflectAttributeValues) message).getObject(),
                                ((ReflectAttributeValues) message).getReflectedAttributes(),
                                message.getTag(),
                                message.getFederationTime(), message.getEventRetraction());
                    } else {
                        federateAmbassador.reflectAttributeValues(
                                ((ReflectAttributeValues) message).getObject(),
                                ((ReflectAttributeValues) message).getReflectedAttributes(),
                                message.getTag());
                    }

                    break;

                case RECEIVE_INTERACTION:
                    if (message.getFederationTime() != null) {
                        federateAmbassador.receiveInteraction(
                                ((ReceiveInteraction) message).getInteractionClass(),
                                ((ReceiveInteraction) message).getReceivedInteraction(),
                                (message.getTag()),
                                message.getFederationTime(),
                                message.getEventRetraction());
                    } else {
                        federateAmbassador.receiveInteraction(
                                ((ReceiveInteraction) message).getInteractionClass(),
                                ((ReceiveInteraction) message).getReceivedInteraction(),
                                (message.getTag()));
                    }
                    break;


                case REMOVE_OBJECT_INSTANCE:
                    if (message.getFederationTime() != null) {
                        federateAmbassador.removeObjectInstance(
                                ((RemoveObjectInstance) message).getObject(),
                                message.getTag(),
                                message.getFederationTime(),
                                message.getEventRetraction());
                    } else {
                        federateAmbassador.removeObjectInstance(
                                ((RemoveObjectInstance) message).getObject(),
                                message.getTag());
                    }
                    break;


                case PROVIDE_ATTRIBUTE_VALUE_UPDATE:
                    federateAmbassador.provideAttributeValueUpdate(
                            ((ProvideAttributeValueUpdate) message).getObject(),
                            ((ProvideAttributeValueUpdate) message).getAttributes());
                    break;

                case REQUEST_RETRACTION:
                    break;

                case REQUEST_ATTRIBUTE_OWNERSHIP_ASSUMPTION:
                    federateAmbassador.requestAttributeOwnershipAssumption(
                            ((RequestAttributeOwnershipAssumption) message).getObject(),
                            ((RequestAttributeOwnershipAssumption) message).getAttributes(),
                            message.getTag());
                    break;

                case REQUEST_ATTRIBUTE_OWNERSHIP_RELEASE:
                    federateAmbassador.requestAttributeOwnershipRelease(
                            ((RequestAttributeOwnershipRelease) message).getObject(),
                            ((RequestAttributeOwnershipRelease) message).getAttributes(),
                            message.getTag());
                    break;

                case ATTRIBUTE_OWNERSHIP_UNAVAILABLE:
                    federateAmbassador.attributeOwnershipUnavailable(
                            ((AttributeOwnershipUnavailable) message).getObject(),
                            (((AttributeOwnershipUnavailable) message).getAttributes()));
                    break;

                case ATTRIBUTE_OWNERSHIP_ACQUISITION_NOTIFICATION:
                    federateAmbassador.attributeOwnershipAcquisitionNotification(
                            ((AttributeOwnershipAcquisitionNotification) message).getObject(),
                            ((AttributeOwnershipAcquisitionNotification) message).getAttributes());
                    break;

                case ATTRIBUTE_OWNERSHIP_DIVESTITURE_NOTIFICATION:
                    federateAmbassador.attributeOwnershipDivestitureNotification(
                            ((AttributeOwnershipDivestitureNotification) message).getObject(),
                            ((AttributeOwnershipDivestitureNotification) message).getAttributes());
                    break;

                case CONFIRM_ATTRIBUTE_OWNERSHIP_ACQUISITION_CANCELLATION:
                    federateAmbassador.confirmAttributeOwnershipAcquisitionCancellation(
                            ((ConfirmAttributeOwnershipAcquisitionCancellation) message).getObject(),
                            ((ConfirmAttributeOwnershipAcquisitionCancellation) message).getAttributes());
                    break;

                case INFORM_ATTRIBUTE_OWNERSHIP:
                    federateAmbassador.informAttributeOwnership(
                            ((InformAttributeOwnership) message).getObject(),
                            ((InformAttributeOwnership) message).getAttribute(),
                            ((InformAttributeOwnership) message).getFederate());
                    break;

                case ATTRIBUTE_IS_NOT_OWNED:
                    federateAmbassador.attributeIsNotOwned(
                            ((AttributeIsNotOwned) message).getObject(),
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
