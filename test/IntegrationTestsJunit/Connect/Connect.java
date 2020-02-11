package IntegrationTests.Connect;

import IntegrationTests.MyFederateAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import org.junit.Assert;
import org.junit.Test;

public class Connect {

    RTIambassador rtia;
    MyFederateAmbassador mya;

    /**
     *  Test the connection service
     *  Succeed if connection went well
     *  Fail if an error occurs
     */
    @Test
    public void connection() {
        System.out.println("Integration test - Connect");
        executeRTIG();
        try {
            RtiFactory factory = (RtiFactory) RtiFactoryFactory.getRtiFactory();
            rtia = factory.getRtiAmbassador();
            mya = new MyFederateAmbassador();
            rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
            System.out.println("Connexion to the RTIG worked well");
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
