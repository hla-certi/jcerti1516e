package integrationTests.JoinFederation;

import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import certi1516e.UavSend;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.NotConnected;
import integrationTests.MyFederateAmbassador;
import java.io.File;
import java.util.logging.Logger;

public class JoinFederation {
    CertiRtiAmbassador rtia;
    MyFederateAmbassador mya;
    private final static Logger LOGGER = Logger.getLogger(JoinFederation.class.getName());
    RTIExecutor rtiExecutor;

    /**
     *  Test the create federation service
     *  Succeed if connection went well
     *  Fail if an error occurs
     */
    public static void main(String[] args) {
        JoinFederation test = new JoinFederation();
        try {
            test.executeRTIG();
            test.init();
            String[] joinModules = {"uav.xml"};

            //Try to join a federation without being connected
            try {
                test.rtia.joinFederationExecution("uav_notConnectedError", "uav", "uav_JoinFederation", joinModules);
            } catch (NotConnected eX1){
                LOGGER.info("1. Exception NotConnected correctly caught. ");
            }
            test.connection();
            test.createFederation();

            //Try to join a federation that doesn't exist
            try {
                test.rtia.joinFederationExecution("uav", "uav", "uav_JoinFederation_2", joinModules);
            } catch (FederationExecutionDoesNotExist eX1){
                LOGGER.info("2. Exception FederationExecutionDoesNotExist correctly caught. ");
            }

            //Join federation without error
            test.rtia.joinFederationExecution("uav", "uav", "uav_JoinFederation", joinModules);
            LOGGER.info("3. Join federation execution worked fine.");

            //Try to join a federation with a federate name already used
            try {
                test.rtia.joinFederationExecution("uav", "uav", "uav_JoinFederation", joinModules);
            } catch (FederateAlreadyExecutionMember eX1){
                LOGGER.info("4. Exception FederateAlreadyExecutionMember correctly caught. ");
            }
            System.out.println("*********** TEST SUCCEED ************");

        } catch (Exception e){
            e.printStackTrace();
            LOGGER.severe("*********** TEST FAILED ************");
        } finally {
            test.killConnectionAndKillRTIG();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Functionnalities used ///////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * Lauch the RTIG
     */
    public void executeRTIG() {
        rtiExecutor = new RTIExecutor();
        try {
            rtiExecutor.executeRTIG();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  Initialization
     */
    public void init() {
        try {
            RtiFactory factory = RtiFactoryFactory.getRtiFactory();
            rtia = (CertiRtiAmbassador) factory.getRtiAmbassador();
            mya = new MyFederateAmbassador();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  Connect this federate to the rtig
     */
    public void connection() throws ConnectionFailed {
        try {
            rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
        } catch(Exception e){
            if(!RTIExecutor.checkLocalHost())
                throw new ConnectionFailed("Connection to the RTIG failed. You are trying to connect to a RTIG of an other machine, but no RTIG was found.");
            else
                throw new ConnectionFailed("Connection to the RTIG failed. There is probably no RTIG running.");
        }
    }

    /**
     * Create federation service
     */
    public void createFederation(){
        try {
            String fom = "uav.xml";
            rtia.createFederationExecution("uav_JoinFederation", fom);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * End correctly the rtia and rtig
     */
    public void killConnectionAndKillRTIG(){
        try{
            rtia.resignFederationExecution(ResignAction.NO_ACTION);
            rtia.destroyFederationExecution("uav_JoinFederation");
            rtia.disconnect();
            rtiExecutor.killRTIG();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
