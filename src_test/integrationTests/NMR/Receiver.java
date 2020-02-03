package integrationTests.NMR;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.*;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.jlc.HLAASCIIstringImpl;

import java.io.IOException;

/**
 * Class Receiver witch create a new specific FederateAmbassador
 * And manage connection to a federation to receive values
 */
public class Receiver {

    //Set times values : initialize all variables here
    private double timeStepNMRreceiver = 20.0;
    private double updateTimeNMRreceiver = 0.2;
    private double lookaheadReceiver = 0.1;
    private String fomName = "uav.xml";

    private static final double BLOCKING_TIME = 0.1;
    private CertiRtiAmbassador rtia_receive;
    private ReceiverFederateAmbassador mya_receive;

    /**
     * Function called by the test to create a new federate and listen a federation to receive and check values
     * @param expectedFloat : float value expected to received by the RAV
     * @param expectedString : String value expected to received by the RAV
     * @param expectedTime : time we expect for the RAV
     * @return true if everything work fine and received time/values match, false if not. This result is used to defined test result
     */
    public boolean receive(float expectedFloat, String expectedString, LogicalTime expectedTime)
            throws InterruptedException, RTIinternalError, CallNotAllowedFromWithinCallback, ConnectionFailed {

        SendandReceiveValues_NMR.LOGGER.info("Receiver .........UAV-RECEIVE");
        SendandReceiveValues_NMR.LOGGER.info("Receiver .........1. Get a link to the RTI");
        RtiFactory factory;
        try{
            factory = RtiFactoryFactory.getRtiFactory();
            rtia_receive = (CertiRtiAmbassador) factory.getRtiAmbassador();
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }

        mya_receive = new ReceiverFederateAmbassador();

        try {
            rtia_receive.connect(mya_receive, CallbackModel.HLA_IMMEDIATE);
        } catch(Exception e){
            if(!RTIExecutor.checkLocalHost())
                SendandReceiveValues_NMR.LOGGER.severe("Connection to the RTIG failed. You are trying to connect to a RTIG of an other machine, but no RTIG was found.");
            else
                SendandReceiveValues_NMR.LOGGER.severe("Connection to the RTIG failed. There is probably no RTIG running.");
            return false;
        }

        boolean flagCreator;
        String federationExecutionName = "testNMR2";

        SendandReceiveValues_NMR.LOGGER.info("Receiver .........2. Create federation - " + federationExecutionName);
        // The first launched federate creates the federation execution
        try {
            //File fom = new File("uav.xml");
            rtia_receive.createFederationExecution(federationExecutionName, fomName);
            flagCreator = true;
        } catch (FederationExecutionAlreadyExists ex) {
            SendandReceiveValues_NMR.LOGGER.info("Receiver .........Can't create federation. It already exists.");
            flagCreator = false;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        SendandReceiveValues_NMR.LOGGER.info("Receiver .........3. Join federation");
        String[] joinModules = {"uav.xml"};
        String federateName = "fed-receive";
        String federateType = "uav";
        try {
            rtia_receive.joinFederationExecution(federateName, federateType, federationExecutionName, joinModules);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        mya_receive.isCreator = flagCreator;

        SendandReceiveValues_NMR.LOGGER.info("Receiver .........4. Initialize Federate Ambassador");
        try {

            mya_receive.initialize(rtia_receive, timeStepNMRreceiver, updateTimeNMRreceiver, lookaheadReceiver);
           // mya_receive.initialize(rtia_receive, timeStep, updateTime, lookAhead);
            mya_receive.setExpectedValues(((CertiLogicalTime1516E)expectedTime).getTime() ,expectedFloat,expectedString);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        SendandReceiveValues_NMR.LOGGER.info("Receiver Ready to got to the sync point");

        // The first launched federate also registers the synchronization point.
        // It waits the user launches the second federate, come back and press
        // 'Enter'.
        if(mya_receive.isCreator) {
            SendandReceiveValues_NMR.LOGGER.info("Sender ------- 5.1 Wait Sender in Synchronization Point");
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

        SendandReceiveValues_NMR.LOGGER.info("Receiver .........6 RAV Loop");
        while(((CertiLogicalTime1516E) mya_receive.localHlaTime).getTime() <= ((CertiLogicalTime1516E) expectedTime).getTime()) {
            try {
                rtia_receive.nextMessageRequest(mya_receive.timeAdvance);
                SendandReceiveValues_NMR.LOGGER.info("Receiver .........6.1 NMR with time=" + ((CertiLogicalTime1516E) mya_receive.timeAdvance).getTime());
                while (!mya_receive.timeAdvanceGranted) {
                    SendandReceiveValues_NMR.LOGGER.info("Receiver .........NMR evokecallback");
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
        SendandReceiveValues_NMR.LOGGER.info("Receiver .........7 Resign federation execution");
        try {
            rtia_receive.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);
        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }

        SendandReceiveValues_NMR.LOGGER.info("Receiver .........8 Try to destroy federation execution - nofail");
        // Uses a loop with for destroying the federation (with a delay if
        // there are other federates that did not resign yet).
        boolean federationIsActive = true;
        try
        {
            while(federationIsActive){
                try{
                    rtia_receive.destroyFederationExecution(federationExecutionName);
                    federationIsActive = false;
                    SendandReceiveValues_NMR.LOGGER.info("Receiver ......... Federation destroyed by this federate");
                    try {
                        // Give the other federates a chance to finish.
                        Thread.sleep(2000l);
                    } catch (InterruptedException e1) {
                        // Ignore.
                    }
                } catch (FederationExecutionDoesNotExist ex) {
                    SendandReceiveValues_NMR.LOGGER.info("Receiver ......... Federation execution does not exists;"
                            +  "May be the Federation was destroyed by some other federate.");
                    federationIsActive = false;
                }   catch (RTIinternalError e) {
                    SendandReceiveValues_NMR.LOGGER.severe("RTIinternalError: " + e.getMessage());
                    return false;
                } catch (FederatesCurrentlyJoined e) {
                    SendandReceiveValues_NMR.LOGGER.info("Receiver ......... Federates currently joined - can't destroy the execution. Wait some time and try again to destroy the federation.");
                    Thread.sleep(1000);
                } catch (NotConnected notConnected) {
                    notConnected.printStackTrace();
                    return false;
                }
            }
        } finally {
            try {
                SendandReceiveValues_NMR.LOGGER.info("Receiver .........9 Disconect from the rti");
                rtia_receive.disconnect();
            } catch (Exception e) {
                SendandReceiveValues_NMR.LOGGER.info("Receiver ......... Disconnecting failed" );
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
