package IntegrationTests.NMR;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import hla.rti1516e.*;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.jlc.HLAASCIIstringImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class Receiver witch create a new specific FederateAmbassador
 * And manage connection to a federation to receive values
 */
public class Receiver {

    //Set times values : initialize all variables here
    private double timeStepNMRreceiver = 20.0;
    private double updateTimeNMRreceiver = 0.2;
    private double lookaheadReceiver = 0.1;

    private static final double BLOCKING_TIME = 0.1;
    private RTIambassador rtia_receive;
    private ReceiverFederateAmbassador mya_receive;

    /**
     * Function called by the test to create a new federate and listen a federation to receive and check values
     * @param expectedFloat : float value expected to received by the RAV
     * @param expectedString : String value expected to received by the RAV
     * @param expectedTime : time we expect for the RAV
     * @return true if everything work fine and received time/values match, false if not. This result is used to defined test result
     */
    public boolean receive(float expectedFloat, String expectedString, LogicalTime expectedTime)
            throws IOException, InterruptedException, RTIinternalError, CallNotAllowedFromWithinCallback {

        System.out.println("Receiver .........UAV-RECEIVE");
        System.out.println("Receiver .........1. Get a link to the RTI");
        RtiFactory factory;
        try{
            factory = RtiFactoryFactory.getRtiFactory();
            rtia_receive = factory.getRtiAmbassador();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        mya_receive = new ReceiverFederateAmbassador();
        try {
            rtia_receive.connect(mya_receive, CallbackModel.HLA_IMMEDIATE);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        boolean flagCreator;
        String federationExecutionName = "testNMR";

        System.out.println("Receiver .........2. Create federation");
        // The first launched federate creates the federation execution
        try {
            File fom = new File("uav.xml");
            rtia_receive.createFederationExecution(federationExecutionName, fom.toURI().toURL());
            flagCreator = true;
        } catch (FederationExecutionAlreadyExists ex) {
            System.out.println("Receiver .........Can't create federation. It already exists.");
            flagCreator = false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        System.out.println("Receiver .........3. Join federation");
        URL[] joinModules = new URL[]{
                (new File("uav.xml")).toURI().toURL()
        };
        String federateName = "fed-receive";
        String federateType = "uav";

        try {
            rtia_receive.joinFederationExecution(federateName, federateType, federationExecutionName, joinModules);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mya_receive.isCreator = flagCreator;

        System.out.println("Receiver .........4. Initialize Federate Ambassador");
        try {

            mya_receive.initialize(rtia_receive, timeStepNMRreceiver, updateTimeNMRreceiver, lookaheadReceiver);
           // mya_receive.initialize(rtia_receive, timeStep, updateTime, lookAhead);
            mya_receive.setExpectedValues(((CertiLogicalTime1516E)expectedTime).getTime() ,expectedFloat,expectedString);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        System.out.println("Receiver Ready to got to the sync point");

        // The first launched federate also registers the synchronization point.
        // It waits the user launches the second federate, come back and press
        // 'Enter'.
        if(mya_receive.isCreator) {
            System.out.println("Sender ------- 5.1 Wait Sender in Synchronization Point");

            while(SendandReceiveValues_NMR.syncroPoint.get() == false){
                Thread.sleep(100);
            }

            HLAASCIIstring tagSync = new HLAASCIIstringImpl("InitSync");
            byte[] tagBuffer = new byte[tagSync.getEncodedLength()];
            ByteWrapper tagWrapper = new ByteWrapper(tagBuffer);
            tagSync.encode(tagWrapper);

            try {
                rtia_receive.registerFederationSynchronizationPoint(mya_receive.synchronizationPointName, tagBuffer);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            // Wait synchronization point succeeded callback
            while(!mya_receive.synchronizationSuccess && !mya_receive.synchronizationFailed){
                rtia_receive.evokeCallback(BLOCKING_TIME);
            }
        } else {
            SendandReceiveValues_NMR.syncroPoint.set(true);
        }

        // Wait synchronization point announcement (announceSynchronizationPoint callback)
        while(!mya_receive.inPause){
            rtia_receive.evokeCallback(BLOCKING_TIME);
        }

        // Informs the RTIG it is aware of the synchronization point "synchronizationPointName"
        try {
            rtia_receive.synchronizationPointAchieved(mya_receive.synchronizationPointName);
        } catch (SynchronizationPointLabelNotAnnounced | SaveInProgress | RestoreInProgress | FederateNotExecutionMember | NotConnected | RTIinternalError synchronizationPointLabelNotAnnounced) {
            synchronizationPointLabelNotAnnounced.printStackTrace();
            return false;
        }

        // Wait the callback federationSynchronized()
        while(mya_receive.inPause){
            try {
                rtia_receive.evokeCallback(BLOCKING_TIME);
            } catch (CallNotAllowedFromWithinCallback callNotAllowedFromWithinCallback) {
                callNotAllowedFromWithinCallback.printStackTrace();
                return false;
            } catch (RTIinternalError rtIinternalError) {
                rtIinternalError.printStackTrace();
                return false;
            }
        }

        System.out.println("Receiver .........6 RAV Loop");
        //System.out.println("Receiver .........6.1 NMR with time=" + ((CertiLogicalTime1516E) mya_receive.timeAdvance).getTime());
        while(((CertiLogicalTime1516E) mya_receive.localHlaTime).getTime() <= ((CertiLogicalTime1516E) expectedTime).getTime()) {
            try {
                rtia_receive.nextMessageRequest(mya_receive.timeAdvance);
                System.out.println("Receiver .........6.1 NMR with time=" + ((CertiLogicalTime1516E) mya_receive.timeAdvance).getTime());
                while (!mya_receive.timeAdvanceGranted) {
                    System.out.println("Receiver .........NMR evokecallback");
                    rtia_receive.evokeCallback(BLOCKING_TIME);
                }
                mya_receive.timeAdvanceGranted = false;
            } catch (LogicalTimeAlreadyPassed logicalTimeAlreadyPassed) {
                logicalTimeAlreadyPassed.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        System.out.println("Receiver .........7 Resign federation execution");
        try {
            rtia_receive.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

        System.out.println("Receiver .........8 Try to destroy federation execution - nofail");
        // Uses a loop with for destroying the federation (with a delay if
        // there are other federates that did not resign yet).
        boolean federationIsActive = true;
        try
        {
            while(federationIsActive){
                try{
                    rtia_receive.destroyFederationExecution(federationExecutionName);
                    federationIsActive = false;
                    System.out.println("Receiver ......... Federation destroyed by this federate");
                    try {
                        // Give the other federates a chance to finish.
                        Thread.sleep(2000l);
                    } catch (InterruptedException e1) {
                        // Ignore.
                    }
                } catch (FederationExecutionDoesNotExist ex) {
                    System.out.println("Receiver ......... Federation execution does not exists;"
                            +  "May be the Federation was destroyed by some other federate.");
                    federationIsActive = false;
                }   catch (RTIinternalError e) {
                    System.out.println("RTIinternalError: " + e.getMessage());
                    return false;
                } catch (FederatesCurrentlyJoined e) {
                    System.out.println("Receiver ......... Federates currently joined - can't destroy the execution. Wait some time and try again to destroy the federation.");
                    Thread.sleep(1000);
                } catch (NotConnected notConnected) {
                    notConnected.printStackTrace();
                    return false;
                }
            }
        } finally {
            try {
                System.out.println("Receiver .........9 Disconect from the rti");
                rtia_receive.disconnect();
            } catch (Exception e) {
                System.out.println("Receiver ......... Disconnecting failed" );
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
