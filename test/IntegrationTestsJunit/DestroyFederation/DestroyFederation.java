package IntegrationTests.DestroyFederation;

import IntegrationTests.MyFederateAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.NotConnected;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;


public class DestroyFederation {
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

            //Try to destroy a federation without beeig connected
            try {
                rtia.destroyFederationExecution("uav_destroy");
            } catch (NotConnected eX1){
                System.out.println("1. Exception NotConnected correctly caught. ");
            }

            connection();
            createFederation();
            joinFederation();

            //Try to destroy a federation with have a federate connected
            try {
                rtia.destroyFederationExecution("uav_destroy");
            } catch(FederatesCurrentlyJoined e){
                System.out.println("2. Exception FederationExecutionDoesNotExist correctly caught.");
            }

            //Resign a federation executio without error
            rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
            rtia.destroyFederationExecution("uav_destroy");
            System.out.println("3. Destroy federation execution worked fine.");


            //Try to destroy a federation that doesn't exist
            try {
                rtia.destroyFederationExecution("uav_destroy");
            } catch (FederationExecutionDoesNotExist e){
                System.out.println("4. Exception FederationExecutionDoesNotExist correctly caught.");
            }
            rtia.disconnect();
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
            rtia.createFederationExecution("uav_destroy", fom.toURI().toURL());
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
            rtia.joinFederationExecution("uav-send", "uav", "uav_destroy", joinModules);
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
            rtia.disconnect();
            rtiExecutor.killRTIG();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
