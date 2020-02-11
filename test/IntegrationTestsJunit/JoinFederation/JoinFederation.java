package IntegrationTestsJunit.JoinFederation;

import IntegrationTestsJunit.MyFederateAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.NotConnected;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class JoinFederation {
    RTIambassador rtia;
    MyFederateAmbassador mya;

    /**
     *  Test the create federation service
     *  Succeed if connection went well
     *  Fail if an error occurs
     */
    @Test
    public void joinFederation(){
        try {
            executeRTIG();
            init();
            URL[] joinModules = new URL[]{(new File("uav.xml")).toURI().toURL()};

            //Try to join a federation without being connected
            try {
                rtia.joinFederationExecution("uav_notConnectedError", "uav", "uav_JoinFederation", joinModules);
            } catch (NotConnected eX1){
                System.out.println("1. Exception NotConnected correctly caught. ");
            }
            connection();
            createFederation();

            //Try to join a federation that doesn't exist
            try {
                rtia.joinFederationExecution("uav", "uav", "uav_JoinFederation_2", joinModules);
            } catch (FederationExecutionDoesNotExist eX1){
                System.out.println("2. Exception FederationExecutionDoesNotExist correctly caught. ");
            }

            //Join federation without error
            rtia.joinFederationExecution("uav", "uav", "uav_JoinFederation", joinModules);
            System.out.println("3. Join federation execution worked fine.");

            //Try to join a federation with a federate name already used
            try {
                rtia.joinFederationExecution("uav", "uav", "uav_JoinFederation", joinModules);
            } catch (FederateAlreadyExecutionMember eX1){
                System.out.println("4. Exception FederateAlreadyExecutionMember correctly caught. ");
            }

            Assert.assertTrue(true);
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        } finally {
            killConnectionAndKillRTIG();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////
    ///////////////////////// Functionnalities used ///////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * Lauch the RTIG
     */
    public void executeRTIG() {
        RTIExecutor rtiExecutor = new RTIExecutor();
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
            RtiFactory factory = (RtiFactory) RtiFactoryFactory.getRtiFactory();
            rtia = factory.getRtiAmbassador();
            mya = new MyFederateAmbassador();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     *  Connect this federate to the rtig
     */
    public void connection() {
        try {
            rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Create federation service
     */
    public void createFederation(){
        try {
            File fom = new File("uav.xml");
            rtia.createFederationExecution("uav_JoinFederation", fom.toURI().toURL());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * End correctly the rtia and rtig
     */
    public void killConnectionAndKillRTIG(){
        RTIExecutor rtiExecutor = new RTIExecutor();
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
