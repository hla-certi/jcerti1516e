package certi1516e;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiLogicalTimeInterval1516E;
import certi.rti1516e.impl.CertiRtiAmbassador;
import hla.rti1516e.*;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.impl.CertiAttributeHandleSet;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.HLAASCIIstringImpl;
import hla.rti1516e.jlc.NullFederateAmbassador;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

//////////////
// UAV-SEND //
//////////////

/**
 * This class implements a HLA federate. It is based on the JCERTI demo 
 * compliant to HLA 1.3. It extends that class for be compliant with HLA 
 * 1516-2010 Extended (HLA 1516e), and introduces some parameters. It creates
 * (if launched first) and joins a federation called federationExecutionName, 
 * advances its logical time with other federates and updates attributes of an
 * instance of a class.
 * </p><p>The synchronization point 'InitSync' is registered by the first launched
 * federate. After launching the second federate, the user must press 'Enter'
 * in the terminal where the first one was launched. The first federate then
 * sends synchronizationPointAchieved() and the RTI provides the callback
 * federationSynchronized() for both federates.
 * </p><p>
 * This federate is called by the following command line, e.g.:
 * ant -DtimeStep=20 -DupdateTime=5 -Dlookahead=1 UAVSend1516e-run
 * <ul>
 * <li> lookahead: according to HLA, the federate promises it will not send any
 * message in the interval (h, h+lookahead), where 'h' is the current logical time.
 * <li> timeStep: the federate will ask to advance its time by steps with this 
 * value, TAR(h + timeStep), 'h' is the current logical time.
 * <li> updateTime: the federate will update attributes with timestamp t = 
 * (h + updateTime), 'h' is the current logical time. For a correct execution,
 * updateTime > lookahead. Otherwise the exception INVALID_FEDERATION_TIME is raised.
 * </ul>
 *  </p><p>
 *  The time advance phase in HLA is a two-step  process: 1) a federate sends 
 *  a time advance request service and 2) waits for the time to be granted by 
 *  the timeAdvanceGrant (TAG) service. Federates using TAR are called time-
 *  stepped federates. If TAR is being used, the time returned always equals 
 *  the asked time h'.
 */
//Execute the RTIG, create and join a federation and send various values
//FIXME: this class does not execute the RTIG: the RTIG must be launched by the first
//launched federate

public class UavSendString {

    private final static Logger LOGGER = Logger.getLogger(UavSendString.class.getName());
    
    /** The sync point all federates will sync up on before starting */
    public static final String READY_TO_RUN = "ReadyToRun";
    
    //private static final double stopTime = 50.0; //If used in "Uav Loop".
    private ObjectInstanceHandle myObject;
    AttributeHandle textAttributeHandle;
    AttributeHandle fomAttributeHandle;

    /**
     * Run a federate since its creation to its destruction
     * Updates values of two attributes (float and string) to a federation
     */
    public void runFederate(double timeStepArg, double uptdateTimeArg, double lookaheadArg) throws Exception {

        LOGGER.info("        UAV-SEND");
        LOGGER.info("     1. Get a link to the RTI");
        
        RtiFactory factory = (RtiFactory) RtiFactoryFactory.getRtiFactory();
        CertiRtiAmbassador rtia = (CertiRtiAmbassador) factory.getRtiAmbassador();
        MyFederateAmbassador mya = new MyFederateAmbassador();
        rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
        boolean flagCreator;
        String federationExecutionName = "uav";
        System.out.println();
        LOGGER.info("     2. Create federation - nofail");
        // The first launched federate creates the federation execution
        try {
            String fomName=  "uav.xml";
            rtia.createFederationExecution(federationExecutionName,  fomName);
            flagCreator = true;
        } catch (FederationExecutionAlreadyExists ex) {
            LOGGER.warning("Can't create federation. It already exists.");
            flagCreator = false;
        }
        System.out.println();
        LOGGER.info("     3. Join federation");
        String[] joinModules = {"uav.xml"};
        String federateName = "uav-send";
        String federateType = "uav";
        rtia.joinFederationExecution(federateName, federateType, federationExecutionName, joinModules);
        mya.isCreator = flagCreator;

        System.out.println();
        LOGGER.info("     4. Initialize Federate Ambassador");
        mya.initialize(rtia, timeStepArg, uptdateTimeArg, lookaheadArg);

        System.out.println();
        // The first launched federate also registers the synchronization point.
        // It waits the user launches the second federate, come back and press 
        // 'Enter'. 
        if (mya.isCreator){
            LOGGER.info("     5. After launch the other federate, press 'Enter' so this federate can register the Synchronization Point ");
            System.in.read();
            HLAASCIIstring s = new HLAASCIIstringImpl("InitSync");
            byte[] tagsyns = new byte[s.getEncodedLength()];
            ByteWrapper bw = new ByteWrapper(tagsyns);
            s.encode(bw);

            rtia.registerFederationSynchronizationPoint(mya.synchronizationPointName, tagsyns);
            // Wait synchronization point succeeded callback
            while(!mya.synchronizationSuccess && ! mya.synchronizationFailed){
                rtia.evokeCallback(BLOCKING_TIME);
            }
        } else {
            LOGGER.info("     5S. Waiting for the creator of the Federation to register the Sync Point ");
        }

        // Wait synchronization point announcement (announceSynchronizationPoint callback)
        while (!mya.inPause) {
            rtia.evokeCallback(BLOCKING_TIME);
        }
        // Informs the RTIG it is aware of the synchronization point "synchronizationPointName"
        rtia.synchronizationPointAchieved(mya.synchronizationPointName);

        // Wait the callback federationSynchronized() 
        while (((MyFederateAmbassador) mya).inPause)
        {
            rtia.evokeCallback(BLOCKING_TIME);
        }

        System.out.println();
        // According to the relation between the timeStep of both 
        // federates, UAVReceive can have a lot of "FEDERATES_CURRENTLY_JOINED"
        // messages, before the federation is destroyed by UavSend. The results 
        // are almost the same using while(i-- > 0) or while (... < stopTime). 
        // For example, for UavSend-timeStep=10 and UavReceive-timeStep=X, if 
        // X>14, there are a few messages. But there are hundred of messages if X<13.
        // It depends on the value of "i" in 'Uav loop' and in 'Rav loop'.
        // FIXME: may be this can be minimized.
        
        int i = 10; //0 with stopTime  4
        LOGGER.info("     6 Uav Loop");
        while (i-- > 0) {
        //while (((CertiLogicalTime1516E) mya.timeAdvance).getTime() < stopTime) {i++;
            //Text attribute
            HLAASCIIstring text =  new HLAASCIIstringImpl("text " + i);
            byte[] textAttribute = new byte[text.getEncodedLength()];
            ByteWrapper textWrapper = new ByteWrapper(textAttribute);
            text.encode(textWrapper);

            //Float attribute
            HLAfloat32BE fom = new BasicHLAfloat32BEImpl((float) 3.14);
            byte[] fomAttribute = new byte[fom.getEncodedLength()];
            ByteWrapper fomWrapper = new ByteWrapper(fomAttribute);
            fom.encode(fomWrapper);

            AttributeHandleValueMap attr = rtia.getAttributeHandleValueMapFactory().create(2);
            attr.put(textAttributeHandle, textAttribute);
            attr.put(fomAttributeHandle, fomAttribute);

            //Tag
            HLAASCIIstring tag =  new HLAASCIIstringImpl("update");
            byte[] tagBuffer = new byte[tag.getEncodedLength()];
            ByteWrapper tagWrapper = new ByteWrapper(tagBuffer);
            text.encode(tagWrapper);

            // The UAV must be executed outside an advance time loop. The 
            // timestamp 'updateTime' is updated when a TAG is received.
            LOGGER.info("     6.1 UAV with time = " + ((CertiLogicalTime1516E)mya.updateTime).getTime());
            try {
                 rtia.updateAttributeValues(myObject, attr, tagBuffer, mya.updateTime);
            } catch (Exception e){
                LOGGER.info( "Timestamp must be bigger than (localHlaTime + lookahead)" );
            }
            // The federate ask to advance to (current logical time + timeStep)
            LOGGER.info("     6.2 TAR with time = " + ((CertiLogicalTime1516E)mya.timeAdvance).getTime());
            rtia.timeAdvanceRequest(mya.timeAdvance);
            while(!mya.timeAdvanceGranted){
                try {
                    rtia.evokeCallback(BLOCKING_TIME);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            mya.timeAdvanceGranted = false;
            Thread.sleep(1000);
        }

        System.out.println();

        LOGGER.info("     7 Resign federation execution");
        rtia.resignFederationExecution( ResignAction.DELETE_OBJECTS );

        // FIXME: any federate can destroy the federation if there is no other 
        // joined federate. The following loop would be the same in both federates?
        
        LOGGER.info("     8 Destroy federation execution - nofail");
        // Uses a loop for destroying the federation (with a delay if
        // there are other federates that did not resign yet).
        boolean federationIsActive = true;
        try
        {
            while(federationIsActive){
                try{
                    rtia.destroyFederationExecution( federationExecutionName );
                    federationIsActive = false;
                    LOGGER.warning("Federation destroyed by this federate");
                } catch( FederationExecutionDoesNotExist dne ) {
                    LOGGER.info( "Federation execution doesn't exist.  May be the Federation was destroyed by some other federate." );
                    federationIsActive = false;
                } catch( FederatesCurrentlyJoined e ) {
                    LOGGER.info( "Couldn't destroy federation" );
                } catch  (RTIinternalError e){
                    LOGGER.info("RTIinternalError: " + e.getMessage());
                }
            }
        } finally {
            LOGGER.info("     9 Disconect from the rti");
            rtia.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            double timeStepArg = Double.valueOf(args[2]);
            double updateTimeArg = Double.valueOf(args[3]);
            double lookahead = Double.valueOf(args[4]);
            new UavSendString().runFederate(timeStepArg, updateTimeArg, lookahead);
        } catch(NumberFormatException exception) {
            new UavSendString().runFederate(10.0, 0.2, 0.1);
        }

    }

    /**
     * Implementation of a FederateAmbassador
     */
    
    private static double BLOCKING_TIME = 0.1;
    
    public class MyFederateAmbassador extends NullFederateAmbassador {
        public boolean isCreator;
        public LogicalTime localHlaTime;
        public LogicalTimeInterval lookahead;
        public LogicalTime timeStep;
        public LogicalTime timeAdvance;
        public double updateTime1;
        public LogicalTime updateTime;
        public boolean timeAdvanceGranted;
        public boolean timeRegulator;
        public boolean timeConstrained;
        public boolean synchronizationSuccess;
        public boolean synchronizationFailed;
        public boolean inPause;
        private String synchronizationPointName = "InitSync";

        /**
         * The Start Registration For Object Class † service shall notify the joined federate that registration of new
         * object instances of the specified object class is advised because at least one of the class attributes that the
         * joined federate is publishing at this object class is actively subscribed to at the specified object class or at a
         * superclass of the specified object class by at least one other joined federate in the federation execution. The
         * joined federate should commence with registration of object instances of the specified class. Generation of
         * the Start Registration For Object Class † service advisory shall be controlled using the Enable/Disable
         * Object Class Relevance Advisory Switch services. The Start Registration For Object Class † service shall be
         * invoked only when the Object Class Relevance Advisory Switch is enabled for the joined federate.
         * @param theClass : Object class designator.
         */
        @Override
        public void startRegistrationForObjectClass(ObjectClassHandle theClass) {
            System.out.println("Object class: " + theClass);
        }

        /**
         * Initialization of all attributes, publish and register objects
         * Enable time regulation
         * @param rtia : RTI ambassador
         */
        public void initialize(RTIambassador rtia, double timeStepArg, double updateTimeArg, double lookaheadArg)
                throws NameNotFound,
                FederateNotExecutionMember,
                RTIinternalError,
                ObjectClassNotDefined,
                AttributeNotDefined,
                OwnershipAcquisitionPending,
                SaveInProgress,
                RestoreInProgress,
                ObjectClassNotPublished,
                NotConnected,
                InvalidObjectClassHandle,
                ObjectInstanceNameInUse,
                ObjectInstanceNameNotReserved
        {
            LOGGER.info("     4.1 Get object class handle");
            // The uav.xml has a class 'SampleClass' and attributes
            // TextAttribute and FOMAttribute
            ObjectClassHandle classHandle = rtia.getObjectClassHandle("SampleClass");

            LOGGER.info("     4.2 Get atribute handles");
            textAttributeHandle = rtia.getAttributeHandle(classHandle, "TextAttribute");
            fomAttributeHandle = rtia.getAttributeHandle(classHandle, "FOMAttribute");

            AttributeHandleSet attributes = new CertiAttributeHandleSet();
            attributes.add(textAttributeHandle);
            attributes.add(fomAttributeHandle);

            try {
                rtia.enableAsynchronousDelivery();
            } catch (AsynchronousDeliveryAlreadyEnabled asynchronousDeliveryAlreadyEnabled) {
                asynchronousDeliveryAlreadyEnabled.printStackTrace();
            }

            LOGGER.info("     4.3 Publish object");
            rtia.publishObjectClassAttributes(classHandle, attributes);

            LOGGER.info("     4.4 Register object instance");
            myObject = rtia.registerObjectInstance(classHandle, "HAF");

            LOGGER.info("     4.5. Set time management configuration (Regulator with lookahed and Constrained)");

            localHlaTime = new CertiLogicalTime1516E(0.0);
            lookahead = new CertiLogicalTimeInterval1516E(lookaheadArg);

            updateTime1 = updateTimeArg;
            timeStep = new CertiLogicalTime1516E(timeStepArg);
            updateTime = new CertiLogicalTime1516E(updateTimeArg);
            // The time is advanced by adding localHlaTime + timeStep; starts with (0.0+timeStepArg)            
            timeAdvance = new CertiLogicalTime1516E(((CertiLogicalTime1516E) timeStep).getTime());
            timeAdvanceGranted = false;

            try {
                rtia.enableTimeRegulation(lookahead);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                rtia.enableTimeConstrained();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * The Announce Synchronization Point † service shall inform a joined federate of the existence of a new
         * synchronization point label. When a synchronization point label has been registered with the Register
         * Federation Synchronization Point service, the RTI shall invoke the Announce Synchronization Point †
         * service, either at all the joined federates in the federation execution or at the specified set of joined federates,
         * to inform them of the label existence. The joined federates informed of the existence of a given
         * synchronization point label via the Announce Synchronization Point † service shall form the synchronization
         * set for that point. If the optional set of joined federate designators was null or not provided when the
         * synchronization point label was registered, the RTI shall also invoke the Announce Synchronization Point †
         * service at all federates that join the federation execution after the synchronization label was registered but
         * before the RTI has ascertained that all joined federates that were informed of the synchronization label
         * existence have invoked the Synchronization Point Achieved service. These newly joining federates shall also
         * become part of the synchronization set for that point. Joined federates that resign from the federation
         * execution after the announcement of a synchronization point but before the federation synchronizes at that
         * point shall be removed from the synchronization set. The user-supplied tag supplied by the Announce
         * Synchronization Point † service shall be the tag that was supplied to the corresponding Register Federation
         * Synchronization Point service invocation.
         * @param synchronizationPointLabel : Synchronization point label.
         * @param userSuppliedTag : User-supplied tag.
         */
        @Override
        public void announceSynchronizationPoint(
                String synchronizationPointLabel, byte[] userSuppliedTag)
                throws FederateInternalError {
            inPause = true;
        }

        /**
         * The Federation Synchronized † service shall inform the joined federate that all joined federates in the
         * synchronization set of the specified synchronization point have invoked the Synchronization Point Achieved
         * service for that point. This service shall be invoked at all joined federates that are in the synchronization set
         * for that point to indicate that the joined federates in the synchronization set have synchronized at that point.
         * Once the synchronization set for a point synchronizes (i.e., the Federation Synchronized † service has been
         * invoked at all joined federates in the set), that point shall no longer be registered, and the synchronization set
         * for that point shall no longer exist. The set of joined federate designators indicates which federates in the
         * synchronization set invoked the Synchronization Point Achieved service with an indication of an
         * unsuccessful synchronization (via the synchronization-success indicator).
         * @param synchronizationPointLabel : Synchronization point label.
         * @param failedToSyncSet : Set of joined federate designators.
         */
        @Override
        public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet)
                throws FederateInternalError {
            inPause = false;
        }

        /**
         * The Confirm Synchronization Point Registration † service shall indicate to the requesting joined federate the
         * status of a requested federation synchronization point registration. This service shall be invoked in response
         * to a Register Federation Synchronization Point service invocation. A registration-success indicator
         * argument indicating success shall mean the label has been successfully registered.
         * If the registration-success indicator argument indicates failure, the failure reason argument shall be provided
         * to identify the reason that the synchronization point registration failed. Possible reasons for the
         * synchronization point registration failure are the following:
         * — The specified label is already in use.
         * — A synchronization set member is not a joined federate.
         * A registration attempt that ends with a negative success indicator shall have no other effect on the federation
         * execution.
         * @param synchronizationPointLabel : Synchronization point label.
         */
        @Override
        public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
                throws FederateInternalError {
            synchronizationSuccess = true;
        }

        /**
         * The Confirm Synchronization Point Registration † service shall indicate to the requesting joined federate the
         * status of a requested federation synchronization point registration. This service shall be invoked in response
         * to a Register Federation Synchronization Point service invocation. A registration-success indicator
         * argument indicating success shall mean the label has been successfully registered.
         * If the registration-success indicator argument indicates failure, the failure reason argument shall be provided
         * to identify the reason that the synchronization point registration failed. Possible reasons for the
         * synchronization point registration failure are the following:
         * — The specified label is already in use.
         * — A synchronization set member is not a joined federate.
         * A registration attempt that ends with a negative success indicator shall have no other effect on the federation
         * execution.
         * @param synchronizationPointLabel : Synchronization point label.
         * @param reason : Optional failure reason.
         */
        @Override
        public void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
                                                           SynchronizationPointFailureReason reason) throws FederateInternalError {
            synchronizationFailed = true;
        }

        /**
         * The Discover Object Instance † service shall inform the joined federate to discover an object instance.
         * Object instance discovery is described in 6.1. The object instance handle shall be unique to the federation
         * execution and shall be uniform (see 6.8) throughout the federation execution.
         * If the Convey Producing Federate Switch for this joined federate is enabled, the producing joined federate
         * argument shall contain the designator for the joined federate that registered the object instance. This
         * producing joined federate may not own instance attributes that caused the discovery, and, in fact, it may be
         * no longer joined to the federation execution.
         * @param theObject : Object instance handle.
         * @param theObjectClass : Object class designator.
         * @param objectName : Object instance name.
         * @throws FederateInternalError
         */
        @Override
        public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
                                           String objectName) throws FederateInternalError {
            LOGGER.info("Discover Object Instance : "
                        + "Object = " + theObject.toString()
                        + ", Object class = " + theObjectClass.toString()
                        + ", Object name = " + objectName );
        }

        /**
         * The Discover Object Instance † service shall inform the joined federate to discover an object instance.
         * Object instance discovery is described in 6.1. The object instance handle shall be unique to the federation
         * execution and shall be uniform (see 6.8) throughout the federation execution.
         * If the Convey Producing Federate Switch for this joined federate is enabled, the producing joined federate
         * argument shall contain the designator for the joined federate that registered the object instance. This
         * producing joined federate may not own instance attributes that caused the discovery, and, in fact, it may be
         * no longer joined to the federation execution.
         * @param theObject : Object instance handle.
         * @param theObjectClass : Object class designator.
         * @param objectName : Object instance name.
         * @param producingFederate : Optional producing joined federate designator.
         * @throws FederateInternalError
         */
        @Override
        public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
                                           String objectName, FederateHandle producingFederate) throws FederateInternalError {
            LOGGER.info("Discover Object Instance : "
                    + "Object = " + theObject.toString()
                    + ", Object class = " + theObjectClass.toString()
                    + ", Object name = " + objectName
                    + ", Producing federate = " + producingFederate.toString());
            System.out.println();

        }

        /**
         * Invocation of the Time Advance Grant † service shall indicate that a prior request to advance the joined
         * federate’s logical time has been honored. The argument of this service shall indicate that the logical time for
         * the joined federate has been advanced to this value.
         * If the grant is issued in response to invocation of a Next Message Request or Time Advance Request service,
         * the RTI shall guarantee that no additional TSO messages shall be delivered in the future with timestamps
         * less than or equal to this value.
         * If the grant is in response to an invocation of a Time Advance Request Available, Next Message Request
         * Available, or Flush Queue Request service, the RTI shall guarantee that no additional TSO messages shall be
         * delivered in the future with timestamps less than the value of the grant.
         * @param theTime : Logical time.
         */
        @Override
        public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
            localHlaTime = new CertiLogicalTime1516E(((CertiLogicalTime1516E) theTime).getTime());
            timeAdvance = new CertiLogicalTime1516E(((CertiLogicalTime1516E) localHlaTime).getTime()
                    + ((CertiLogicalTime1516E) timeStep).getTime());
            updateTime = new CertiLogicalTime1516E(((CertiLogicalTime1516E) localHlaTime).getTime() + updateTime1);
            LOGGER.info("     6.3 TAG with time = " + ((CertiLogicalTime1516E) theTime).getTime());
            timeAdvanceGranted = true;
            System.out.println();

        }

        /**
         * The Provide Attribute Value Update † service requests the current values for attributes owned by the joined
         * federate for a given object instance. The owning joined federate responds to the Provide Attribute Value
         * Update † service with an invocation of the Update Attribute Values service to provide the requested instance
         * attribute values to the federation. The owning joined federate is not required to provide the values.
         * The user-supplied tag argument supplied to the corresponding Request Attribute Value Update service
         * invocation shall be provided with all corresponding Provide Attribute Value Update † service invocations.
         * @param theObject : Object instance designator.
         * @param theAttributes : Set of attribute designators.
         * @param userSuppliedTag : User-supplied tag.
         */
        @Override
        public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
                                                byte[] userSuppliedTag) throws FederateInternalError {
            LOGGER.info("Object handle hashCode : " + theObject.hashCode());
            LOGGER.info("Attributes : ");
            for (AttributeHandle attributeHandle : theAttributes) {
                LOGGER.info("Hash : " + attributeHandle.hashCode());
            }
            System.out.println();
        }
    }
}


   
