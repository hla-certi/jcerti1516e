package integrationTests.ExecuteRTIG;

import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.exceptions.RTIinternalError;
import integrationTests.JoinFederation.JoinFederation;

import java.util.logging.Logger;


public class ExecuteRTIGTest {

    private final static Logger LOGGER = Logger.getLogger(ExecuteRTIGTest.class.getName());
    public static void main(String[] args) {
        RTIExecutor rtiExecutor = new RTIExecutor();
        try {
            rtiExecutor.executeRTIG();
            LOGGER.info("*********** TEST SUCCEED ************");
        } catch (Exception e){
            LOGGER.info("*********** TEST FAILED ************");
            e.printStackTrace();
        } finally {
            rtiExecutor.killRTIG();
        }
    }

}
