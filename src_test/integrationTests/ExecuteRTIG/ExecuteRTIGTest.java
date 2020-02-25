package integrationTests.ExecuteRTIG;

import java.util.logging.Logger;

import certi.rti1516e.impl.RTIExecutor;

public class ExecuteRTIGTest {

	private final static Logger LOGGER = Logger.getLogger(ExecuteRTIGTest.class.getName());

	public static void main(String[] args) {
		RTIExecutor rtiExecutor = new RTIExecutor();
		try {
			rtiExecutor.executeRTIG();
			LOGGER.info("*********** TEST SUCCEED ************");
		} catch (Exception e) {
			LOGGER.info("*********** TEST FAILED ************");
			e.printStackTrace();
		} finally {
			rtiExecutor.killRTIG();
		}
	}

}
