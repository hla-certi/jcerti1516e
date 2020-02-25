package integrationTests.Connect;

import java.util.logging.Logger;

import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.ConnectionFailed;
import integrationTests.MyFederateAmbassador;

public class Connect {

	private final static Logger LOGGER = Logger.getLogger(Connect.class.getName());
	CertiRtiAmbassador rtia;
	MyFederateAmbassador mya;
	RTIExecutor rtiExecutor;

	/**
	 * Test the connection service Succeed if connection went well Fail if an error
	 * occurs
	 */
	public static void main(String[] args) {
		System.out.println("Integration test - Connect");
		Connect connectTest = new Connect();
		connectTest.executeRTIG();
		try {
			RtiFactory factory = RtiFactoryFactory.getRtiFactory();
			connectTest.rtia = (CertiRtiAmbassador) factory.getRtiAmbassador();
			connectTest.mya = new MyFederateAmbassador();
			try {
				connectTest.rtia.connect(connectTest.mya, CallbackModel.HLA_IMMEDIATE);
			} catch (Exception e) {
				if (!RTIExecutor.checkLocalHost())
					throw new ConnectionFailed(
							"Connection to the RTIG failed. You are trying to connect to a RTIG of an other machine, but no RTIG was found.");
				else
					throw new ConnectionFailed("Connection to the RTIG failed. There is probably no RTIG running.");
			}
			LOGGER.info("Connexion to the RTIG worked well");
			LOGGER.info("*********** TEST SUCCEED ************");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("*********** TEST FAILED ************");
		} finally {
			connectTest.killConnectionAndKillRTIG();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * End correctly the rtia and rtig
	 */
	public void killConnectionAndKillRTIG() {
		try {
			rtia.disconnect();
			rtiExecutor.killRTIG();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
