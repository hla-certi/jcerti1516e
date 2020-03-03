package certi.rti1516e.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import certi.communication.CertiException;
import certi.communication.CertiMessage;
import certi.communication.CertiMessageType;
import certi.communication.messages1516E.AttributeIsNotOwned1516E;
import certi.communication.messages1516E.AttributeOwnershipAcquisitionNotification1516E;
import certi.communication.messages1516E.AttributeOwnershipUnavailable1516E;
import certi.communication.messages1516E.CertiMessage1516E;
import certi.communication.messages1516E.CloseConnexion1516E;
import certi.communication.messages1516E.ConfirmAttributeOwnershipAcquisitionCancellation1516E;
import certi.communication.messages1516E.CreateFederationExecution1516E;
import certi.communication.messages1516E.CreateFederationExecution1516E_V2;
import certi.communication.messages1516E.DeleteObjectInstance1516E;
import certi.communication.messages1516E.DestroyFederationExecution1516E;
import certi.communication.messages1516E.DisableAsynchronousDelivery1516E;
import certi.communication.messages1516E.DisableTimeConstrained1516E;
import certi.communication.messages1516E.DisableTimeRegulation1516E;
import certi.communication.messages1516E.DiscoverObjectInstance1516E;
import certi.communication.messages1516E.EnableAsynchronousDelivery1516E;
import certi.communication.messages1516E.EnableTimeConstrained1516E;
import certi.communication.messages1516E.EnableTimeRegulation1516E;
import certi.communication.messages1516E.GetAttributeHandle1516E;
import certi.communication.messages1516E.GetAttributeName1516E;
import certi.communication.messages1516E.GetInteractionClassHandle1516E;
import certi.communication.messages1516E.GetInteractionClassName1516E;
import certi.communication.messages1516E.GetObjectClassHandle1516E;
import certi.communication.messages1516E.GetObjectClassName1516E;
import certi.communication.messages1516E.GetObjectInstanceHandle1516E;
import certi.communication.messages1516E.GetObjectInstanceName1516E;
import certi.communication.messages1516E.GetParameterHandle1516E;
import certi.communication.messages1516E.GetParameterName1516E;
import certi.communication.messages1516E.InformAttributeOwnership1516E;
import certi.communication.messages1516E.InitiateFederateRestore1516E;
import certi.communication.messages1516E.JoinFederationExecution1516E;
import certi.communication.messages1516E.JoinFederationExecution1516E_V2;
import certi.communication.messages1516E.LocalDeleteObjectInstance1516E;
import certi.communication.messages1516E.MessageBuffer1516E;
import certi.communication.messages1516E.MessageFactory1516E;
import certi.communication.messages1516E.NextEventRequest1516E;
import certi.communication.messages1516E.NextEventRequestAvailable1516E;
import certi.communication.messages1516E.OpenConnexion1516E;
import certi.communication.messages1516E.ProvideAttributeValueUpdate1516E;
import certi.communication.messages1516E.PublishInteractionClass1516E;
import certi.communication.messages1516E.PublishObjectClass1516E;
import certi.communication.messages1516E.ReceiveInteraction1516E;
import certi.communication.messages1516E.ReflectAttributeValues1516E;
import certi.communication.messages1516E.RegisterFederationSynchronizationPoint1516E;
import certi.communication.messages1516E.RegisterObjectInstance1516E;
import certi.communication.messages1516E.RemoveObjectInstance1516E;
import certi.communication.messages1516E.RequestAttributeOwnershipAssumption1516E;
import certi.communication.messages1516E.RequestAttributeOwnershipRelease1516E;
import certi.communication.messages1516E.RequestObjectAttributeValueUpdate1516E;
import certi.communication.messages1516E.ResignAction1516E;
import certi.communication.messages1516E.ResignFederationExecution1516E;
import certi.communication.messages1516E.SendInteraction1516E;
import certi.communication.messages1516E.StartRegistrationForObjectClass1516E;
import certi.communication.messages1516E.StopRegistrationForObjectClass1516E;
import certi.communication.messages1516E.SubscribeInteractionClass1516E;
import certi.communication.messages1516E.SubscribeObjectClassAttributes1516E;
import certi.communication.messages1516E.SynchronizationPointAchieved1516E;
import certi.communication.messages1516E.TickRequest1516E;
import certi.communication.messages1516E.TickRequestNext1516E;
import certi.communication.messages1516E.TickRequestStop1516E;
import certi.communication.messages1516E.TimeAdvanceRequest1516E;
import certi.communication.messages1516E.TimeAdvanceRequestAvailable1516E;
import certi.communication.messages1516E.TurnInteractionsOff1516E;
import certi.communication.messages1516E.TurnInteractionsOn1516E;
import certi.communication.messages1516E.UnpublishInteractionClass1516E;
import certi.communication.messages1516E.UnpublishObjectClass1516E;
import certi.communication.messages1516E.UnsubscribeInteractionClass1516E;
import certi.communication.messages1516E.UnsubscribeObjectClass1516E;
import certi.communication.messages1516E.UpdateAttributeValues1516E;
import certi.logging.HtmlFormatter;
import certi.logging.StreamListener;
import certi.rti.impl.CertiEventRetractionHandle;
import certi.rti.impl.CertiHandleValuePairCollection;
import hla.rti.ReflectedAttributes;
import hla.rti1516.InteractionClassNotSubscribed;
import hla.rti1516.SpecifiedSaveLabelDoesNotExist;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleFactory;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.AttributeHandleValueMapFactory;
import hla.rti1516e.AttributeSetRegionSetPairList;
import hla.rti1516e.AttributeSetRegionSetPairListFactory;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.DimensionHandle;
import hla.rti1516e.DimensionHandleFactory;
import hla.rti1516e.DimensionHandleSet;
import hla.rti1516e.DimensionHandleSetFactory;
import hla.rti1516e.FederateAmbassador;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleFactory;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.FederateHandleSetFactory;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.InteractionClassHandleFactory;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeFactory;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.MessageRetractionReturn;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectClassHandleFactory;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ObjectInstanceHandleFactory;
import hla.rti1516e.OrderType;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleFactory;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.ParameterHandleValueMapFactory;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RangeBounds;
import hla.rti1516e.RegionHandle;
import hla.rti1516e.RegionHandleSet;
import hla.rti1516e.RegionHandleSetFactory;
import hla.rti1516e.ResignAction;
import hla.rti1516e.ServiceGroup;
import hla.rti1516e.TimeQueryReturn;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.TransportationTypeHandleFactory;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.impl.CertiAttributeHandleSetFactory;
import hla.rti1516e.impl.CertiAttributeHandleValueMap;
import hla.rti1516e.impl.CertiAttributeHandleValueMapFactory;
import hla.rti1516e.impl.CertiObjectHandle;


/**
 * Implementation of RTIambassador interface
 * Most of the Javadoc here is from the standard IEEE Std 1516.1-2010
 */
public class CertiRtiAmbassador implements RTIambassador {
    private Socket socket;
    private MessageBuffer1516E messageBuffer;
    private final static Logger LOGGER = Logger.getLogger(CertiRtiAmbassador.class.getName());
    private final static int SOCKET_TIMEOUT = 60000; // Socket timeout in milliseconds.
    private FederateAmbassador federateAmbassador;
    private Process rtiaProcess;


    class JavaMachineHook extends Thread {

        private CertiRtiAmbassador rtia;

        public JavaMachineHook(CertiRtiAmbassador rtia) {
            super("Certi Java machine hook");
            this.rtia = rtia;
        }

        @Override
        public void run() {
            super.run();
//            try {
//                rtia.disconnect();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

    /**
     * The Connect service shall be used to establish a connection between the unjoined federate and the RTI. No
     * interaction between the unjoined federate and the RTI may take place until a successful invocation of the
     * Connect service has been performed. If the optional local settings designator is not present, the default local
     * settings shall be used.
     * The callback model argument indicates the desired callback model, either IMMEDIATE or EVOKED.
     * @param federateReference : reference to the federate
     * @param callbackModel : model to use for callbacks
     * @param localSettingsDesignator : stringh witch contain the name of a system property. If defined, we load a
     * properties file and use it to augment/override the RID
     */
    @Override
    public void connect(FederateAmbassador federateReference, CallbackModel callbackModel, String localSettingsDesignator)
            throws ConnectionFailed,
            InvalidLocalSettingsDesignator,
            UnsupportedCallbackModel,
            AlreadyConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        if (federateReference == null)
            throw new RTIinternalError("Federate reference is null");

        /////////////////////////
        // Load properties file //
        //////////////////////////
        Properties properties = new Properties();

        if(localSettingsDesignator == null) localSettingsDesignator = "certi.properties";

        try {
            properties.load(new FileInputStream(localSettingsDesignator));
        } catch (FileNotFoundException ex) {
            //Property file not found - not a big deal
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
            // Reads to this socket block, possibly forever.
            // Set a timeout of one minute. When this expires, java.net.SocketTimeoutException
            // will be thrown.
            serverSocket.setSoTimeout(SOCKET_TIMEOUT);
        } catch (IOException exception) {
            throw new RTIinternalError("Creating server socket failed. " + exception.getLocalizedMessage());
        }
        LOGGER.info("Using TCP socket server on port " + serverSocket.getLocalPort());


        try {
            String rtiaPathString = properties.getProperty("rtiaPath") != null ? properties.getProperty("rtiaPath") : "";
            rtiaProcess = Runtime.getRuntime().exec(rtiaPathString + "rtia -p " + serverSocket.getLocalPort());

            // Read error and output streams, so that in case debugging is enabled for RTIA
            // the process will not block because stream buffers are full
            StreamListener outListener = new StreamListener(rtiaProcess.getInputStream());
            StreamListener errListener = new StreamListener(rtiaProcess.getErrorStream());
            outListener.start();
            errListener.start();
        } catch (IOException exception) {
            throw new RTIinternalError("RTI Ambassador executable not found. " + exception.getLocalizedMessage());
        }



        try {
            socket = serverSocket.accept();
            // Reads to this socket block, possibly forever.
            // Set a timeout of one minute. When this expires, java.net.SocketTimeoutException
            // will be thrown.
            socket.setSoTimeout(SOCKET_TIMEOUT);
            messageBuffer = new MessageBuffer1516E(socket.getInputStream(), socket.getOutputStream());
        } catch (IOException exception) {
            throw new RTIinternalError("Connection to RTIA failed. " + exception.getLocalizedMessage());
        }

        ////////////////////
        // Open connexion //
        ////////////////////
        try {
            OpenConnexion1516E openConnexion = new OpenConnexion1516E();
            openConnexion.setVersionMinor(0);
            openConnexion.setVersionMajor(1);
            processRequest(openConnexion);
        } catch (Exception exception) {
            throw new RTIinternalError("Connection to RTIA failed. " + exception.getLocalizedMessage());
        }


        federateAmbassador = federateReference;
        ///////////////////////////////
        // Prepare Java runtime hook //
        ///////////////////////////////
        Runtime.getRuntime().addShutdownHook(new JavaMachineHook(this));

    }

    /**
     * The Connect service shall be used to establish a connection between the unjoined federate and the RTI. No
     * interaction between the unjoined federate and the RTI may take place until a successful invocation of the
     * Connect service has been performed. If the optional local settings designator is not present, the default local
     * settings shall be used.
     * The callback model argument indicates the desired callback model, either IMMEDIATE or EVOKED.
     * @param federateReference : reference to the federate
     * @param callbackModel : model to use for callbacks
     */
    @Override
    public void connect(FederateAmbassador federateReference, CallbackModel callbackModel)
            throws ConnectionFailed,
            InvalidLocalSettingsDesignator,
            UnsupportedCallbackModel,
            AlreadyConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        this.connect(federateReference, callbackModel, null);
    }

    /**
     * The Disconnect service shall be used to terminate the connection between a federate and the RTI.
     * The federate cannot be a member of the execution.
     * The disconnect check is the RTIA is really destroyed after the closing request, and if not destroy it
     */
    @Override
    public void disconnect() throws FederateIsExecutionMember, CallNotAllowedFromWithinCallback, RTIinternalError {
        if(rtiaProcess == null){
//            throw new CallNotAllowedFromWithinCallback("RTIA not connected");
            return;
        }
        CloseConnexion1516E request = new CloseConnexion1516E();
        try {
            LOGGER.info("Closing connection to RTIA");
            processRequest(request);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error with closing connection", ex);
        }
        //Check if rtia process closed, if not kill it
        int i = 10;
        if(rtiaProcess.isAlive() && i > 0){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(rtiaProcess.isAlive())
            rtiaProcess.destroy();

        try{
            this.federateAmbassador = null;
            socket.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /** Create a federation execution with the specified name and FDD file
     *  and add it to the set of supported federation executions.
     *  Each federation execution created by this service shall be
     *  independent of all other federation executions, and there shall be no
     *  intercommunication within the RTI between federation executions.
     *  The federation name must be unique. This method will throw an exception
     *  if a federation already exists with the specified name.
     *
     *  This method assumes that the RTIG will have access to the FDD file
     *  on its local file system. If the fddFile argument is not an absolute
     *  path, then the CERTI RTIG will search for the file on a path specified by
     *  its CERTI_FOM_PATH environment variable.
     *  As of this writing, CERTI does not support specifying the FDD file
     *  as a network resource like a URL or URI, so specifying the path
     *  here is your only option.
     *
     *  NOTE: This method signature is not in the standard for the Java API.
     *  See https://www.sisostds.org/APIs.aspx (IEEE1516-2010_Java_API.zip).
     *  Instead, the standard specifies the versions below, which take URLs
     *  as arguments. However, CERTI's RTIG does not support retrieving these
     *  files from a URL, so the methods below will not work.
     *
     * @param federationExecutionName : name of the federation execution
     * @param fddFilename : The file name or path and file name of the FED file on
     *   the machine that is executing the RTIG.
     */
    public void createFederationExecution(String federationExecutionName, String fddFilename)
            throws  ErrorReadingFDD,
                    CouldNotOpenFDD,
                    FederationExecutionAlreadyExists,
                    NotConnected,
                    RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(federationExecutionName == null || federationExecutionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federation execution name");
        }

        if(fddFilename == null || fddFilename == "") {
            throw new RTIinternalError("Incorrect or empty FDD file name");
        }

        CreateFederationExecution1516E_V2 request = new CreateFederationExecution1516E_V2();
        request.setFederationName(federationExecutionName);
        request.setFomName(fddFilename);

        try {
            processRequest(request);
        } catch (FederationExecutionAlreadyExists ex) {
            throw ex;
        } catch (CouldNotOpenFDD ex) {
            throw ex;
        } catch (ErrorReadingFDD ex) {
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

    // FIXME: The methods below that take multiple URLs need versions that
    // take multiple strings, assuming that CERTI supports those (does it?).

    /**
     * The following implementations do not work if the federation is running in
     * different computers. It is kept because follow the Java API but it does not
     * work with CERTI. From the standard:
     * 'The Create Federation Execution service shall create a new federation execution and add it to the set of
     * supported federation executions. Each federation execution created by this service shall be independent of
     * all other federation executions, and there shall be no intercommunication within the RTI between federation
     * executions.
     * The RTI shall load the supplied MIM if specified; otherwise, it shall automatically load the standard MIM
     * (see Annex G). The MIM shall be loaded before any supplied FOM module is loaded. If a MIM argument is
     * supplied, the supplied MIM module designator shall not be “HLAstandardMIM.”
     * At least one FOM module shall be supplied.
     * The FOM modules supplied, together with the default or supplied MIM, shall result in a compliant FDD.
     * The FOM module designators argument identifies the FOM modules that, together with the MIM, shall
     * furnish the FDD for the federation execution to be created.'
     * @param federationExecutionName : name of the federation execution
     * @param fomModules : set of FOM module designators
     * @param mimModule : optional MIM designator
     * @param logicalTimeImplementationName : Optional logical time implementation. If not provided, the RTI provided HLAfloat64Time
     * representation of logical time shall be used.
     * @deprecated Use {@link createFederationExecution(String,String)}.
     */
    @Override
    @Deprecated
    public void createFederationExecution(  String federationExecutionName,
                                            URL[] fomModules,
                                            URL mimModule,
                                            String logicalTimeImplementationName)
            throws CouldNotCreateLogicalTimeFactory,
            InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            ErrorReadingMIM,
            CouldNotOpenMIM,
            DesignatorIsHLAstandardMIM,
            FederationExecutionAlreadyExists,
            NotConnected,
            RTIinternalError
    {
        if(federationExecutionName == null || federationExecutionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federation execution name");
        }

        if(fomModules == null || fomModules.length == 0) {
            throw new RTIinternalError("Incorrect or empty fomModule file name");
        }

        if(logicalTimeImplementationName != null) {
            if(!logicalTimeImplementationName.equals("HLAfloat64Time")
                    && !logicalTimeImplementationName.equals("HLAinteger64Time"))
                throw new CouldNotCreateLogicalTimeFactory("");
        }
        CreateFederationExecution1516E request = new CreateFederationExecution1516E();
        request.setFederationName(federationExecutionName);
        request.setFomModules(fomModules);

        if(mimModule != null) {
            if(mimModule.toString().equals("HLAstandardMIM.xml")) {
                throw new DesignatorIsHLAstandardMIM("MIM designator shall not be HLAstandardMIM");
            }
            request.setMimDesignator(mimModule);
        }

        if(logicalTimeImplementationName != null ) request.setLogicalTimeImplName(logicalTimeImplementationName);

        try {
            processRequest(request);
        } catch (FederationExecutionAlreadyExists ex) {
            throw ex;
        } catch (CouldNotOpenFDD ex) {
            throw ex;
        } catch (ErrorReadingFDD ex) {
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

    /**
     * The Create Federation Execution service shall create a new federation execution and add it to the set of
     * supported federation executions. Each federation execution created by this service shall be independent of
     * all other federation executions, and there shall be no intercommunication within the RTI between federation
     * executions.
     * The RTI shall load the supplied MIM if specified; otherwise, it shall automatically load the standard MIM
     * (see Annex G). The MIM shall be loaded before any supplied FOM module is loaded. If a MIM argument is
     * supplied, the supplied MIM module designator shall not be “HLAstandardMIM.”
     * At least one FOM module shall be supplied.
     * The FOM modules supplied, together with the default or supplied MIM, shall result in a compliant FDD.
     * The FOM module designators argument identifies the FOM modules that, together with the MIM, shall
     * furnish the FDD for the federation execution to be created.
     * @param federationExecutionName : name of the federation execution
     * @param fomModules : set of FOM module designators
     * @param logicalTimeImplementationName : Optional logical time implementation. If not provided, the RTI provided HLAfloat64Time
     * representation of logical time shall be used.
     * @deprecated Use {@link createFederationExecution(String,String)}.
     */
    @Override
    @Deprecated
    public void createFederationExecution(  String federationExecutionName,
                                            URL[] fomModules,
                                            String logicalTimeImplementationName)
            throws  CouldNotCreateLogicalTimeFactory,
            InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            FederationExecutionAlreadyExists,
            NotConnected,
            RTIinternalError
    {
        try {
            createFederationExecution(federationExecutionName, fomModules, null, logicalTimeImplementationName);
        } catch (ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM e) {
            e.printStackTrace();
        }

    }

    /**
     * The Create Federation Execution service shall create a new federation execution and add it to the set of
     * supported federation executions. Each federation execution created by this service shall be independent of
     * all other federation executions, and there shall be no intercommunication within the RTI between federation
     * executions.
     * The RTI shall load the supplied MIM if specified; otherwise, it shall automatically load the standard MIM
     * (see Annex G). The MIM shall be loaded before any supplied FOM module is loaded. If a MIM argument is
     * supplied, the supplied MIM module designator shall not be “HLAstandardMIM.”
     * At least one FOM module shall be supplied.
     * The FOM modules supplied, together with the default or supplied MIM, shall result in a compliant FDD.
     * The FOM module designators argument identifies the FOM modules that, together with the MIM, shall
     * furnish the FDD for the federation execution to be created.
     * @param federationExecutionName : name of the federation execution
     * @param fomModules : set of FOM module designators
     * @param mimModule : optional MIM designator
     * @deprecated Use {@link createFederationExecution(String,String)}.
     */
    @Override
    @Deprecated
    public void createFederationExecution(String federationExecutionName,
                                          URL[] fomModules,
                                          URL mimModule)
            throws  InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            ErrorReadingMIM,
            CouldNotOpenMIM,
            DesignatorIsHLAstandardMIM,
            FederationExecutionAlreadyExists,
            NotConnected,
            RTIinternalError
    {
        try {
            createFederationExecution(federationExecutionName, fomModules, mimModule, null);
        } catch (CouldNotCreateLogicalTimeFactory e) {
            e.printStackTrace();
        }

    }

    /**
     * The Create Federation Execution service shall create a new federation execution and add it to the set of
     * supported federation executions. Each federation execution created by this service shall be independent of
     * all other federation executions, and there shall be no intercommunication within the RTI between federation
     * executions.
     * The RTI shall load the supplied MIM if specified; otherwise, it shall automatically load the standard MIM
     * (see Annex G). The MIM shall be loaded before any supplied FOM module is loaded. If a MIM argument is
     * supplied, the supplied MIM module designator shall not be “HLAstandardMIM.”
     * At least one FOM module shall be supplied.
     * The FOM modules supplied, together with the default or supplied MIM, shall result in a compliant FDD.
     * The FOM module designators argument identifies the FOM modules that, together with the MIM, shall
     * furnish the FDD for the federation execution to be created.
     * @param federationExecutionName : name of the federation execution
     * @param fomModules : set of FOM module designators
     * @deprecated Use {@link createFederationExecution(String,String)}.
     */
    @Override
    @Deprecated
    public void createFederationExecution(  String federationExecutionName,
                                            URL[] fomModules)
            throws InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            FederationExecutionAlreadyExists,
            NotConnected,
            RTIinternalError
    {
        try {
            createFederationExecution(federationExecutionName, fomModules, null, null);
        } catch (CouldNotCreateLogicalTimeFactory | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM e) {
            e.printStackTrace();
        }
    }

    /**
     * The Create Federation Execution service shall create a new federation execution and add it to the set of
     * supported federation executions. Each federation execution created by this service shall be independent of
     * all other federation executions, and there shall be no intercommunication within the RTI between federation
     * executions.
     * The RTI shall load the supplied MIM if specified; otherwise, it shall automatically load the standard MIM
     * (see Annex G). The MIM shall be loaded before any supplied FOM module is loaded. If a MIM argument is
     * supplied, the supplied MIM module designator shall not be “HLAstandardMIM.”
     * At least one FOM module shall be supplied.
     * The FOM modules supplied, together with the default or supplied MIM, shall result in a compliant FDD.
     * The FOM module designators argument identifies the FOM modules that, together with the MIM, shall
     * furnish the FDD for the federation execution to be created.
     * @param federationExecutionName : name of the federation execution
     * @param fomModule : FOM module designator
     * @deprecated Use {@link createFederationExecution(String,String)}.
     */
    @Override
    //@Deprecated
    public void createFederationExecution(  String federationExecutionName,
                                            URL fomModule)
            throws InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            FederationExecutionAlreadyExists,
            NotConnected,
            RTIinternalError
    {
        URL[] fomModules = {fomModule};
        try {
            createFederationExecution(federationExecutionName, fomModules, null, null);
        } catch (CouldNotCreateLogicalTimeFactory | ErrorReadingMIM | CouldNotOpenMIM | DesignatorIsHLAstandardMIM e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroyFederationExecution(String federationExecutionName)
            throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        DestroyFederationExecution1516E request = new DestroyFederationExecution1516E();
        request.setFederationName(federationExecutionName);
        try {
            processRequest(request);
        } catch (FederatesCurrentlyJoined ex) {
            throw ex;
        } catch (FederationExecutionDoesNotExist ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (NoSuchElementException ex) {
            // Incomplete response was received.
            // Assume that the federation has been destroyed and ignore.
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public void listFederationExecutions() throws NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }


     /** The argument that has to be passed to joinFederationExecution() is,
     * according to the HLA 1516-2010 standard, the location of FOM modules on
     * a possibly _remote_ file system.
     * See https://www.sisostds.org/APIs.aspx (IEEE1516-2010_Java_API.zip),
     * java/src/hla/rti1516e/RTIambassador.java where is defined the method
     * joinFederationExecution(String federationExecutionName, URL[] fomModules),
     * CERTI is written in C++ and is compliant with the C++ API that defines the
     * method with a string (std::wstring const & fomModule), where the FOM
     * module is not a network resource. It is a private resource to the RTIG on
     * the local filesystem where the RTIG is running.
     * From the standard: 'The Join Federation Execution service shall affiliate the federate with a federation execution. Invocation of
     * the Join Federation Execution service shall indicate the intention to participate in the specified federation.
     * The federate name argument shall be unique within a federation execution at any given time. It may,
     * however, be assigned to a different federate after a restore operation. The RTI shall provide a unique name if
     * no federate name is provided, and it is the responsibility of the federate to obtain and preserve this unique
     * name for potential restore operations. The federate type argument shall distinguish federate categories for
     * federation save-and-restore purposes. The returned joined federate designator shall be unique for the lifetime
     * of the federation execution as long as a restore is not in progress at any federate.
     * In addition, the Join Federation Execution service may also be used to provide additional FOM module
     * designators. The optional set of additional FOM module designators argument identifies the FOM modules
     * that provide additional FDD. Contents of the FOM modules may duplicate information in the current FDD
     * of the federation execution, but they shall not conflict with the current FDD.'
     * @param federateName : Optional federate name. If not provided, the RTI shall assign a unique name.
     * @param federateType : Federate type.
     * @param federationExecutionName : Federation execution name.
     * @param additionalFomModules : Optional set of additional FOM module designators.
     * @return Joined federate designator
     */
    public FederateHandle joinFederationExecution(  String federateName,
                                                    String federateType,
                                                    String federationExecutionName,
                                                    String[] additionalFomModules)
            throws
            FederationExecutionDoesNotExist,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            SaveInProgress,
            RestoreInProgress,
            FederateAlreadyExecutionMember,
            NotConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }

        if (federateName == null || federateName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federate name");
        } //-> ne pas lever d'erreur, lui assigner un nom unique
        if (federationExecutionName == null || federationExecutionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federation name");
        }

        JoinFederationExecution1516E_V2 request = new JoinFederationExecution1516E_V2();
        request.setFederateName(federateName);
        request.setFederateType(federateType);
        request.setFederationName(federationExecutionName);
        request.setAdditionalFomModules(additionalFomModules);

        try {
            JoinFederationExecution1516E response = (JoinFederationExecution1516E) processRequest(request);
            return new CertiObjectHandle( (Integer)response.getFederate() );
        } catch (CouldNotOpenFDD ex) {
            throw ex;
        } catch (ErrorReadingFDD ex) {
            throw ex;
        } catch (CallNotAllowedFromWithinCallback ex) {
            throw ex;
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The following implementations do not work if the federation is running in
     * different computers. It is kept because follow the Java API but it does not
     * work with CERTI. From the standard:
     * 'The Join Federation Execution service shall affiliate the federate with a federation execution. Invocation of
     * the Join Federation Execution service shall indicate the intention to participate in the specified federation.
     * The federate name argument shall be unique within a federation execution at any given time. It may,
     * however, be assigned to a different federate after a restore operation. The RTI shall provide a unique name if
     * no federate name is provided, and it is the responsibility of the federate to obtain and preserve this unique
     * name for potential restore operations. The federate type argument shall distinguish federate categories for
     * federation save-and-restore purposes. The returned joined federate designator shall be unique for the lifetime
     * of the federation execution as long as a restore is not in progress at any federate.
     * In addition, the Join Federation Execution service may also be used to provide additional FOM module
     * designators. The optional set of additional FOM module designators argument identifies the FOM modules
     * that provide additional FDD. Contents of the FOM modules may duplicate information in the current FDD
     * of the federation execution, but they shall not conflict with the current FDD.'
     * @param federateName : Optional federate name. If not provided, the RTI shall assign a unique name.
     * @param federateType : Federate type.
     * @param federationExecutionName : Federation execution name.
     * @param additionalFomModules : Optional set of additional FOM module designators.
     * @return Joined federate designator
     */
    @Override
    public FederateHandle joinFederationExecution(  String federateName,
                                                    String federateType,
                                                    String federationExecutionName,
                                                    URL[] additionalFomModules)
            throws  CouldNotCreateLogicalTimeFactory,
            FederateNameAlreadyInUse,
            FederationExecutionDoesNotExist,
            InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            SaveInProgress,
            RestoreInProgress,
            FederateAlreadyExecutionMember,
            NotConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }

        if (federateName == null || federateName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federate name");
        } //-> ne pas lever d'erreur, lui assigner un nom unique
        if (federationExecutionName == null || federationExecutionName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty federation name");
        }

        JoinFederationExecution1516E request = new JoinFederationExecution1516E();
        request.setFederateName(federateName);
        request.setFederateType(federateType);
        request.setFederationName(federationExecutionName);
        request.setAdditionalFomModules(additionalFomModules);

        try {
            JoinFederationExecution1516E response = (JoinFederationExecution1516E) processRequest(request);
            return new CertiObjectHandle( (Integer)response.getFederate() );
        } catch (CouldNotOpenFDD ex) {
            throw ex;
        } catch (ErrorReadingFDD ex) {
            throw ex;
        } catch (CallNotAllowedFromWithinCallback ex) {
            throw ex;
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Join Federation Execution service shall affiliate the federate with a federation execution. Invocation of
     * the Join Federation Execution service shall indicate the intention to participate in the specified federation.
     * The federate name argument shall be unique within a federation execution at any given time. It may,
     * however, be assigned to a different federate after a restore operation. The RTI shall provide a unique name if
     * no federate name is provided, and it is the responsibility of the federate to obtain and preserve this unique
     * name for potential restore operations. The federate type argument shall distinguish federate categories for
     * federation save-and-restore purposes. The returned joined federate designator shall be unique for the lifetime
     * of the federation execution as long as a restore is not in progress at any federate.
     * In addition, the Join Federation Execution service may also be used to provide additional FOM module
     * designators. The optional set of additional FOM module designators argument identifies the FOM modules
     * that provide additional FDD. Contents of the FOM modules may duplicate information in the current FDD
     * of the federation execution, but they shall not conflict with the current FDD.
     * @param federateType : Federate type.
     * @param federationExecutionName : Federation execution name.
     * @param additionalFomModules : Optional set of additional FOM module designators.
     * @return Joined federate designator
     */
    @Override
    public FederateHandle joinFederationExecution(  String federateType,
                                                    String federationExecutionName,
                                                    URL[] additionalFomModules)
            throws  CouldNotCreateLogicalTimeFactory,
            FederationExecutionDoesNotExist,
            InconsistentFDD,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            SaveInProgress,
            RestoreInProgress,
            FederateAlreadyExecutionMember,
            NotConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        try
        {
            //TODO Change default name by unique name when it will be donne in c++
            return joinFederationExecution( "defaultName",
                    federateType,
                    federationExecutionName,
                    additionalFomModules );
        }
        catch( FederateNameAlreadyInUse fniu )
        {
            // should not happen
            LOGGER.log(Level.SEVERE, "joinFederationExecution", fniu );
            return null;
        }
    }

    /**
     * The Join Federation Execution service shall affiliate the federate with a federation execution. Invocation of
     * the Join Federation Execution service shall indicate the intention to participate in the specified federation.
     * The federate name argument shall be unique within a federation execution at any given time. It may,
     * however, be assigned to a different federate after a restore operation. The RTI shall provide a unique name if
     * no federate name is provided, and it is the responsibility of the federate to obtain and preserve this unique
     * name for potential restore operations. The federate type argument shall distinguish federate categories for
     * federation save-and-restore purposes. The returned joined federate designator shall be unique for the lifetime
     * of the federation execution as long as a restore is not in progress at any federate.
     * In addition, the Join Federation Execution service may also be used to provide additional FOM module
     * designators. The optional set of additional FOM module designators argument identifies the FOM modules
     * that provide additional FDD. Contents of the FOM modules may duplicate information in the current FDD
     * of the federation execution, but they shall not conflict with the current FDD.
     * @param federateName : Optional federate name. If not provided, the RTI shall assign a unique name.
     * @param federateType : Federate type.
     * @param federationExecutionName : Federation execution name.
     * @return Joined federate designator
     */
    @Override
    public FederateHandle joinFederationExecution( String federateName,
                                                   String federateType,
                                                   String federationExecutionName)
            throws  CouldNotCreateLogicalTimeFactory,
            FederateNameAlreadyInUse,
            FederationExecutionDoesNotExist,
            SaveInProgress,
            RestoreInProgress,
            FederateAlreadyExecutionMember,
            NotConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        try
        {
            return joinFederationExecution( federateName, federateType, federationExecutionName, (URL[]) null );
        }
        catch( CouldNotOpenFDD cnof )
        {
            // should not happen
            LOGGER.log(Level.SEVERE, "joinFederationExecution", cnof );
            return null;
        }
        catch( InconsistentFDD ifdd )
        {
            // should not happen
            LOGGER.log(Level.SEVERE, "joinFederationExecution", ifdd );
            return null;
        }
        catch( ErrorReadingFDD erf )
        {
            // should not happen
            LOGGER.log(Level.SEVERE, "joinFederationExecution", erf );
            return null;
        }
    }


    /**
     * The Join Federation Execution service shall affiliate the federate with a federation execution. Invocation of
     * the Join Federation Execution service shall indicate the intention to participate in the specified federation.
     * The federate name argument shall be unique within a federation execution at any given time. It may,
     * however, be assigned to a different federate after a restore operation. The RTI shall provide a unique name if
     * no federate name is provided, and it is the responsibility of the federate to obtain and preserve this unique
     * name for potential restore operations. The federate type argument shall distinguish federate categories for
     * federation save-and-restore purposes. The returned joined federate designator shall be unique for the lifetime
     * of the federation execution as long as a restore is not in progress at any federate.
     * In addition, the Join Federation Execution service may also be used to provide additional FOM module
     * designators. The optional set of additional FOM module designators argument identifies the FOM modules
     * that provide additional FDD. Contents of the FOM modules may duplicate information in the current FDD
     * of the federation execution, but they shall not conflict with the current FDD.
     * @param federateType : Federate type.
     * @param federationExecutionName : Federation execution name.
     * @return Joined federate designator
     */
    @Override
    public FederateHandle joinFederationExecution( String federateType,
                                                   String federationExecutionName)
            throws  CouldNotCreateLogicalTimeFactory,
            FederationExecutionDoesNotExist,
            SaveInProgress, RestoreInProgress,
            FederateAlreadyExecutionMember,
            NotConnected,
            CallNotAllowedFromWithinCallback,
            RTIinternalError
    {
        try
        {
            //TODO Change default name by unique name when it will be donne in c++
            return joinFederationExecution("defaultName", federateType, federationExecutionName );
        }
        catch( FederateNameAlreadyInUse niu )
        {
            throw new RTIinternalError( niu.getMessage(), niu );
        }
    }

    /**
     * The Resign Federation Execution service shall indicate the requested cessation of federation participation.
     * Before resigning, ownership of instance attributes held by the joined federate should be resolved. The joined
     * federate may transfer ownership of these instance attributes to other joined federates, unconditionally divest
     * them for ownership acquisition at a later point, or delete the object instance of which they are a part
     * (assuming the joined federate has the privilege to delete these object instances).
     * @param resignAction : action directive
     */
    @Override
    public void resignFederationExecution(ResignAction resignAction)
            throws InvalidResignAction, OwnershipAcquisitionPending, FederateOwnsAttributes, FederateNotExecutionMember,
            NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
        ResignFederationExecution1516E request = new ResignFederationExecution1516E();
        request.setResignAction(ResignAction1516E.value(resignAction));
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        try {
            //ResignFederationExecution response = (ResignFederationExecution) processRequest(request);
            processRequest(request);
        } catch (FederateOwnsAttributes ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        } catch (InvalidResignAction ex) {
            throw ex;
        } catch (RTIinternalError ex) {
            throw ex;
        } catch (OwnershipAcquisitionPending ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (CallNotAllowedFromWithinCallback ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Register Federation Synchronization Point service shall be used to initiate the registration of an
     * upcoming synchronization point label. When a synchronization point label has been successfully registered
     * (indicated through the Confirm Synchronization Point Registration † service), the RTI shall inform some or
     * all joined federates of the label’s existence by invoking the Announce Synchronization Point † service at
     * those joined federates. The optional set of joined federate designators shall be provided by the joined
     * federate if only a subset of joined federates in the federation execution are to be informed of the label
     * existence. If the set is provided, joined federates in the federation execution, which are members of the set,
     * shall be informed of the label existence. The set may contain valid federate designators of federates no
     * longer joined in the federation execution. If the optional set of joined federate designators is empty or not
     * supplied, all joined federates in the federation execution shall be informed of the label’s existence. If the
     * optional set of designators is not empty, all designated joined federates shall be federation execution
     * members. The user-supplied tag shall provide a vehicle for information to be associated with the
     * synchronization point and shall be announced along with the synchronization label. It is possible for
     * multiple synchronization points registered by the same or different joined federates to be pending at the
     * same point.
     * @param synchronizationPointLabel : Synchronization point label.
     * @param userSuppliedTag : User-supplied tag.
     */
    @Override
    public void registerFederationSynchronizationPoint( String synchronizationPointLabel,
                                                        byte[] userSuppliedTag)
            throws  SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }

        if (synchronizationPointLabel == null || synchronizationPointLabel.length() == 0) {
            throw new RTIinternalError("Incorrect or empty suncrhonization point label");
        }
        RegisterFederationSynchronizationPoint1516E request = new RegisterFederationSynchronizationPoint1516E();

        request.setLabel(synchronizationPointLabel);
        request.setTag(userSuppliedTag);
        //request.setBooleanValue(false); //without set of federates

        try {
            processRequest(request);
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

    /**
     * The Register Federation Synchronization Point service shall be used to initiate the registration of an
     * upcoming synchronization point label. When a synchronization point label has been successfully registered
     * (indicated through the Confirm Synchronization Point Registration † service), the RTI shall inform some or
     * all joined federates of the label’s existence by invoking the Announce Synchronization Point † service at
     * those joined federates. The optional set of joined federate designators shall be provided by the joined
     * federate if only a subset of joined federates in the federation execution are to be informed of the label
     * existence. If the set is provided, joined federates in the federation execution, which are members of the set,
     * shall be informed of the label existence. The set may contain valid federate designators of federates no
     * longer joined in the federation execution. If the optional set of joined federate designators is empty or not
     * supplied, all joined federates in the federation execution shall be informed of the label’s existence. If the
     * optional set of designators is not empty, all designated joined federates shall be federation execution
     * members. The user-supplied tag shall provide a vehicle for information to be associated with the
     * synchronization point and shall be announced along with the synchronization label. It is possible for
     * multiple synchronization points registered by the same or different joined federates to be pending at the
     * same point.
     * @param synchronizationPointLabel : Synchronization point label.
     * @param userSuppliedTag : User-supplied tag.
     * @param synchronizationSet : Optional set of joined federate designators.
     */
    @Override
    public void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag,
                                                       FederateHandleSet synchronizationSet) throws InvalidFederateHandle, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (synchronizationPointLabel == null || synchronizationPointLabel.length() == 0) {
            throw new RTIinternalError("Incorrect or empty suncrhonization point label");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling registerFederationSynchronizationPoint with Tag NULL");
        }
        if (synchronizationSet == null) {
            throw new RTIinternalError("Calling registerFederationSynchronizationPoint with synchronizationSet NULL");
        }

        RegisterFederationSynchronizationPoint1516E request = new RegisterFederationSynchronizationPoint1516E();

        request.setLabel(synchronizationPointLabel);
        request.setTag(userSuppliedTag);

        try {
            processRequest(request);
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

    /**
     * The Synchronization Point Achieved service shall inform the RTI that the joined federate has reached the
     * specified synchronization point. Once all joined federates in the synchronization set for a given point have
     * invoked this service, the RTI shall not invoke the Announce Synchronization Point † on any newly joining
     * federates. The optional synchronization-success indicator shall inform the RTI that the joined federate was
     * synchronized either successfully or unsuccessfully. The RTI shall consider the absence of the indicator as if
     * the joined federate synchronized successfully.
     * @param synchronizationPointLabel : label of the synchronization point
     */
    @Override
    public void synchronizationPointAchieved(String synchronizationPointLabel)
            throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        this.synchronizationPointAchieved(synchronizationPointLabel, true);
    }

    /**
     * The Synchronization Point Achieved service shall inform the RTI that the joined federate has reached the
     * specified synchronization point. Once all joined federates in the synchronization set for a given point have
     * invoked this service, the RTI shall not invoke the Announce Synchronization Point † on any newly joining
     * federates. The optional synchronization-success indicator shall inform the RTI that the joined federate was
     * synchronized either successfully or unsuccessfully. The RTI shall consider the absence of the indicator as if
     * the joined federate synchronized successfully.
     * @param synchronizationPointLabel : lable of the synchronization point
     * @param successIndicator : inform if the joined federate was synchronized either successfully or unsuccessfully
     */
    @Override
    public void synchronizationPointAchieved(String synchronizationPointLabel, boolean successIndicator)
            throws SynchronizationPointLabelNotAnnounced, SaveInProgress, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        SynchronizationPointAchieved1516E synchronizationPointAchieved = new SynchronizationPointAchieved1516E();
        synchronizationPointAchieved.setLabel(synchronizationPointLabel);

        try {
            processRequest(synchronizationPointAchieved);
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

    @Override
    public void requestFederationSave(String label)
            throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }


    @Override
    public void requestFederationSave(String label, LogicalTime theTime)
            throws LogicalTimeAlreadyPassed, InvalidLogicalTime, FederateUnableToUseTime, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void federateSaveBegun()
            throws SaveNotInitiated, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void federateSaveComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void federateSaveNotComplete() throws FederateHasNotBegunSave, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void abortFederationSave()
            throws SaveNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void queryFederationSaveStatus()
            throws RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void requestFederationRestore(String label)
            throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void federateRestoreComplete()
            throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void federateRestoreNotComplete()
            throws RestoreNotRequested, SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void abortFederationRestore()
            throws RestoreNotInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void queryFederationRestoreStatus()
            throws SaveInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    /**
     * The information provided by the joined federate via the Publish Object Class Attributes service shall be used
     * in multiple ways. First, it shall indicate an object class of which the joined federate may subsequently
     * register object instances. Second, it shall indicate the class attributes of the object class for which the joined
     * federate is capable of owning the corresponding instance attributes. Only the joined federate that owns an
     * instance attribute shall provide values for that instance attribute to the federation. The joined federate may
     * become the owner of an instance attribute and thereby be capable of updating its value in the following twoways:
     *  — By registering an object instance of a published class. Upon registration of an object instance, the
     *  registering joined federate shall become the owner of all instance attributes of that object instance
     *  for which the joined federate is publishing the corresponding class attributes at the registered class
     *  of the object instance.
     *  — By using ownership management services to acquire instance attributes of object instances. The
     *  joined federate may acquire only the instance attributes for which the joined federate is publishing
     *  the corresponding class attributes at the known class of the specified object instance.
     *  Each use of this service shall add to the publications specified to the RTI in previous service invocations for
     *  the same object class. Invoking this service with an empty set of class attributes shall be equivalent to adding
     *  no publications for the object class.
     * @param theClass : Object class designator.
     * @param attributeList : Set of attribute designators.
     */

    @Override
    public void publishObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
            throws AttributeNotDefined,
            ObjectClassNotDefined,
            SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        PublishObjectClass1516E request = new PublishObjectClass1516E();

        request.setObjectClass(theClass.hashCode());
        request.setAttributes(attributeList);

        try {
            processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Unpublish Interaction Class service shall inform the RTI that the joined federate will no longer send
     * interactions of the specified class.
     * @param theClass : class witch will no longer receive informations
     */
    @Override
    public void unpublishObjectClass(ObjectClassHandle theClass)
            throws OwnershipAcquisitionPending, ObjectClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        UnpublishObjectClass1516E request = new UnpublishObjectClass1516E();
        request.setObjectClass(theClass.hashCode());

        try {
            processRequest(request);
        } catch (ObjectClassNotDefined ex) {
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public void unpublishObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
            throws OwnershipAcquisitionPending, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     * The Publish Interaction Class service shall inform the RTI of the classes of interactions that the joined
     * federate will send to the federation execution.
     * @param theInteraction : Interaction class designator
     */
    @Override
    public void publishInteractionClass(InteractionClassHandle theInteraction) throws InteractionClassNotDefined,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        PublishInteractionClass1516E request = new PublishInteractionClass1516E();
        request.setInteractionClass(theInteraction.hashCode());
        try {
            processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Unpublish Interaction Class service shall inform the RTI that the joined federate will no longer send
     * interactions of the specified class.
     * @param theInteraction : Interaction class designator.
     */
    @Override
    public void unpublishInteractionClass(InteractionClassHandle theInteraction) throws InteractionClassNotDefined,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        UnpublishInteractionClass1516E request = new UnpublishInteractionClass1516E();
        request.setInteractionClass(theInteraction.hashCode());
        try {
            processRequest(request);

        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
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

    /**
     * The Subscribe Object Class Attributes service shall specify an object class at which the RTI shall notify the
     * joined federate of discovery of object instances. When subscribing to an object class, the joined federate
     * shall also provide a set of class attributes. The values of only the instance attributes that correspond to the
     * specified class attributes, for all object instances discovered as a result of this service invocation, shall be
     * provided to the joined federate from the RTI (via the Reflect Attribute Values † service). The set of class
     * attributes provided shall be a subset of the available attributes of the specified object class.
     * @param theClass : Object class designator.
     * @param attributeList : Set of attribute designators.
     */
    @Override
    public void subscribeObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
            throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        SubscribeObjectClassAttributes1516E request = new SubscribeObjectClassAttributes1516E();

        request.setObjectClass(theClass.hashCode());
        request.setAttributes(attributeList);

        try {
            processRequest(request);
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

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    /**
     * The Subscribe Object Class Attributes service shall specify an object class at which the RTI shall notify the
     * joined federate of discovery of object instances. When subscribing to an object class, the joined federate
     * shall also provide a set of class attributes. The values of only the instance attributes that correspond to the
     * specified class attributes, for all object instances discovered as a result of this service invocation, shall be
     * provided to the joined federate from the RTI (via the Reflect Attribute Values † service). The set of class
     * attributes provided shall be a subset of the available attributes of the specified object class.
     * A joined federate shall discover an object instance only as being of a class to which the joined federate is
     * subscribed.
     * @param theClass : Object class designator.
     * @param attributeList : Set of attribute designators.
     * @param updateRateDesignator : Optional update rate designator
     */
    @Override
    public void subscribeObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList,
                                               String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        SubscribeObjectClassAttributes1516E request = new SubscribeObjectClassAttributes1516E() ;
        request.setObjectClass(theClass.hashCode());
        request.setAttributes(attributeList);
        request.setActive(true);

        try {
            processRequest(request);
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

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public void subscribeObjectClassAttributesPassively(ObjectClassHandle theClass, AttributeHandleSet attributeList)
            throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subscribeObjectClassAttributesPassively(ObjectClassHandle theClass, AttributeHandleSet attributeList,
                                                        String updateRateDesignator) throws AttributeNotDefined, ObjectClassNotDefined, InvalidUpdateRateDesignator,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     * The Unsubscribe Object Class Attributes service can be used in multiple ways. First, it could be used to
     * unsubscribe a whole class and thereby inform the RTI that it shall stop notifying the joined federate of object
     * instance discovery at the specified object class. Second, it could be used to unsubscribe specific class
     * attributes and thereby inform the RTI that the joined federate shall no longer receive values for any of the
     * unsubscribed class attributes corresponding instance attributes.
     * If the optional set of attribute designators argument is supplied, only those class attributes shall be
     * unsubscribed for the joined federate. Otherwise, all subscribed class attributes of the specified class shall be
     * unsubscribed for the joined federate. Invoking this service with an empty set of class attributes shall be
     * equivalent to removing no subscriptions for the object class.
     * All in-scope instance attributes whose corresponding class attributes are unsubscribed by the service
     * invocation shall go out of scope. Refer to 9.1.1 for an expanded interpretation of this service when a joined
     * federate is using DDM services in conjunction with DM services.
     * Invocation of this service, subject to other conditions, may cause an implicit out-of-scope situation for
     * affected instance attributes, i.e., invoking joined federate shall NOT be notified via an invocation of the
     * Attributes Out Of Scope † service invocation when the instance attribute goes out of scope.
     * @param theClass : Object class designator.
     */
    @Override
    public void unsubscribeObjectClass(ObjectClassHandle theClass) throws ObjectClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        UnsubscribeObjectClass1516E request = new UnsubscribeObjectClass1516E();
        request.setObjectClass(theClass.hashCode());

        try {
            processRequest(request);
        } catch (ObjectClassNotDefined ex) {
            throw ex;
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

    @Override
    public void unsubscribeObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
            throws AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void subscribeInteractionClass(InteractionClassHandle theClass)
            throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        SubscribeInteractionClass1516E request = new SubscribeInteractionClass1516E();
        request.setInteractionClass(theClass.hashCode());
        try {
            this.processRequest(request);
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

        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public void subscribeInteractionClassPassively(InteractionClassHandle theClass)
            throws FederateServiceInvocationsAreBeingReportedViaMOM, InteractionClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     * The Unsubscribe Interaction Class service informs the RTI that it shall no longer notify the joined federate
     * of sent interactions of the specified interaction class. See 9.1.5 for an expanded interpretation of this service
     * when DDM is used.
     * @param theClass : Interaction class designator.
     */
    @Override
    public void unsubscribeInteractionClass(InteractionClassHandle theClass) throws InteractionClassNotDefined,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        UnsubscribeInteractionClass1516E request = new UnsubscribeInteractionClass1516E();
        request.setInteractionClass(theClass.hashCode());
        try {
            this.processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public void reserveObjectInstanceName(String theObjectName) throws IllegalName, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseObjectInstanceName(String theObjectInstanceName) throws ObjectInstanceNameNotReserved,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reserveMultipleObjectInstanceName(Set<String> theObjectNames) throws IllegalName, NameSetWasEmpty,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseMultipleObjectInstanceName(Set<String> theObjectNames) throws ObjectInstanceNameNotReserved,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     * The RTI shall create a federation execution-wide unique object instance handle and shall coadunate that
     * handle with an instance of the supplied object class. All instance attributes of the object instance for which
     * the corresponding class attributes are currently published by the registering joined federate shall be set as
     * owned by the registering joined federate.
     * An object instance handle shall be uniform throughout the federation execution; i.e., with respect to a
     * particular object instance, the handle provided to the joined federate that registers the object instance shall
     * be the same handle provided to all joined federates that discover the object instance. The phrase “the same
     * handle” shall denote handle equality rather than handle identity. Two handles shall be considered to be the
     * same if, according to the comparison operator in each of the APIs (for example, according to the “equals”
     * method in the Java API), the handles would be determined to be equal. The handles shall also have equality
     * between joined federates that are using different language APIs. The handles may be communicated
     * between joined federates via instance attributes or interaction parameters.
     * If the optional object instance name argument is supplied, that name shall have been successfully reserved
     * by the registering federate as indicated by the Object Instance Name Reserved † service and shall be
     * coadunated with the object instance. If the optional object instance name argument is not supplied, the RTI
     * shall create a federation execution-wide unique name, and that name shall be coadunated with the object
     * instance
     * @param theClass : Object class designator.
     * @return Object instance handle.
     */
    @Override
    public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass)
            throws ObjectClassNotPublished,
            ObjectClassNotDefined,
            SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        try {
            return registerObjectInstance(theClass, null);
        } catch (ObjectClassNotPublished ex) {
            throw ex;
        } catch (ObjectClassNotDefined ex) {
            throw ex;
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

    /**
     * The RTI shall create a federation execution-wide unique object instance handle and shall coadunate that
     * handle with an instance of the supplied object class. All instance attributes of the object instance for which
     * the corresponding class attributes are currently published by the registering joined federate shall be set as
     * owned by the registering joined federate.
     * An object instance handle shall be uniform throughout the federation execution; i.e., with respect to a
     * particular object instance, the handle provided to the joined federate that registers the object instance shall
     * be the same handle provided to all joined federates that discover the object instance. The phrase “the same
     * handle” shall denote handle equality rather than handle identity. Two handles shall be considered to be the
     * same if, according to the comparison operator in each of the APIs (for example, according to the “equals”
     * method in the Java API), the handles would be determined to be equal. The handles shall also have equality
     * between joined federates that are using different language APIs. The handles may be communicated
     * between joined federates via instance attributes or interaction parameters.
     * If the optional object instance name argument is supplied, that name shall have been successfully reserved
     * by the registering federate as indicated by the Object Instance Name Reserved † service and shall be
     * coadunated with the object instance. If the optional object instance name argument is not supplied, the RTI
     * shall create a federation execution-wide unique name, and that name shall be coadunated with the object
     * instance
     * @param theClass : Object class designator.
     * @param theObjectName : Optional object instance name.
     * @return Object instance handle.
     */
    @Override
    public ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass, String theObjectName)
            throws  ObjectInstanceNameInUse,
            ObjectInstanceNameNotReserved,
            ObjectClassNotPublished,
            ObjectClassNotDefined,
            SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        RegisterObjectInstance1516E request = new RegisterObjectInstance1516E();
        request.setObjectClass(theClass.hashCode());
        request.setObjectName(theObjectName);

        try {
            RegisterObjectInstance1516E response = (RegisterObjectInstance1516E) processRequest(request);
            return new CertiObjectHandle( (Integer)response.getObject() );
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Update Attribute Values service shall provide instance attribute values to the federation. The joined
     * federate shall be able to update only owned instance attributes. The joined federate should supply instance
     * attribute values using the federation-agreed-upon update policies. This service, coupled with the Reflect
     * Attribute Values † service, forms the primary data exchange mechanism supported by the RTI.
     * If preferred order type (see 8.1.1) of at least one instance attribute is TSO, the joined federate invoking this
     * service is time-regulating, and the timestamp argument is present, this service shall return a
     * federation-unique message retraction designator. Otherwise, no message retraction designator shall be
     * returned. A timestamp value should be provided if the preferred order type of at least one instance attribute
     * is TSO and the joined federate invoking this service is time-regulating.
     * The user-supplied tag argument shall be provided with all corresponding Reflect Attribute Value † service
     * invocations, even multiple ones at the same reflecting joined federate.
     * @param theObject Object instance designator.
     * @param theAttributes : Constrained set of attribute designator and value pairs.
     * @param userSuppliedTag : User-supplied tag.
     */
    @Override
    public void updateAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes, byte[] userSuppliedTag)
            throws AttributeNotOwned,
            AttributeNotDefined,
            ObjectInstanceNotKnown,
            SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling updateAttributeValues with Tag NULL");
        }

        UpdateAttributeValues1516E request = new UpdateAttributeValues1516E();
        request.setObject(theObject.hashCode());
        CertiHandleValuePairCollection coll = new CertiHandleValuePairCollection();
        for( AttributeHandle handle : theAttributes.keySet() )
        {
            coll.add(handle.hashCode(), theAttributes.get(handle));
        }
        request.setSuppliedAttributes(coll);
        request.setTag(userSuppliedTag);

        try {
            processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Update Attribute Values service shall provide instance attribute values to the federation. The joined
     * federate shall be able to update only owned instance attributes. The joined federate should supply instance
     * attribute values using the federation-agreed-upon update policies. This service, coupled with the Reflect
     * Attribute Values † service, forms the primary data exchange mechanism supported by the RTI.
     * If preferred order type (see 8.1.1) of at least one instance attribute is TSO, the joined federate invoking this
     * service is time-regulating, and the timestamp argument is present, this service shall return a
     * federation-unique message retraction designator. Otherwise, no message retraction designator shall be
     * returned. A timestamp value should be provided if the preferred order type of at least one instance attribute
     * is TSO and the joined federate invoking this service is time-regulating.
     * The user-supplied tag argument shall be provided with all corresponding Reflect Attribute Value † service
     * invocations, even multiple ones at the same reflecting joined federate.
     * @param theObject Object instance designator.
     * @param theAttributes : Constrained set of attribute designator and value pairs.
     * @param userSuppliedTag : User-supplied tag.
     * @param theTime : Optional timestamp.
     */
    @Override
    public MessageRetractionReturn updateAttributeValues(ObjectInstanceHandle theObject,
                                                         AttributeHandleValueMap theAttributes, byte[] userSuppliedTag, LogicalTime theTime)
            throws InvalidLogicalTime, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        if (userSuppliedTag == null) {
            throw new RTIinternalError("Calling updateAttributeValues with Tag NULL");
        }
        if(!(theTime instanceof CertiLogicalTime1516E ))
            throw new InvalidLogicalTime("LogicalTime value is not a CertiLogicalTime1516E");

        UpdateAttributeValues1516E request = new UpdateAttributeValues1516E();

        request.setObject(theObject.hashCode());
        CertiHandleValuePairCollection coll = new CertiHandleValuePairCollection();
        for( AttributeHandle handle : theAttributes.keySet() )
        {
            coll.add(handle.hashCode(), theAttributes.get(handle));
        }
        request.setSuppliedAttributes(coll);
        request.setTag(userSuppliedTag);
        request.setFederationTime1516E((CertiLogicalTime1516E)theTime);

        try {
            UpdateAttributeValues1516E response = (UpdateAttributeValues1516E) processRequest(request);

            CertiEventRetractionHandle eventRetractionHandle = response.getEventRetractionHandle();
            boolean rhiv = true;
            MessageRetractionHandle mrh = (MessageRetractionHandle) new CertiObjectHandle(eventRetractionHandle.getSendingFederate());
            return new MessageRetractionReturn(rhiv, mrh);


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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Send Interaction service shall send an interaction into the federation. Only parameters that are available
     * at the specified interaction class may be sent in a given interaction, as defined in the FDD. A federate is not
     * required to send all available parameters of the interaction class with the interaction.
     * @param theInteraction : Interaction class designator
     * @param theParameters : Constrained set of interaction parameter designator and value pairs.
     * @param userSuppliedTag : User-supplied tag.
     */
    @Override
    public void sendInteraction(InteractionClassHandle theInteraction, ParameterHandleValueMap theParameters,
                                byte[] userSuppliedTag)
            throws InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(userSuppliedTag == null || userSuppliedTag.length == 0)
            throw new RTIinternalError("Calling sendIntercation with tag null");

        SendInteraction1516E request = new SendInteraction1516E();
        request.setInteractionClass(theInteraction.hashCode());
        request.setTag(userSuppliedTag);
        request.setRegion(0);
        request.setSuppliedParameters(theParameters);
        try {
            processRequest(request);
        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
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

    /**
     * The Send Interaction service shall send an interaction into the federation. Only parameters that are available
     * at the specified interaction class may be sent in a given interaction, as defined in the FDD. A federate is not
     * required to send all available parameters of the interaction class with the interaction.
     * @param theInteraction : Interaction class designator
     * @param theParameters : Constrained set of interaction parameter designator and value pairs.
     * @param userSuppliedTag : User-supplied tag.
     * @param theTime Optional timestamp.
     * @return Optional message retraction designator.
     */
    @Override
    public MessageRetractionReturn sendInteraction(InteractionClassHandle theInteraction,
                                                   ParameterHandleValueMap theParameters, byte[] userSuppliedTag, LogicalTime theTime)
            throws InvalidLogicalTime, InteractionClassNotPublished, InteractionParameterNotDefined,
            InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected,
            RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(!(theTime instanceof CertiLogicalTime1516E))
            throw new InvalidLogicalTime("LogicalTime value is not a CertiLogicalTime1516E");

        SendInteraction1516E request = new SendInteraction1516E();

        request.setInteractionClass(theInteraction.hashCode());
        request.setTag(userSuppliedTag);
        request.setRegion(0);
        request.setSuppliedParameters(theParameters);
        request.setFederationTime1516E((CertiLogicalTime1516E) theTime);

        try {
            SendInteraction1516E response = (SendInteraction1516E) processRequest(request);

            CertiEventRetractionHandle eventRetractionHandle = (CertiEventRetractionHandle) response.getEventRetraction();
            boolean rhiv = true;
            MessageRetractionHandle mrh = (MessageRetractionHandle) new CertiObjectHandle(eventRetractionHandle.getSendingFederate());
            return new MessageRetractionReturn(rhiv, mrh);

        } catch (InteractionClassNotPublished ex) {
            throw ex;
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (InteractionClassNotDefined ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
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

    /**
     * The Delete Object Instance service shall inform the federation that an object instance with the specified
     * designator, which has the HLAprivilegeToDeleteObject instance attribute that is owned by the joined
     * federate, is to be removed from the federation execution. Once the object instance is removed from the
     * federation execution, the designator shall not be reused and all joined federates that owned attributes of the
     * object instance shall no longer own those attributes. The object instance name may be reused for
     * subsequently registered objects. The RTI shall use the Remove Object Instance † service to inform all other
     * joined federates which know the object instance that the object instance has been deleted. The invoking
     * joined federate shall own the HLAprivilegeToDeleteObject attribute of the specified object instance.
     * The preferred order type (see 8.1.1) of the sent message representing a Delete Object Instance service
     * invocation shall be based on the preferred order type of the HLAprivilegeToDeleteObject attribute of the
     * specified object instance. If preferred order type of the HLAprivilegeToDeleteObject attribute of the
     * specified object instance is TSO, the joined federate invoking this service is time-regulating, and the
     * timestamp argument is present, this service shall return a federation-unique message retraction designator.
     * Otherwise, no message retraction designator shall be returned. A timestamp value should be provided if the
     * preferred order type of the HLAprivilegeToDeleteObject attribute of the specified object instance is TSO
     * and the joined federate invoking this service is time-regulating.
     * The user-supplied tag argument shall be provided with all corresponding Remove Object Instance † service
     * invocations.
     * @param objectHandle : Object instance designator.
     * @param userSuppliedTag : User-supplied tag.
     */
    @Override
    public void deleteObjectInstance(ObjectInstanceHandle objectHandle, byte[] userSuppliedTag)
            throws DeletePrivilegeNotHeld, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        DeleteObjectInstance1516E request = new DeleteObjectInstance1516E();
        request.setObject(objectHandle.hashCode());
        request.setTag(userSuppliedTag);

        try {
            processRequest(request);
        } catch (DeletePrivilegeNotHeld ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
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

    /**
     * The Delete Object Instance service shall inform the federation that an object instance with the specified
     * designator, which has the HLAprivilegeToDeleteObject instance attribute that is owned by the joined
     * federate, is to be removed from the federation execution. Once the object instance is removed from the
     * federation execution, the designator shall not be reused and all joined federates that owned attributes of the
     * object instance shall no longer own those attributes. The object instance name may be reused for
     * subsequently registered objects. The RTI shall use the Remove Object Instance † service to inform all other
     * joined federates which know the object instance that the object instance has been deleted. The invoking
     * joined federate shall own the HLAprivilegeToDeleteObject attribute of the specified object instance.
     * The preferred order type (see 8.1.1) of the sent message representing a Delete Object Instance service
     * invocation shall be based on the preferred order type of the HLAprivilegeToDeleteObject attribute of the
     * specified object instance. If preferred order type of the HLAprivilegeToDeleteObject attribute of the
     * specified object instance is TSO, the joined federate invoking this service is time-regulating, and the
     * timestamp argument is present, this service shall return a federation-unique message retraction designator.
     * Otherwise, no message retraction designator shall be returned. A timestamp value should be provided if the
     * preferred order type of the HLAprivilegeToDeleteObject attribute of the specified object instance is TSO
     * and the joined federate invoking this service is time-regulating.
     * The user-supplied tag argument shall be provided with all corresponding Remove Object Instance † service
     * invocations.
     * @param objectHandle : Object instance designator.
     * @param userSuppliedTag : User-supplied tag.
     * @param theTime  : Optional timestamp.
     * @return Optional message retraction designator.
     */
    @Override
    public MessageRetractionReturn deleteObjectInstance(ObjectInstanceHandle objectHandle, byte[] userSuppliedTag,
                                                        LogicalTime theTime) throws InvalidLogicalTime, DeletePrivilegeNotHeld, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     * The Local Delete Object Instance service shall inform the RTI that it shall treat the specified object instance
     * as if the RTI had never notified the invoking joined federate to discover the object instance. The object
     * instance shall not be removed from the federation execution. The joined federate shall not own any attributes
     * of the specified object instance when invoking this service.
     * After the termination of invocation of this service, the specified object instance may subsequently be
     * discovered by the invoking joined federate, at a possibly different class from previously known.
     * @param objectHandle : Object instance designator.
     */
    @Override
    public void localDeleteObjectInstance(ObjectInstanceHandle objectHandle)
            throws OwnershipAcquisitionPending, FederateOwnsAttributes, ObjectInstanceNotKnown, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        LocalDeleteObjectInstance1516E request = new LocalDeleteObjectInstance1516E();
        request.setObject(objectHandle.hashCode());
        try {
            processRequest(request);
        } catch (OwnershipAcquisitionPending ex) {
            throw ex;
        } catch (FederateOwnsAttributes ex) {
            throw ex;
        } catch (SaveInProgress ex) {
            throw ex;
        } catch (RestoreInProgress ex) {
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

    /**
     * The Request Attribute Value Update service shall be used to stimulate the update of values of specified
     * attributes. When this service is used, the RTI shall solicit the current values of the specified attributes from
     * their owners using the Provide Attribute Value Update † service. The RTI shall not invoke the Provide
     * Attribute Value Update † service for unowned instance attributes. When an object class is specified, the RTI
     * shall solicit the values of the specified instance attributes for all object instances registered at that class and
     * all subclasses of that class. When an object instance designator is specified, the RTI shall solicit the values of
     * the specified instance attributes for the particular object instance. The timestamp of any resulting Reflect
     * Attribute Values † service invocations is determined by the updating joined federate.
     * If the federate invoking this service also owns some of the instance attributes whose values are being
     * requested, it shall not have the Provide Attribute Value Update † service invoked on it by the RTI. The
     * request is taken as an implicit invocation of the provide service at the requesting federate for all qualified
     * instance attributes that it owns.
     * The user-supplied tag argument shall be provided with all corresponding Provide Attribute Value Update †
     * service invocations.
     * @param theObject : Object instance designator.
     * @param theAttributes : Set of attribute designators.
     * @param userSuppliedTag : User-supplied tag.
     */
    @Override
    public void requestAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
            throws AttributeNotDefined,
            ObjectInstanceNotKnown,
            SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError
    {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        RequestObjectAttributeValueUpdate1516E request = new RequestObjectAttributeValueUpdate1516E();

        request.setObject(theObject.hashCode());
        request.setAttributes(theAttributes);

        try {
            processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Request Attribute Value Update service shall be used to stimulate the update of values of specified
     * attributes. When this service is used, the RTI shall solicit the current values of the specified attributes from
     * their owners using the Provide Attribute Value Update † service. The RTI shall not invoke the Provide
     * Attribute Value Update † service for unowned instance attributes. When an object class is specified, the RTI
     * shall solicit the values of the specified instance attributes for all object instances registered at that class and
     * all subclasses of that class. When an object instance designator is specified, the RTI shall solicit the values of
     * the specified instance attributes for the particular object instance. The timestamp of any resulting Reflect
     * Attribute Values † service invocations is determined by the updating joined federate.
     * If the federate invoking this service also owns some of the instance attributes whose values are being
     * requested, it shall not have the Provide Attribute Value Update † service invoked on it by the RTI. The
     * request is taken as an implicit invocation of the provide service at the requesting federate for all qualified
     * instance attributes that it owns.
     * The user-supplied tag argument shall be provided with all corresponding Provide Attribute Value Update †
     * service invocations.
     * @param theClass : object class designator.
     * @param theAttributes : Set of attribute designators.
     * @param userSuppliedTag : User-supplied tag.
     */
    @Override
    public void requestAttributeValueUpdate(ObjectClassHandle theClass, AttributeHandleSet theAttributes,byte[] userSuppliedTag)
            throws AttributeNotDefined,
            ObjectClassNotDefined,
            SaveInProgress,
            RestoreInProgress,
            FederateNotExecutionMember,
            NotConnected,
            RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theAttributes == null || theAttributes.size() == 0) {
            throw new RTIinternalError("Incorrect or empty attributes parameter");
        }
        RequestObjectAttributeValueUpdate1516E request = new RequestObjectAttributeValueUpdate1516E();

        request.setObject(theClass.hashCode());
        request.setAttributes(theAttributes);

        try {
            processRequest(request);
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
        } catch (ObjectClassNotDefined ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }

    }

    @Override
    public void requestAttributeTransportationTypeChange(ObjectInstanceHandle theObject,
                                                         AttributeHandleSet theAttributes, TransportationTypeHandle theType) throws AttributeAlreadyBeingChanged,
            AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, InvalidTransportationType, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryAttributeTransportationType(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
            throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestInteractionTransportationTypeChange(InteractionClassHandle theClass,
                                                           TransportationTypeHandle theType) throws InteractionClassAlreadyBeingChanged, InteractionClassNotPublished,
            InteractionClassNotDefined, InvalidTransportationType, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryInteractionTransportationType(FederateHandle theFederate, InteractionClassHandle theInteraction)
            throws InteractionClassNotDefined, SaveInProgress, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unconditionalAttributeOwnershipDivestiture(ObjectInstanceHandle theObject,
                                                           AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void negotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle theObject,
                                                        AttributeHandleSet theAttributes, byte[] userSuppliedTag)
            throws AttributeAlreadyBeingDivested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void confirmDivestiture(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
                                   byte[] userSuppliedTag) throws NoAcquisitionPending, AttributeDivestitureWasNotRequested, AttributeNotOwned,
            AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void attributeOwnershipAcquisition(ObjectInstanceHandle theObject, AttributeHandleSet desiredAttributes,
                                              byte[] userSuppliedTag) throws AttributeNotPublished, ObjectClassNotPublished, FederateOwnsAttributes,
            AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void attributeOwnershipAcquisitionIfAvailable(ObjectInstanceHandle theObject,
                                                         AttributeHandleSet desiredAttributes) throws AttributeAlreadyBeingAcquired, AttributeNotPublished,
            ObjectClassNotPublished, FederateOwnsAttributes, AttributeNotDefined, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void attributeOwnershipReleaseDenied(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
            throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttributeHandleSet attributeOwnershipDivestitureIfWanted(ObjectInstanceHandle theObject,
                                                                    AttributeHandleSet theAttributes) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelNegotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle theObject,
                                                              AttributeHandleSet theAttributes)
            throws AttributeDivestitureWasNotRequested, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void cancelAttributeOwnershipAcquisition(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
            throws AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned, AttributeNotDefined,
            ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected,
            RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void queryAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
            throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAttributeOwnedByFederate(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
            throws AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     *The Enable Time Regulation service shall enable time-regulation for the joined federate invoking the service
     * and thereby enable the joined federate to send TSO messages. The joined federate shall request that its
     * lookahead be set to the specified value. The RTI shall indicate the logical time assigned to the joined
     * federate through the Time Regulation Enabled † service. The logical time provided when time-regulation is
     * enabled shall be the smallest possible logical time that is greater than or equal to the joined federate’s current
     * logical time and for which all other constraints necessary to ensure TSO message delivery are satisfied. In
     * other words, in general, the logical time that the joined federate will be given (plus the requested lookahead)
     * will always be greater than or equal to the maximum logical time of all time-constrained joined federates
     * (with the possibility of being of equal value depending on what form of time advancement was used by each
     * time-constrained joined federate).
     * Upon the RTI’s invocation of the corresponding Time Regulation Enabled † service, the invoking joined
     * federate may begin sending TSO messages that have a timestamp greater than or equal to the joined
     * federate’s logical time plus the joined federate’s lookahead. Zero lookahead joined federates are not subject
     * to additional restrictions when time-regulation is first enabled.
     * Because the invocation of this service may require the RTI to advance the invoking joined federate’s logical
     * time, this service has an additional meaning for time-constrained joined federates. Since the advancing
     * logical time for a time-constrained joined federate is synonymous with a guarantee that all TSO messages
     * with timestamps less than the new logical time have been delivered, the invocation of this service shall be
     * considered an implicit Time Advance Request Available service invocation. The subsequent invocation of
     * Time Regulation Enabled † service shall be considered an implicit Time Advance Grant † service
     * invocation. Thus, if a time-constrained joined federate attempts to become time-regulating, it may receive
     * RO and TSO messages between its invocation of the Enable Time Regulation service and the RTI’s invocation of
     * the Time Regulation Enabled † service at the joined federate. This special case is not
     * illustrated in the state chart in Figure 17.
     * @param theLookahead : Lookahead
     */
    @Override
	public void enableTimeRegulation(LogicalTimeInterval theLookahead) throws InvalidLookahead, InTimeAdvancingState,
			RequestForTimeRegulationPending, TimeRegulationAlreadyEnabled, SaveInProgress, RestoreInProgress,
			FederateNotExecutionMember, NotConnected, RTIinternalError {
		if (rtiaProcess == null) {
			throw new NotConnected("RTIA not connected");
		}
		EnableTimeRegulation1516E request = new EnableTimeRegulation1516E();
		request.setLookAhead((CertiLogicalTimeInterval1516E)theLookahead);
		try {
			processRequest(request);
		} catch (InvalidLookahead ex) {
			throw ex;
		} catch (TimeRegulationAlreadyEnabled ex) {
			throw ex;
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

    /**
     * Invocation of the Disable Time Regulation service shall indicate that the joined federate is disabling
     * time regulation. Subsequent messages sent by the joined federate shall be sent automatically as RO
     * messages.
     */
    @Override
    public void disableTimeRegulation() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        DisableTimeRegulation1516E request = new DisableTimeRegulation1516E();
        try{
            processRequest(request);
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

    /**
     * The Enable Time Constrained service shall request that the joined federate invoking the service become
     * time-constrained. The RTI shall indicate that the joined federate is time-constrained by invoking the Time
     * Constrained Enabled † service.
     */
    @Override
    public void enableTimeConstrained()
            throws InTimeAdvancingState, RequestForTimeConstrainedPending, TimeConstrainedAlreadyEnabled,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        EnableTimeConstrained1516E request = new EnableTimeConstrained1516E();
        try{
            processRequest(request);
        } catch (TimeConstrainedAlreadyEnabled ex) {
            throw ex;
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

    /**
     * Invocation of the Disable Time Constrained service shall indicate that the joined federate is no longer
     * time-constrained. All enqueued and subsequent TSO messages shall be delivered to the joined federate as
     * RO messages.
     */
    @Override
    public void disableTimeConstrained() throws TimeConstrainedIsNotEnabled, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        DisableTimeConstrained1516E request = new DisableTimeConstrained1516E();
        try{
            processRequest(request);
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

    /**
     * The Time Advance Request service shall request an advance of the joined federate’s logical time and release
     * zero or more messages for delivery to the joined federate.
     * Invocation of this service shall cause the following set of messages to be delivered to the joined federate:
     * — All messages queued in the RTI that the joined federate will receive as RO messages
     * — All messages that the joined federate will receive as TSO messages that have timestamps less than
     * or equal to the specified logical time
     * After invoking the Time Advance Request service, the messages shall be passed to the joined federate when
     * the RTI invokes the Receive Interaction †, Reflect Attribute Values †, and Remove Object Instance †
     * services.
     * By invoking the Time Advance Request service with the specified logical time, the joined federate is
     * guaranteeing that it will not generate a TSO message at any point in the future with a timestamp less than or
     * equal to the specified logical time, even if the joined federate’s lookahead is zero. Further, the joined
     * federate may not generate any TSO messages in the future with timestamps less than the specified logical
     * time plus the actual lookahead the joined federate would have if it were granted to the specified logical time.
     * A Time Advance Grant † service invocation shall complete this request and indicate to the joined federate
     * that it has advanced to the specified logical time and that no additional TSO messages will be delivered to
     * the joined federate in the future with timestamps less than or equal to the logical time of the grant. For timeconstrained
     * joined federates, requests for which the specified logical time is less than GALT can be granted
     * without waiting for other joined federates to advance.
     * @param theTime : logical time.
     */
    @Override
    public void timeAdvanceRequest(LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime,
            InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(!(theTime instanceof LogicalTime))
            throw new InvalidLogicalTime("LogicalTime is not an instance of LogicalTime");

        TimeAdvanceRequest1516E request = new TimeAdvanceRequest1516E();
        CertiLogicalTime1516E certiFedTime = (CertiLogicalTime1516E)theTime;
        request.setFederationTime1516E(certiFedTime);

        try{
            processRequest(request);
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

    /**
     * The Time Advance Request Available service shall request an advance of the joined federate’s logical time. It
     * is similar to the Time Advance Request service to logical time T, except
     * — The RTI shall not guarantee delivery of all messages with timestamps equal to T when a Time
     * Advance Grant † service invocation to logical time T is issued
     * — After the joined federate receives a Time Advance Grant † service invocation to logical time T, it can
     * send additional messages with timestamps equal to T if the joined federate’s actual lookahead is zero
     * Invocation of this service shall cause the following set of messages to be delivered to the joined federate:
     * — All messages queued in the RTI that the joined federate will receive as RO messages
     * — All messages that the joined federate will receive as TSO messages that have timestamps less than
     * the specified logical time
     * — Any messages queued in the RTI that the joined federate will receive as TSO messages that have
     * timestamps equal to the specified logical time
     * After invoking the Time Advance Request Available service, the messages shall be passed to the joined
     * federate when the RTI invokes the Receive Interaction †, Reflect Attribute Values †, and Remove Object
     * Instance † services.
     * By invoking the Time Advance Request Available service with the specified logical time, the joined federate
     * is guaranteeing that it will not generate a TSO message at any point in the future with a timestamp less than
     * the specified logical time plus the actual lookahead the joined federate would have if it were granted to the
     * specified logical time.
     * A Time Advance Grant † service invocation shall complete this request and indicate to the joined federate
     * that it has advanced to the specified logical time and no additional TSO messages shall be delivered to the
     * joined federate in the future with timestamps less than the logical time of the grant. Additional messages
     * with timestamps equal to the logical time of the grant can arrive in the future. For time-constrained joined
     * federates, requests for which the specified logical
     * @param theTime : logical time
     */
    @Override
    public void timeAdvanceRequestAvailable(LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime,
            InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(!(theTime instanceof LogicalTime))
            throw new InvalidLogicalTime("LogicalTime is not an instance of LogicalTime");

        TimeAdvanceRequestAvailable1516E request = new TimeAdvanceRequestAvailable1516E();
        CertiLogicalTime1516E certiFedTime = (CertiLogicalTime1516E)theTime;
        request.setFederationTime1516E(certiFedTime);

        try{
            processRequest(request);
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

    /**
     * The Next Message Request service shall request the logical time of the joined federate to be advanced to the
     * timestamp of the next TSO message that will be delivered to the joined federate, provided that message has
     * a timestamp no greater than the logical time specified in the request.
     * Invocation of this service shall cause the following set of messages to be delivered to the joined federate:
     * — All messages queued in the RTI that the joined federate will receive as RO messages
     * — The smallest timestamped message that will ever be received by the joined federate as a TSO
     * message with a timestamp less than or equal to the specified logical time, and all other messages
     * containing the same timestamp that the joined federate will receive as TSO messages
     * After invocation of the Next Message Request service, the messages shall be passed to the joined federate
     * when the RTI invokes the Receive Interaction †, Reflect Attribute Values †, and Remove Object Instance †
     * services.
     * By invoking Next Message Request with the specified logical time, the joined federate is guaranteeing that it
     * will not generate a TSO message before the next Time Advance Grant † service invocation with a timestamp
     * less than or equal to the specified logical time (or less than the specified logical time plus the actual
     * lookahead the joined federate would have if it were granted to the specified logical time if its lookahead is
     * not zero).
     * If it does not receive any TSO messages before the Time Advance Grant † service invocation, the joined
     * federate shall guarantee that it will not generate a TSO message at any point in the future with a timestamp
     * less than or equal to the specified logical time (or less than the specified logical time plus its actual
     * lookahead if its lookahead is not zero).
     * If it does receive any TSO messages before the Time Advance Grant † service invocation, the joined federate
     * shall guarantee that it will not generate a TSO message at any point in the future with a timestamp less than
     * or equal to the logical time of the grant (or less than the logical time of the grant plus its actual lookahead if
     * its lookahead is not zero).
     * A Time Advance Grant † service invocation shall complete this request and indicate to the joined federate
     * that it has advanced its logical time to the timestamp of the TSO messages that are delivered, if any, or to the
     * specified logical time if no TSO messages were delivered. It shall also indicate that no TSO messages will
     * be delivered to the joined federate in the future with timestamps less than or equal to the logical time of the
     * grant. For time-constrained joined federates, requests for which either LITS or the specified logical time is
     * less than GALT can be granted without waiting for other joined federates to advance.
     * @param theTime : logical time.
     */
    @Override
    public void nextMessageRequest(LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime,
            InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(!(theTime instanceof LogicalTime))
            throw new InvalidLogicalTime("LogicalTime is not an instance of LogicalTime");

        NextEventRequest1516E request = new NextEventRequest1516E();
        CertiLogicalTime1516E certiFedTime = (CertiLogicalTime1516E)theTime;
        request.setFederationTime1516E(certiFedTime);

        try{
            processRequest(request);
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

    /**
     * The Next Message Request Available service shall request the logical time of the joined federate to be
     * advanced to the timestamp of the next TSO message that will be delivered to the joined federate, provided
     * that message has a timestamp no greater than the logical time specified in the request. It is similar to the Next
     * Message Request service except
     * — The RTI shall not guarantee delivery of all messages with timestamps equal to T when a Time
     * Advance Grant † service invocation to logical time T is issued, and
     * — After the joined federate receives a Time Advance Grant † service invocation to logical time T, it can
     * send additional messages with timestamps equal to T if the joined federate’s lookahead is zero.
     * @param theTime : logical time
     */
    @Override
    public void nextMessageRequestAvailable(LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime,
            InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if(!(theTime instanceof LogicalTime))
            throw new InvalidLogicalTime("LogicalTime is not an instance of LogicalTime");

        NextEventRequestAvailable1516E request = new NextEventRequestAvailable1516E();
        CertiLogicalTime1516E certiFedTime = (CertiLogicalTime1516E)theTime;
        request.setFederationTime1516E(certiFedTime);

        try{
            processRequest(request);
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

    @Override
    public void flushQueueRequest(LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime,
            InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    /**
     * Invocations of the Enable Asynchronous Delivery service shall instruct the RTI to deliver received RO
     * messages to the invoking joined federate when it is in either the Time Advancing state or Time Granted
     * state.
     */
    @Override
    public void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        EnableAsynchronousDelivery1516E request = new EnableAsynchronousDelivery1516E();
        try{
            processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * Invocations of the Disable Asynchronous Delivery service shall, for a time-constrained federate, instruct the
     * RTI to deliver RO messages to the invoking federate only when it is in the Time Advancing state.
     */
    @Override
    public void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        DisableAsynchronousDelivery1516E request = new DisableAsynchronousDelivery1516E();
        try{
            processRequest(request);
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
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public TimeQueryReturn queryGALT()
            throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public LogicalTime queryLogicalTime()
            throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public TimeQueryReturn queryLITS()
            throws SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void modifyLookahead(LogicalTimeInterval theLookahead)
            throws InvalidLookahead, InTimeAdvancingState, TimeRegulationIsNotEnabled, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public LogicalTimeInterval queryLookahead() throws TimeRegulationIsNotEnabled, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void retract(MessageRetractionHandle theHandle)
            throws MessageCanNoLongerBeRetracted, InvalidMessageRetractionHandle, TimeRegulationIsNotEnabled,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void changeAttributeOrderType(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
                                         OrderType theType) throws AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void changeInteractionOrderType(InteractionClassHandle theClass, OrderType theType)
            throws InteractionClassNotPublished, InteractionClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public RegionHandle createRegion(DimensionHandleSet dimensions) throws InvalidDimensionHandle, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void commitRegionModifications(RegionHandleSet regions) throws RegionNotCreatedByThisFederate, InvalidRegion,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void deleteRegion(RegionHandle theRegion)
            throws RegionInUseForUpdateOrSubscription, RegionNotCreatedByThisFederate, InvalidRegion, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public ObjectInstanceHandle registerObjectInstanceWithRegions(ObjectClassHandle theClass,
                                                                  AttributeSetRegionSetPairList attributesAndRegions)
            throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotPublished,
            ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectInstanceHandle registerObjectInstanceWithRegions(ObjectClassHandle theClass,
                                                                  AttributeSetRegionSetPairList attributesAndRegions, String theObject) throws ObjectInstanceNameInUse,
            ObjectInstanceNameNotReserved, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion,
            AttributeNotPublished, ObjectClassNotPublished, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void associateRegionsForUpdates(ObjectInstanceHandle theObject,
                                           AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext,
            RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void unassociateRegionsForUpdates(ObjectInstanceHandle theObject,
                                             AttributeSetRegionSetPairList attributesAndRegions)
            throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectInstanceNotKnown,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void subscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
                                                          AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext,
            RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void subscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
                                                          AttributeSetRegionSetPairList attributesAndRegions, String updateRateDesignator)
            throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined,
            ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle theClass,
                                                                   AttributeSetRegionSetPairList attributesAndRegions) throws InvalidRegionContext,
            RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle theClass,
                                                                   AttributeSetRegionSetPairList attributesAndRegions, String updateRateDesignator)
            throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined,
            ObjectClassNotDefined, InvalidUpdateRateDesignator, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void unsubscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
                                                            AttributeSetRegionSetPairList attributesAndRegions)
            throws RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void subscribeInteractionClassWithRegions(InteractionClassHandle theClass, RegionHandleSet regions)
            throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext,
            RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void subscribeInteractionClassPassivelyWithRegions(InteractionClassHandle theClass, RegionHandleSet regions)
            throws FederateServiceInvocationsAreBeingReportedViaMOM, InvalidRegionContext,
            RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void unsubscribeInteractionClassWithRegions(InteractionClassHandle theClass, RegionHandleSet regions)
            throws RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void sendInteractionWithRegions(InteractionClassHandle theInteraction, ParameterHandleValueMap theParameters,
                                           RegionHandleSet regions, byte[] userSuppliedTag)
            throws InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion, InteractionClassNotPublished,
            InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public MessageRetractionReturn sendInteractionWithRegions(InteractionClassHandle theInteraction,
                                                              ParameterHandleValueMap theParameters, RegionHandleSet regions, byte[] userSuppliedTag, LogicalTime theTime)
            throws InvalidLogicalTime, InvalidRegionContext, RegionNotCreatedByThisFederate, InvalidRegion,
            InteractionClassNotPublished, InteractionParameterNotDefined, InteractionClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void requestAttributeValueUpdateWithRegions(ObjectClassHandle theClass,
                                                       AttributeSetRegionSetPairList attributesAndRegions, byte[] userSuppliedTag) throws InvalidRegionContext,
            RegionNotCreatedByThisFederate, InvalidRegion, AttributeNotDefined, ObjectClassNotDefined, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public ResignAction getAutomaticResignDirective()
            throws FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAutomaticResignDirective(ResignAction resignAction)
            throws InvalidResignAction, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public FederateHandle getFederateHandle(String theName)
            throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getFederateName(FederateHandle theHandle) throws InvalidFederateHandle, FederateHandleNotKnown,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    /**
     * The Get Object Class Handle service shall return the object class handle associated with the supplied object
     * class name.
     * @param theName : Object class name.
     * @return Object class handle.
     * @throws NameNotFound
     * @throws FederateNotExecutionMember
     * @throws NotConnected
     * @throws RTIinternalError
     */
    @Override
    public ObjectClassHandle getObjectClassHandle(String theName)
            throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty object class name");
        }

        GetObjectClassHandle1516E request = new GetObjectClassHandle1516E();
        request.setClassName(theName);

        try {
            GetObjectClassHandle1516E response = (GetObjectClassHandle1516E) processRequest(request);
            return new CertiObjectHandle(response.getObjectClass());
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

    /**
     * The Get Object Class Name service shall return the object class name associated with the supplied object
     * class handle.
     * @param theHandle : Object class handle.
     * @return Object class name.
     */
    @Override
    public String getObjectClassName(ObjectClassHandle theHandle)
            throws InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetObjectClassName1516E request = new GetObjectClassName1516E();
        request.setObjectClass(theHandle.hashCode());
        try{
            GetObjectClassName1516E response = (GetObjectClassName1516E) processRequest(request);
            return response.getClassName();
        } catch (InvalidObjectClassHandle ex) {
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

    @Override
    public ObjectClassHandle getKnownObjectClassHandle(ObjectInstanceHandle theObject)
            throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    /**
     * The Get Object Instance Handle service shall return the handle of the object instance with the supplied
     * name.
     * @param theName : Object instance name.
     * @return Object instance handle.
     */
    @Override
    public ObjectInstanceHandle getObjectInstanceHandle(String theName)
            throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetObjectInstanceHandle1516E request = new GetObjectInstanceHandle1516E();
        request.setObjectInstanceName(theName);

        try {
            GetObjectInstanceHandle1516E response = (GetObjectInstanceHandle1516E) processRequest(request);
            return new CertiObjectHandle(response.getObject());

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

    /**
     * The Get Object Instance Name service shall return the name of the object instance with the supplied handle.
     * @param theHandle Object instance handle.
     * @return Object instance name.
     */
    @Override
    public String getObjectInstanceName(ObjectInstanceHandle theHandle)
            throws ObjectInstanceNotKnown, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetObjectInstanceName1516E request = new GetObjectInstanceName1516E();
        request.setObject(theHandle.hashCode());

        try {
            GetObjectInstanceName1516E response = (GetObjectInstanceName1516E) processRequest(request);
            return response.getObjectInstanceName();
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

    /**
     * The Get Attribute Handle service shall return the class attribute handle associated with the supplied class
     * attribute name and object class.
     * @param whichClass : Object class handle.
     * @param theName : Class attribute name.
     * @return Class attribute handle.
     */
    @Override
    public AttributeHandle getAttributeHandle(ObjectClassHandle whichClass, String theName)
            throws NameNotFound, InvalidObjectClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        if (theName == null || theName.length() == 0) {
            throw new RTIinternalError("Incorrect or empty name");
        }

        GetAttributeHandle1516E request = new GetAttributeHandle1516E();
        request.setAttributeName(theName);
        request.setObjectClass(whichClass.hashCode());

        try {
            GetAttributeHandle1516E response = (GetAttributeHandle1516E) processRequest(request);
            return new CertiObjectHandle(response.getAttribute());
        } catch (InvalidObjectClassHandle ex) {
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

    /**
     * The Get Attribute Name service shall return the class attribute name associated with the supplied class
     * attribute handle and object class.
     * @param whichClass : Object class handle.
     * @param theHandle : Class attribute handle.
     * @return Class attribute name.
     */
    @Override
    public String getAttributeName(ObjectClassHandle whichClass, AttributeHandle theHandle)
            throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle, FederateNotExecutionMember,
            NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetAttributeName1516E request = new GetAttributeName1516E();
        request.setObjectClass(whichClass.hashCode());
        request.setAttribute(theHandle.hashCode());

        try {
            GetAttributeHandle1516E response = (GetAttributeHandle1516E) processRequest(request);
            return response.getAttributeName();
        } catch (InvalidObjectClassHandle ex) {
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

    @Override
    public double getUpdateRateValue(String updateRateDesignator)
            throws InvalidUpdateRateDesignator, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public double getUpdateRateValueForAttribute(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
            throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember, NotConnected,
            RTIinternalError {
        throw new UnsupportedOperationException();

    }

    /**
     * The Get Interaction Class Handle service shall return the interaction class handle associated with the
     * supplied interaction class name.
     * @param theName : Interaction class name.
     * @return Interaction class handle.
     */
    @Override
    public InteractionClassHandle getInteractionClassHandle(String theName)
            throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetInteractionClassHandle1516E request = new GetInteractionClassHandle1516E();
        request.setClassName(theName);

        try{
            GetInteractionClassHandle1516E response = (GetInteractionClassHandle1516E) processRequest(request);
            return new CertiObjectHandle(response.getInteractionClass());
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

    /**
     * The Get Interaction Class Name service shall return the interaction class name associated with the supplied
     * interaction class handle.
     * @param theHandle : Interaction class handle.
     * @return Interaction class name.
     */
    @Override
    public String getInteractionClassName(InteractionClassHandle theHandle)
            throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetInteractionClassName1516E request = new GetInteractionClassName1516E();
        request.setInteractionClass(theHandle.hashCode());

        try{
            GetInteractionClassName1516E response = (GetInteractionClassName1516E) processRequest(request);
            return response.getClassName();
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

    /**
     * The Get Parameter Handle service shall return the interaction parameter handle associated with the supplied
     * parameter name and interaction class.
     * @param whichClass : Interaction class handle.
     * @param theName : Parameter name.
     * @return Parameter handle.
     */
    @Override
    public ParameterHandle getParameterHandle(InteractionClassHandle whichClass, String theName) throws NameNotFound,
            InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetParameterHandle1516E request = new GetParameterHandle1516E();
        request.setInteractionClass(whichClass.hashCode());
        request.setParameterName(theName);

        try{
            GetParameterHandle1516E response = (GetParameterHandle1516E) processRequest(request);
            return new CertiObjectHandle(response.getParameter());
        } catch (NameNotFound ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        }catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    /**
     * The Get Parameter Name service shall return the interaction parameter name associated with the supplied
     * parameter handle and interaction class.
     * @param whichClass : Interaction class handle.
     * @param theHandle : Parameter handle.
     * @return Parameter name.
     */
    @Override
    public String getParameterName(InteractionClassHandle whichClass, ParameterHandle theHandle)
            throws InteractionParameterNotDefined, InvalidParameterHandle, InvalidInteractionClassHandle,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        if(rtiaProcess == null){
            throw new NotConnected("RTIA not connected");
        }
        GetParameterName1516E request = new GetParameterName1516E();
        request.setInteractionClass(whichClass.hashCode());
        request.setParameter((short)theHandle.hashCode());

        try{
            GetParameterHandle1516E response = (GetParameterHandle1516E) processRequest(request);
            return response.getParameterName();
        } catch (InteractionParameterNotDefined ex) {
            throw ex;
        } catch (FederateNotExecutionMember ex) {
            throw ex;
        }catch (RTIinternalError ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception caught", ex);
            throw new RTIinternalError("Unexpected exception caught.");
        }
    }

    @Override
    public OrderType getOrderType(String theName)
            throws InvalidOrderName, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public String getOrderName(OrderType theType)
            throws InvalidOrderType, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransportationTypeHandle getTransportationTypeHandle(String theName)
            throws InvalidTransportationName, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public String getTransportationTypeName(TransportationTypeHandle theHandle)
            throws InvalidTransportationType, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public DimensionHandleSet getAvailableDimensionsForClassAttribute(ObjectClassHandle whichClass,
                                                                      AttributeHandle theHandle) throws AttributeNotDefined, InvalidAttributeHandle, InvalidObjectClassHandle,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public DimensionHandleSet getAvailableDimensionsForInteractionClass(InteractionClassHandle theHandle)
            throws InvalidInteractionClassHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public DimensionHandle getDimensionHandle(String theName)
            throws NameNotFound, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDimensionName(DimensionHandle theHandle)
            throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getDimensionUpperBound(DimensionHandle theHandle)
            throws InvalidDimensionHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public DimensionHandleSet getDimensionHandleSet(RegionHandle region) throws InvalidRegion, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public RangeBounds getRangeBounds(RegionHandle region, DimensionHandle dimension)
            throws RegionDoesNotContainSpecifiedDimension, InvalidRegion, SaveInProgress, RestoreInProgress,
            FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setRangeBounds(RegionHandle region, DimensionHandle dimension, RangeBounds bounds)
            throws InvalidRangeBound, RegionDoesNotContainSpecifiedDimension, RegionNotCreatedByThisFederate,
            InvalidRegion, SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected,
            RTIinternalError {
        throw new UnsupportedOperationException();
    }

    @Override
    public long normalizeFederateHandle(FederateHandle federateHandle)
            throws InvalidFederateHandle, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public long normalizeServiceGroup(ServiceGroup group)
            throws InvalidServiceGroup, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void enableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOn,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();


    }

    @Override
    public void disableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOff,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void enableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOn, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void disableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOff, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void enableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOn, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void disableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOff, SaveInProgress,
            RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void enableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOn,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void disableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOff,
            SaveInProgress, RestoreInProgress, FederateNotExecutionMember, NotConnected, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    /**
     * The Evoke Callback service instructs the RTI that the invoking federate is prepared to receive a single
     * federate callback. If there are no pending callbacks to be delivered to the federate, this service invocation
     * shall wait for the duration indicated by the supplied argument. The supplied argument shall have a precision
     * of at least 1 ms. This service shall result in either the timeout expiring or a single callback to be invoked on
     * the federate. The pending callback indicator argument shall be a boolean value: TRUE shall indicate that
     * pending callbacks exist, and FALSE shall indicate that no pending callbacks exist. This service has no effect
     * in the IMMEDIATE callback model.
     * @param approximateMinimumTimeInSeconds : Minimum amount of wall-clock time the service will wait for a callback.
     * @return Pending callback indicator.
     * @throws CallNotAllowedFromWithinCallback
     * @throws RTIinternalError
     */
    @Override
    public boolean evokeCallback(double approximateMinimumTimeInSeconds)
            throws CallNotAllowedFromWithinCallback, RTIinternalError {
        return tickKernel(false, approximateMinimumTimeInSeconds, approximateMinimumTimeInSeconds);
    }

    /**
     * The Evoke Multiple Callbacks service instructs the RTI that the invoking federate is prepared to receive
     * multiple federate callbacks. The service shall continue to process available callbacks until the minimum
     * specified wall-clock time. At that wall-clock time, if there are no additional callbacks to be delivered to the
     * federate, the service shall complete. If, after the minimum specified wall-clock time, there continue to be
     * callbacks, the RTI shall continue to deliver those callbacks until the maximum specified wall-clock time is
     * exceeded. Both supplied wall-clock time arguments shall have a precision of at least 1 ms. The pending
     * callback indicator argument shall be a boolean value: TRUE shall indicate that pending callbacks exist, and
     * FALSE shall indicate that no pending callbacks exist. This service has no effect in the IMMEDIATE
     * callback model.
     * @param approximateMinimumTimeInSeconds : Minimum amount of wall-clock time.
     * @param approximateMaximumTimeInSeconds : Maximum amount of wall-clock time.
     * @return Pending callback indicator.
     */
    @Override
    public boolean evokeMultipleCallbacks(double approximateMinimumTimeInSeconds,
                                          double approximateMaximumTimeInSeconds) throws CallNotAllowedFromWithinCallback, RTIinternalError {
        try {
            return tickKernel(true, approximateMinimumTimeInSeconds, approximateMaximumTimeInSeconds);
        } catch (CallNotAllowedFromWithinCallback ex) {
            LOGGER.severe("CallNotAllowedFromWithinCallback");
            throw new RTIinternalError("CallNotAllowedFromWithinCallback");
        }
    }

    @Override
    public void enableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public void disableCallbacks() throws SaveInProgress, RestoreInProgress, RTIinternalError {
        throw new UnsupportedOperationException();

    }

    @Override
    public AttributeHandleFactory getAttributeHandleFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();
    }

    @Override
    public AttributeHandleSetFactory getAttributeHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        return new CertiAttributeHandleSetFactory();
    }

    @Override
    public AttributeHandleValueMapFactory getAttributeHandleValueMapFactory()
            throws FederateNotExecutionMember, NotConnected {
        return new CertiAttributeHandleValueMapFactory();
    }

    @Override
    public AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory()
            throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public DimensionHandleFactory getDimensionHandleFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public DimensionHandleSetFactory getDimensionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public FederateHandleFactory getFederateHandleFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public FederateHandleSetFactory getFederateHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public InteractionClassHandleFactory getInteractionClassHandleFactory()
            throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public ObjectClassHandleFactory getObjectClassHandleFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectInstanceHandleFactory getObjectInstanceHandleFactory()
            throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public ParameterHandleFactory getParameterHandleFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public ParameterHandleValueMapFactory getParameterHandleValueMapFactory()
            throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public RegionHandleSetFactory getRegionHandleSetFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public TransportationTypeHandleFactory getTransportationTypeHandleFactory()
            throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    @Override
    public String getHLAversion() {
        throw new UnsupportedOperationException();

    }

    @Override
    public LogicalTimeFactory getTimeFactory() throws FederateNotExecutionMember, NotConnected {
        throw new UnsupportedOperationException();

    }

    /**
     * Process a request.
     * Send the parameter Message to the RTIA, then read the response and transfrome it to a new Message
     * @param request : message to send
     * @return CertiMessage containing the response received from the RTIA
     * @throws CallNotAllowedFromWithinCallback
     */
    public CertiMessage processRequest(CertiMessage request)
            throws AsynchronousDeliveryAlreadyEnabled,
            AsynchronousDeliveryAlreadyDisabled,
            AttributeAlreadyOwned,
            AttributeAlreadyBeingAcquired,
            AttributeAlreadyBeingDivested,
            AttributeDivestitureWasNotRequested,
            AttributeAcquisitionWasNotRequested,
            AttributeNotDefined,
            AttributeNotOwned,
            AttributeNotPublished,
            RTIinternalError,
            CouldNotDecode,
            DeletePrivilegeNotHeld,
            FederateAlreadyExecutionMember,
            FederateInternalError,
            FederateNotExecutionMember,
            FederateOwnsAttributes,
            FederatesCurrentlyJoined,
            FederationExecutionAlreadyExists,
            FederationExecutionDoesNotExist,
            InteractionClassNotDefined,
            InteractionClassNotDefined,
            InteractionClassNotPublished,
            InteractionParameterNotDefined,
            InvalidLookahead,
            InvalidObjectClassHandle,
            InvalidResignAction,
            NameNotFound,
            ObjectClassNotDefined,
            ObjectClassNotPublished,
            RestoreInProgress,
            RestoreNotRequested,
            SaveInProgress,
            SaveNotInitiated,
            UnableToPerformSave,
            OwnershipAcquisitionPending,
            TimeRegulationAlreadyEnabled,
            TimeConstrainedAlreadyEnabled,
            InvalidRegionContext,
            ErrorReadingFDD,
            CouldNotOpenFDD,
            CallNotAllowedFromWithinCallback, FederateNameAlreadyInUse, ObjectInstanceNotKnown, InvalidTransportationType, SpecifiedSaveLabelDoesNotExist, InvalidMessageRetractionHandle, InteractionClassNotSubscribed {

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
                response = MessageFactory1516E.readMessage(messageBuffer);

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

    private void translateException(CertiException ex)
            throws /*ArrayIndexOutOfBounds,*/
            AsynchronousDeliveryAlreadyEnabled,
            AsynchronousDeliveryAlreadyDisabled,
            AttributeAlreadyOwned,
            AttributeAlreadyBeingAcquired,
            AttributeAlreadyBeingDivested,
            AttributeDivestitureWasNotRequested,
            AttributeAcquisitionWasNotRequested,
            AttributeNotDefined,
            AttributeNotOwned,
            AttributeNotPublished,
            CallNotAllowedFromWithinCallback,
            RTIinternalError,
            DeletePrivilegeNotHeld,
            FederateAlreadyExecutionMember,
            FederateInternalError,
            FederateNotExecutionMember,
            FederateOwnsAttributes,
            FederatesCurrentlyJoined,
            FederationExecutionAlreadyExists,
            FederationExecutionDoesNotExist,
            InteractionClassNotDefined,
            InteractionClassNotPublished,
            InteractionParameterNotDefined,
            InvalidLookahead,
            InvalidObjectClassHandle,
            InvalidResignAction,
            NameNotFound,
            ObjectClassNotDefined,
            ObjectClassNotPublished,
            RestoreInProgress,
            RestoreNotRequested,
            SaveInProgress,
            SaveNotInitiated,
            UnableToPerformSave,
            OwnershipAcquisitionPending,
            TimeRegulationAlreadyEnabled,
            TimeConstrainedAlreadyEnabled,
            InvalidRegionContext,
            ErrorReadingFDD,
            CouldNotOpenFDD, FederateNameAlreadyInUse, InvalidMessageRetractionHandle, InvalidTransportationType, ObjectInstanceNotKnown  {
        LOGGER.warning("Throwing exception: " + ex.getExceptionType().toString());

        switch (ex.getExceptionType()) {
            case NO_EXCEPTION:
                break;

            case ARRAY_INDEX_OUT_OF_BOUNDS:
                //throw new ArrayIndexOutOfBounds(ex.getReason());

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

            case ATTRIBUTE_NOT_OWNED:
                throw new AttributeNotOwned(ex.getReason());

            case ATTRIBUTE_NOT_PUBLISHED:
                throw new AttributeNotPublished(ex.getReason());

            case ATTRIBUTE_NOT_SUBSCRIBED:
                throw new RTIinternalError(ex.getReason());

            case CONCURRENT_ACCESS_ATTEMPTED:
                throw new CallNotAllowedFromWithinCallback(ex.getReason());

            case COULD_NOT_OPEN_FED:
                throw new CouldNotOpenFDD(ex.getReason());

            case COULD_NOT_OPEN_RID:
                throw new RTIinternalError(ex.getReason());

            case DELETE_PRIVILEGE_NOT_HELD:
                throw new DeletePrivilegeNotHeld(ex.getReason());

            case ERROR_READING_RID:
                throw new RTIinternalError(ex.getReason());

            case ERROR_READING_FED:
                throw new ErrorReadingFDD(ex.getReason());

            case FEDERATE_ALREADY_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_ALREADY_EXECUTION_MEMBER:
                throw new FederateAlreadyExecutionMember(ex.getReason());

            case FEDERATE_DOES_NOT_EXIST:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_INTERNAL_ERROR:
                throw new FederateInternalError(ex.getReason());

            case FEDERATE_NAME_ALREADY_IN_USE:
                throw new FederateNameAlreadyInUse(ex.getReason());

            case FEDERATE_NOT_EXECUTION_MEMBER:
                throw new FederateNotExecutionMember(ex.getReason());

            case FEDERATE_NOT_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_OWNS_ATTRIBUTES:
                throw new FederateOwnsAttributes(ex.getReason());

            case FEDERATES_CURRENTLY_JOINED:
                throw new FederatesCurrentlyJoined(ex.getReason());

            case FEDERATION_ALREADY_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATION_EXECUTION_ALREADY_EXISTS:
                throw new FederationExecutionAlreadyExists(ex.getReason());

            case FEDERATION_EXECUTION_DOES_NOT_EXIST:
                throw new FederationExecutionDoesNotExist(ex.getReason());

            case FEDERATION_NOT_PAUSED:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_NOT_PUBLISHING:
                throw new RTIinternalError(ex.getReason());

            case FEDERATE_NOT_SUBSCRIBING:
                throw new RTIinternalError(ex.getReason());

            case ID_SUPPLY_EXHAUSTED:
                throw new RTIinternalError(ex.getReason());

            case INTERACTION_CLASS_NOT_DEFINED:
                throw new InteractionClassNotDefined(ex.getReason());

            case INTERACTION_CLASS_NOT_KNOWN:
                throw new InteractionClassNotDefined(ex.getReason());

            case INTERACTION_CLASS_NOT_PUBLISHED:
                throw new InteractionClassNotPublished(ex.getReason());

            case INTERACTION_PARAMETER_NOT_DEFINED:
                throw new InteractionParameterNotDefined(ex.getReason());

            case INVALID_DIVESTITURE_CONDITION:
                throw new RTIinternalError(ex.getReason());

            case INVALID_FEDERATION_TIME_DELTA:
                throw new RTIinternalError(ex.getReason());

            case INVALID_LOOKAHEAD:
                throw new InvalidLookahead(ex.getReason());

            case INVALID_OBJECT_HANDLE:
                throw new InvalidObjectClassHandle(ex.getReason());

            case INVALID_RESIGN_ACTION:
                throw new InvalidResignAction(ex.getReason());

            case INVALID_RETRACTION_HANDLE:
                throw new InvalidMessageRetractionHandle(ex.getReason());

            case INVALID_ROUTING_SPACE:
                throw new RTIinternalError(ex.getReason());

            case INVALID_TRANSPORTATION_HANDLE:
                throw new InvalidTransportationType(ex.getReason());

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
                throw new ObjectClassNotDefined(ex.getReason());

            case OBJECT_CLASS_NOT_PUBLISHED:
                throw new ObjectClassNotPublished(ex.getReason());

            case OBJECT_NOT_KNOWN:
                throw new ObjectInstanceNotKnown(ex.getReason());

            case RESTORE_IN_PROGRESS:
                throw new RestoreInProgress(ex.getReason());

            case RESTORE_NOT_REQUESTED:
                throw new RestoreNotRequested(ex.getReason());

            case RTI_INTERNAL_ERROR:
                throw new RTIinternalError(ex.getReason());

            case SAVE_IN_PROGRESS:
                throw new SaveInProgress(ex.getReason());

            case SAVE_NOT_INITIATED:
                throw new SaveNotInitiated(ex.getReason());

            case SECURITY_ERROR:
                throw new RTIinternalError(ex.getReason());

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

            case OWNERSHIP_ACQUISITION_PENDING:
                throw new OwnershipAcquisitionPending(ex.getReason());

            case TIME_REGULATION_ALREADY_ENABLED:
                throw new TimeRegulationAlreadyEnabled(ex.getReason());

            case TIME_CONSTRAINED_ALREADY_ENABLED:
                throw new TimeConstrainedAlreadyEnabled(ex.getReason());

            case INVALID_REGION_CONTEXT:
                throw new InvalidRegionContext(ex.getReason());

            default:
                LOGGER.severe("Throwing unknown exception !");
                throw new RuntimeException("Can not translate exception (it is probably not implemented yet)." + ex.getReason());
        }
    }

    /**
     *
     * @param multiple
     * @param minimum
     * @param maximum
     * @return
     */
    synchronized private boolean tickKernel(boolean multiple, double minimum, double maximum)
            throws CallNotAllowedFromWithinCallback/*,
            ConcurrentAccessAttempted*/,
            RTIinternalError
    {
        TickRequest1516E tickRequest;
        CertiMessage tickResponse = null;

        LOGGER.fine("Request callback(s) from the local RTIA");
        tickRequest = new TickRequest1516E();
        tickRequest.setMultiple(multiple);
        tickRequest.setMinTickTime(minimum);
        tickRequest.setMaxTickTime(maximum);

        synchronized(this.messageBuffer) {

            tickRequest.writeMessage(this.messageBuffer);

            try {
                LOGGER.info("Sending message (" + tickRequest.toString() + ")");
                this.messageBuffer.send();
            } catch (IOException ex) {
                LOGGER.severe("libRTI: exception: NetworkError (write)");
                throw new RTIinternalError("NetworkError in tick() while sending TICK_REQUEST: " + ex.getMessage());
            }

            LOGGER.fine("Reading response(s) from the local RTIA");
            while (true) {
                try {
                    tickResponse = MessageFactory1516E.readMessage(messageBuffer);
                    LOGGER.info("Received: " + tickResponse.toString() + "\n");
                } catch (IOException ex) {
                    throw new RTIinternalError("NetworkError in tick() while receiving response: " + ex.getMessage());
                } catch (CertiException certiException) {
                    // tick() may only throw exceptions defined in the HLA standard
                    // the RTIA is responsible for sending 'allowed' exceptions only
                    try {
                        translateException(certiException);
                    } catch (CallNotAllowedFromWithinCallback ex) {
                        throw ex;
                    /*} catch (ConcurrentAccessAttempted ex) {
                        throw ex;*/
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
                    return ((TickRequest1516E) tickResponse).isMultiple();
                } else {
                    try {
                        // Otherwise, the RTI calls a FederateAmbassador service.
                        callFederateAmbassador(tickResponse);
                    } catch (RTIinternalError ex) {
                        // RTIA awaits TICK_REQUEST_NEXT, terminate the tick() processing
                        TickRequestStop1516E tickRequestStop = new TickRequestStop1516E();
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
                    CertiMessage tickNext = new TickRequestNext1516E();
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
                            message.getLabel(), null);
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
                    federateAmbassador.requestFederationRestoreFailed(message.getLabel());
                    break;

                case FEDERATION_RESTORE_BEGUN:
                    federateAmbassador.federationRestoreBegun();
                    break;

                case INITIATE_FEDERATE_RESTORE:
                    federateAmbassador.initiateFederateRestore(
                            message.getLabel(),
                            "NITIATE_FEDERATE_RESTORE",
                            new CertiObjectHandle(((InitiateFederateRestore1516E) message).getFederate()));
                    break;

                case FEDERATION_RESTORED:
                    federateAmbassador.federationRestored();
                    break;

                case FEDERATION_NOT_RESTORED:
                    //federateAmbassador.federationNotRestored();
                    break;

                case START_REGISTRATION_FOR_OBJECT_CLASS:
                    federateAmbassador.startRegistrationForObjectClass(
                            (new CertiObjectHandle(((StartRegistrationForObjectClass1516E) message).getObjectClass())));
                    break;

                case STOP_REGISTRATION_FOR_OBJECT_CLASS:
                    int objectClass = ((StopRegistrationForObjectClass1516E)message).getObjectClass();
                    federateAmbassador.stopRegistrationForObjectClass(new CertiObjectHandle(objectClass));
                    break;

                case TURN_INTERACTIONS_ON:
                    federateAmbassador.turnInteractionsOn(
                            new CertiObjectHandle(((TurnInteractionsOn1516E) message).getInteractionClass()));
                    break;

                case TURN_INTERACTIONS_OFF:
                    federateAmbassador.turnInteractionsOff(
                            new CertiObjectHandle(((TurnInteractionsOff1516E) message).getInteractionClass()));
                    break;

                case DISCOVER_OBJECT_INSTANCE:
                    federateAmbassador.discoverObjectInstance(
                            new CertiObjectHandle(((DiscoverObjectInstance1516E) message).getObject()),
                            new CertiObjectHandle(((DiscoverObjectInstance1516E) message).getObjectClass()),
                            ((DiscoverObjectInstance1516E) message).getObjectName());
                    break;

                case REFLECT_ATTRIBUTE_VALUES:
                    LOGGER.info("Reflect: ");
                    ReflectedAttributes attr = ((ReflectAttributeValues1516E) message).getReflectedAttributes();
                    //LOGGER.info(attr.toString());
                    CertiAttributeHandleValueMap coll = new CertiAttributeHandleValueMap(attr.size());
                    for( int i = 0; i < attr.size(); i++)
                    {
                        coll.put( new CertiObjectHandle(attr.getAttributeHandle(i)), attr.getValue(i));
                    }

                    if (((ReflectAttributeValues1516E) message).getFederationTime1516E() != null) {
                        CertiObjectHandle theObject = new CertiObjectHandle(((ReflectAttributeValues1516E) message).getObject());
                        LogicalTime theTime = ((ReflectAttributeValues1516E)message).getFederationTime1516E();
                        federateAmbassador.reflectAttributeValues(theObject, coll, message.getTag(), null, null, theTime, null, null );

                    } else {
                        ObjectInstanceHandle theObject = new CertiObjectHandle(((ReflectAttributeValues1516E) message).getObject());
                        federateAmbassador.reflectAttributeValues(theObject, coll, message.getTag(),
                                null, null, null) ;
                    }
                    break;

                case RECEIVE_INTERACTION:
                    /*
                    InteractionClassHandle interactionClass,
                           ParameterHandleValueMap theParameters,
                           byte[] userSuppliedTag,
                           OrderType sentOrdering,
                           TransportationTypeHandle theTransport,
                           SupplementalReceiveInfo receiveInfo)
                     */
                    InteractionClassHandle interactionClass = new CertiObjectHandle(((ReceiveInteraction1516E) message).getInteractionClass());
                    ParameterHandleValueMap theParameters = ((ReceiveInteraction1516E) message).getParameterHandleValueMap();
                    federateAmbassador.receiveInteraction(
                            interactionClass, theParameters, message.getTag(), null, null, null);
                    break;

                case REMOVE_OBJECT_INSTANCE:
                    if (((CertiMessage1516E)message).getFederationTime1516E() != null) {

                        ObjectInstanceHandle theObject = new CertiObjectHandle(((RemoveObjectInstance1516E) message).getObject());
                        byte[] userSuppliedTag = message.getTag();
                        OrderType sentOrdering = null;
                        LogicalTime theTime = ((CertiMessage1516E) message).getFederationTime1516E();
                        OrderType receivedOrdering = null;
                        FederateAmbassador.SupplementalRemoveInfo removeInfo = null;

                        federateAmbassador.removeObjectInstance(theObject, userSuppliedTag,
                                sentOrdering, theTime, receivedOrdering, removeInfo);
                    } else {
                        ObjectInstanceHandle theObject = new CertiObjectHandle(((RemoveObjectInstance1516E) message).getObject());
                        byte[] userSuppliedTag = message.getTag();
                        OrderType sentOrdering = null;
                        FederateAmbassador.SupplementalRemoveInfo removeInfo = null;

                        federateAmbassador.removeObjectInstance(theObject, userSuppliedTag,
                                sentOrdering, removeInfo);
                    }
                    break;


                case PROVIDE_ATTRIBUTE_VALUE_UPDATE:
                    LOGGER.info("PROVIDE_ATTRIBUTE_VALUE_UPDATE: ");
                    federateAmbassador.provideAttributeValueUpdate(
                            (new CertiObjectHandle(((ProvideAttributeValueUpdate1516E) message).getObject())),
                            ((ProvideAttributeValueUpdate1516E) message).getAttributes(), null);
                    break;

                case REQUEST_RETRACTION:
                    break;

                case REQUEST_ATTRIBUTE_OWNERSHIP_ASSUMPTION:
                    federateAmbassador.requestAttributeOwnershipAssumption(
                            new CertiObjectHandle(((RequestAttributeOwnershipAssumption1516E) message).getObject()),
                            ((RequestAttributeOwnershipAssumption1516E) message).getAttributes(),
                            message.getTag());
                    break;

                case REQUEST_ATTRIBUTE_OWNERSHIP_RELEASE:
                    federateAmbassador.requestAttributeOwnershipRelease(
                            new CertiObjectHandle(((RequestAttributeOwnershipRelease1516E) message).getObject()),
                            ((RequestAttributeOwnershipRelease1516E) message).getAttributes(),
                            message.getTag());
                    break;

                case ATTRIBUTE_OWNERSHIP_UNAVAILABLE:
                    federateAmbassador.attributeOwnershipUnavailable(
                            new CertiObjectHandle(((AttributeOwnershipUnavailable1516E) message).getObject()),
                            ((AttributeOwnershipUnavailable1516E) message).getAttributes());
                    break;

                case ATTRIBUTE_OWNERSHIP_ACQUISITION_NOTIFICATION:
                    int object = ((AttributeOwnershipAcquisitionNotification1516E) message).getObject();
                    ObjectInstanceHandle objectHandle = new CertiObjectHandle(object);
                    AttributeHandleSet attributeHandleSet = ((AttributeOwnershipAcquisitionNotification1516E) message).getAttributes();
                    byte[] tag = message.getTag();
                    federateAmbassador.attributeOwnershipAcquisitionNotification(
                            objectHandle,
                            attributeHandleSet,
                            tag);
                    break;

//                case ATTRIBUTE_OWNERSHIP_DIVESTITURE_NOTIFICATION:
//                    federateAmbassador.attributeOwnershipDivestitureNotification(
//                            ((AttributeOwnershipDivestitureNotification1516E) message).getObject(),
//                            ((AttributeOwnershipDivestitureNotification1516E) message).getAttributes());
//                    break;

                case CONFIRM_ATTRIBUTE_OWNERSHIP_ACQUISITION_CANCELLATION:
                    federateAmbassador.confirmAttributeOwnershipAcquisitionCancellation(
                            new CertiObjectHandle(((ConfirmAttributeOwnershipAcquisitionCancellation1516E) message).getObject()),
                            ((ConfirmAttributeOwnershipAcquisitionCancellation1516E) message).getAttributes());
                    break;

                case INFORM_ATTRIBUTE_OWNERSHIP:
                    federateAmbassador.informAttributeOwnership(
                            new CertiObjectHandle(((InformAttributeOwnership1516E) message).getObject()),
                            new CertiObjectHandle(((InformAttributeOwnership1516E) message).getAttribute()),
                            new CertiObjectHandle(((InformAttributeOwnership1516E) message).getFederate()));
                    break;

                case ATTRIBUTE_IS_NOT_OWNED:
                    federateAmbassador.attributeIsNotOwned(
                            new CertiObjectHandle(((AttributeIsNotOwned1516E) message).getObject()),
                            new CertiObjectHandle(((AttributeIsNotOwned1516E) message).getAttribute()));
                    break;

                case TIME_ADVANCE_GRANT:
                    if(message instanceof CertiMessage1516E)
                        federateAmbassador.timeAdvanceGrant(((CertiMessage1516E)message).getFederationTime1516E());
                    break;

                case TIME_REGULATION_ENABLED:
                    federateAmbassador.timeRegulationEnabled(((CertiMessage1516E)message).getFederationTime1516E());
                    break;

                case TIME_CONSTRAINED_ENABLED:
                    federateAmbassador.timeConstrainedEnabled(((CertiMessage1516E)message).getFederationTime1516E());
                    break;

                default:
                    LOGGER.severe("RTI service requested by RTI is unknown.");
                    throw new RTIinternalError("RTI service requested by RTI is unknown : " + message.getType());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error in " + message.getType() + ": " + ex.getMessage(), ex);
            throw new RTIinternalError(ex.getMessage());
        }
    }

}