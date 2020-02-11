package IntegrationTests.CreateFederation;
import IntegrationTests.MyFederateAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.NotConnected;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;


public class CreateFederation {
    RTIambassador rtia;
    MyFederateAmbassador mya;

    /**
     *  Test the create federation service
     *  Succeed if connection went well
     *  Fail if an error occurs
     */
    @Test
    public void createFederation(){
        try {
            executeRTIG();
            init();
//            //Try to create a federation before being connected to the rtig
            try {
                File fom = new File("uav.xml");
                System.out.println(fom.toURI().toURL());
                rtia.createFederationExecution("uav_CreateFederation", fom.toURI().toURL());
            } catch (NotConnected eX1){
                System.out.println("1. Exception NotConnected correctly caught. ");
            }
            connection();
            //Create a federation without error
            File fom = new File("uav.xml");
            rtia.createFederationExecution("uav_CreateFederation", fom.toURI().toURL());
            System.out.println("2. Federation creation worked fine.");

            try {
                //Try to create a federation with the same name that a existing federation
                rtia.createFederationExecution("uav_CreateFederation", fom.toURI().toURL());
            } catch (FederationExecutionAlreadyExists eX1){
                System.out.println("3. Exception FederationExecutionAlreadyExists correctly caught.");
            }

            try {
                //Try to create a federation with a bad FOM
                File errorFom = new File("error.xml");
                rtia.createFederationExecution("uav_CreateFederation_fomError", errorFom.toURI().toURL());
            } catch (CouldNotOpenFDD eX2){
                System.out.println("4. Exception CouldNotOpenFDD correctly caught");
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
     * End correctly the rtia and rtig
     */
    public void killConnectionAndKillRTIG(){
        RTIExecutor rtiExecutor = new RTIExecutor();
        try{
            rtia.destroyFederationExecution("uav_CreateFederation");
            // No need to destroy federation "uav_CreateFederation_fomError", not created beaucause of the CouldNotOpenFDD exception
            rtia.disconnect();
            rtiExecutor.killRTIG();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
