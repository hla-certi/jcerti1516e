package IntegrationTests.ResignFederation;

import IntegrationTests.MyFederateAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;


public class ResignFederation {
    RTIambassador rtia;
    MyFederateAmbassador mya;

    /**
     *  Test the create federation service
     *  Succeed if connection went well
     *  Fail if an error occurs
     */
    @Test
    public void resignFederation(){
        try {
            executeRTIG();
            init();

            //Try to join a federation without being connected
            try {
                rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
            } catch (NotConnected e){
                System.out.println("1. Exception NotConnected correctly caught. ");
            }
            connection();
            createFederation();

            //Try to join a federation without being connected
            try {
                rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
            } catch (FederateNotExecutionMember e){
                System.out.println("2. Trying to resign without joinning the federation - Exception FederateNotExecutionMember correctly caught. ");
            }

            joinFederation();

            //Resign federation execution without error
            rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
            System.out.println("3. Resign federation execution worked fine. ");

            //Try to join a federation with we already resign
            try {
                rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
            } catch (FederateNotExecutionMember e){
                System.out.println("4. Try to resign a federation we already resigned - Exception FederateNotExecutionMember correctly caught. ");
            }

            Assert.assertTrue(true);
        } catch (Exception ex) {
            ex.printStackTrace();
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
            rtia.createFederationExecution("uav_ResignFederation", fom.toURI().toURL());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *  Join Federation service
     */
    public void joinFederation(){
        try {
            URL[] joinModules = new URL[]{
                    (new File("uav.xml")).toURI().toURL()
            };
            rtia.joinFederationExecution("uav", "uav", "uav_ResignFederation", joinModules);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * End correctly the rtia and rtig
     */
    public void killConnectionAndKillRTIG(){
        RTIExecutor rtiExecutor = new RTIExecutor();
        try {
            rtia.destroyFederationExecution("uav_ResignFederation");
            rtia.disconnect();
            rtiExecutor.killRTIG();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
