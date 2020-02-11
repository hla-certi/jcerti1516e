package IntegrationTests.ExecuteRTIG;

import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.exceptions.RTIinternalError;
import org.junit.Assert;
import org.junit.Test;


public class ExecuteRTIGTest {

    @Test
    public void ExecuteRTIG() throws RTIinternalError {
        RTIExecutor rtiExecutor = new RTIExecutor();
        try {
            rtiExecutor.executeRTIG();
            Assert.assertTrue(true);
        } catch (Exception e){
            e.printStackTrace();
            Assert.fail();
        } finally {
            rtiExecutor.killRTIG();
        }
    }

}
