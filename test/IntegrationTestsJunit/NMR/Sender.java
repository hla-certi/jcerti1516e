package IntegrationTestsJunit.NMR;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import hla.rti1516e.*;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.HLAASCIIstringImpl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Class Sender witch create a new specific FederateAmbassador
 * And manage connection to a federation to send values
 */
public class Sender {

    private static final double BLOCKING_TIME = 0.1;
    private RTIambassador rtia_send;
    private SenderFederateAmbassador mya_send;
    private boolean senderIsCreator;

    //Set times values
    private double updateTimeNMRSender; //Set with parameter expectedTime, to check if the time is correct at test end (in Receiver)
    private final double timeStepNMRsender = 10.0;
    private final double lookaheadSender = 0.1;
    
    //FIXME: for the clarity of the code:
    // - this Sender federate has the parameter updateTime, that must be used in its code
    // - it will send this value that will be considered as a "expected" value for the Receiver
    // But be carefull with the names:
    // - the expected value RAV(t') for the Receiver corresponds to the uav(t') made
    // by the Sender, t'=localtime + updateTime

    /**
     * Function called by the test to create a new federate and listen a federation to receive and check values
     * @param expectedFloat : float value expected to received by the RAV
     * @param expectedString : String value expected to received by the RAV
     * @param updateTime : updateTime for the federate sender. This time is set here because it used to made the UAV
     *                   and we need to know UAV time to check if RAV time if correct
     * @return true if everything work fine, false if not. This result is used to defined test result
     */
    public boolean send(float expectedFloat, String expectedString, LogicalTime updateTime) throws IOException, InterruptedException {

        updateTimeNMRSender = ((CertiLogicalTime1516E)updateTime).getTime();

        //Initialize values
        RtiFactory factory = null;
        try {
            factory = RtiFactoryFactory.getRtiFactory();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
            return false;
        }
        try {
            rtia_send = factory.getRtiAmbassador();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
            return false;

        }
        mya_send = new SenderFederateAmbassador();

        //Connect to the RTIG
        try {
            System.out.println("Sender ------- 1. Get a link to the RTI");
            rtia_send.connect(mya_send, CallbackModel.HLA_IMMEDIATE);

        } catch (Exception e) {
            System.out.println("Error in the connexion to the RTI");
            e.printStackTrace();
            return false;

        }

        //Create Federation
        File fom = new File("uav.xml");
        String federationExecutionName = "testNMR";
        try {
            System.out.println("Sender ------- 2. Create federation");
            rtia_send.createFederationExecution(federationExecutionName,  fom.toURI().toURL());
            senderIsCreator = true;
        } catch (FederationExecutionAlreadyExists ex ) {
            System.out.println("Sender ------- Can't create federation. It already exists.");
            senderIsCreator = false;
        } catch( MalformedURLException url )
        {
            url.printStackTrace();
            return false;

        } catch(InconsistentFDD | ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError ex){
            return false;
        }

        //Join Federation
        URL[] joinModules = new URL[]{
                (new File("uav.xml")).toURI().toURL()
        };
        String federateName = "fed-send";
        String federateType = "uav";
        try {
            System.out.println("Sender ------- 3. Join federation");
            rtia_send.joinFederationExecution(federateName, federateType, federationExecutionName, joinModules);
        } catch (CouldNotCreateLogicalTimeFactory couldNotCreateLogicalTimeFactory) {
            couldNotCreateLogicalTimeFactory.printStackTrace();
            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }
        mya_send.isCreator = senderIsCreator;

        //Initialize Federate Ambassador
         try {
            System.out.println("Sender ------- 4. Initialize Federate Ambassador");
            // FIXME: why the update time is the expected time?
            // Because UAV is made with time t = updateTime
           mya_send.initialize(rtia_send, timeStepNMRsender, updateTimeNMRSender, lookaheadSender);
            //mya_send.initialize(rtia_send, timeStep, updateTime, lookAhead);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

        if (mya_send.isCreator){
            System.out.println("Sender ------- 5.1 Wait Receiver in Synchronization Point");
            while(SendandReceiveValues_NMR.syncroPoint.get() == false){
                //Wait Receiver in Synchronization Point
                Thread.sleep(100);
            }
            System.out.println("Sender ------- 5.2 Synchronization Point achieved in receiver");
            //System.in.read();

            HLAASCIIstring s = new HLAASCIIstringImpl("InitSync");
            byte[] tagsyns = new byte[s.getEncodedLength()];
            ByteWrapper bw = new ByteWrapper(tagsyns);
            s.encode(bw);

            try {
                rtia_send.registerFederationSynchronizationPoint(mya_send.synchronizationPointName, tagsyns);
            } catch (Exception e) {
                e.printStackTrace();
                return false;

            }

            // Wait synchronization point succeeded callback
            while(!mya_send.synchronizationSuccess && ! mya_send.synchronizationFailed){
                try {
                    rtia_send.evokeCallback(BLOCKING_TIME);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;

                }
            }
        } else {
            SendandReceiveValues_NMR.syncroPoint.set(true);
        }


        // Wait synchronization point announcement (announceSynchronizationPoint callback)
        while (!mya_send.inPause) {
            try {
                rtia_send.evokeCallback(BLOCKING_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        // Informs the RTIG it is aware of the synchronization point "synchronizationPointName"
        try {
            rtia_send.synchronizationPointAchieved(mya_send.synchronizationPointName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Wait the callback federationSynchronized()
        while (mya_send.inPause)
        {
            try {
                rtia_send.evokeCallback(BLOCKING_TIME);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        System.out.println("Sender ------- 6. UAV Loop");
//        int i = 1; //0 with stopTime  4
//        while (i-- > 0) {
        //Text attribute
        HLAASCIIstring text =  new HLAASCIIstringImpl(expectedString);
        byte[] textAttribute = new byte[text.getEncodedLength()];
        ByteWrapper textWrapper = new ByteWrapper(textAttribute);
        text.encode(textWrapper);

        //Float attribute
        HLAfloat32BE value = new BasicHLAfloat32BEImpl((float) expectedFloat);
        byte[] fomAttribute = new byte[value.getEncodedLength()];
        ByteWrapper fomWrapper = new ByteWrapper(fomAttribute);
        value.encode(fomWrapper);

        AttributeHandleValueMap attr = null;
        try {
            attr = rtia_send.getAttributeHandleValueMapFactory().create(2);
        } catch (FederateNotExecutionMember federateNotExecutionMember) {
            federateNotExecutionMember.printStackTrace();
            return false;
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
            return false;
        }

        attr.put(mya_send.textAttributeHandle, textAttribute);
        attr.put(mya_send.fomAttributeHandle, fomAttribute);

        //Tag
        HLAASCIIstring tag =  new HLAASCIIstringImpl("update");
        byte[] tagBuffer = new byte[tag.getEncodedLength()];
        ByteWrapper tagWrapper = new ByteWrapper(tagBuffer);
        text.encode(tagWrapper);

        // The UAV must be executed outside an advance time loop. The
        // timestamp 'updateTime' is updated when a TAG is received.
        System.out.println("Sender ------- 6.1 UAV with time = " + ((CertiLogicalTime1516E)mya_send.updateTime).getTime());
        try {
            rtia_send.updateAttributeValues(mya_send.myObject, attr, tagBuffer, mya_send.updateTime);
        } catch (Exception e){
            System.out.println("Sender ------- Timestamp must be bigger than (localHlaTime + lookahead)" );
            return false;
        }
        // The federate ask to advance to (current logical time + timeStep)
        System.out.println("Sender ------- 6.2 NMR with time = " + ((CertiLogicalTime1516E)mya_send.timeAdvance).getTime());
        try {
            rtia_send.nextMessageRequest(mya_send.timeAdvance);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        while(!mya_send.timeAdvanceGranted){
            try {
                rtia_send.evokeCallback(BLOCKING_TIME);
            } catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }
        mya_send.timeAdvanceGranted = false;
        Thread.sleep(1000);
//        }
        System.out.println("Sender ------- 7. Resign federation execution");
        try {
            rtia_send.resignFederationExecution( ResignAction.DELETE_OBJECTS );
        } catch (InvalidResignAction invalidResignAction) {
            invalidResignAction.printStackTrace();
        } catch (OwnershipAcquisitionPending ownershipAcquisitionPending) {
            ownershipAcquisitionPending.printStackTrace();
        } catch (FederateOwnsAttributes federateOwnsAttributes) {
            federateOwnsAttributes.printStackTrace();
        } catch (FederateNotExecutionMember federateNotExecutionMember) {
            federateNotExecutionMember.printStackTrace();
        } catch (NotConnected notConnected) {
            notConnected.printStackTrace();
        } catch (CallNotAllowedFromWithinCallback callNotAllowedFromWithinCallback) {
            callNotAllowedFromWithinCallback.printStackTrace();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        }

        System.out.println("Sender ------- 8. Destroy federation execution - nofail");
        // Uses a loop for destroying the federation (with a delay if
        // there are other federates that did not resign yet).
        boolean federationIsActive = true;
        try
        {
            while(federationIsActive){
                try{
                    rtia_send.destroyFederationExecution( federationExecutionName );
                    federationIsActive = false;
                    System.out.println("Warning: Sender ------- Federation destroyed by this federate");
                } catch( FederationExecutionDoesNotExist dne ) {
                    System.out.println("Warning: Sender ------- Federation execution doesn't exist.  May be the Federation was destroyed by some other federate." );
                    federationIsActive = false;
                } catch( FederatesCurrentlyJoined e ) {
                    System.out.println("Warning: Sender ------- Couldn't destroy federation" );
                } catch  (RTIinternalError e){
                    System.out.println("Sender ------- RTIinternalError: " + e.getMessage());
                } catch (NotConnected notConnected) {
                    notConnected.printStackTrace();
                }
            }
        } finally {
            System.out.println("Sender ------- 9. Disconect from the rti");
            try {
                rtia_send.disconnect();
            } catch (FederateIsExecutionMember federateIsExecutionMember) {
                federateIsExecutionMember.printStackTrace();
                return false;
            } catch (CallNotAllowedFromWithinCallback callNotAllowedFromWithinCallback) {
                callNotAllowedFromWithinCallback.printStackTrace();
                return false;
            } catch (RTIinternalError rtIinternalError) {
                rtIinternalError.printStackTrace();
                return false;
            }
        }
        return true;

    }

}
