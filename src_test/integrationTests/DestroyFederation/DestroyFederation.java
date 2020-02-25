package integrationTests.DestroyFederation;

import java.util.logging.Logger;

import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.NotConnected;
import integrationTests.MyFederateAmbassador;

public class DestroyFederation {
	CertiRtiAmbassador rtia;
	MyFederateAmbassador mya;
	private final static Logger LOGGER = Logger.getLogger(DestroyFederation.class.getName());
	RTIExecutor rtiExecutor;

	/**
	 * Test the create federation service Succeed if connection went well Fail if an
	 * error occurs
	 */
	public static void main(String[] args) {
		DestroyFederation test = new DestroyFederation();
		try {
			test.executeRTIG();
			test.init();

			// Try to destroy a federation without beeig connected
			try {
				test.rtia.destroyFederationExecution("uav_destroy");
			} catch (NotConnected eX1) {
				LOGGER.info("1. Exception NotConnected correctly caught. ");
			}
			test.connection();
			test.createFederation();
			test.joinFederation();

			// Try to destroy a federation with have a federate connected
			try {
				test.rtia.destroyFederationExecution("uav_destroy");
			} catch (FederatesCurrentlyJoined e) {
				LOGGER.info("2. Exception FederationExecutionDoesNotExist correctly caught.");
			}

			// Resign a federation executio without error
			test.rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
			test.rtia.destroyFederationExecution("uav_destroy");
			LOGGER.info("3. Destroy federation execution worked fine.");

			// Try to destroy a federation that doesn't exist
			try {
				test.rtia.destroyFederationExecution("uav_destroy");
			} catch (FederationExecutionDoesNotExist e) {
				LOGGER.info("4. Exception FederationExecutionDoesNotExist correctly caught.");
			}
			test.rtia.disconnect();
			LOGGER.info("*********** TEST SUCCEED ************");
		} catch (Exception ex) {
			ex.printStackTrace();
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialization
	 */
	public void init() {
		try {
			RtiFactory factory = RtiFactoryFactory.getRtiFactory();
			rtia = (CertiRtiAmbassador) factory.getRtiAmbassador();
			mya = new MyFederateAmbassador();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Connect this federate to the rtig
	 */
	public void connection() throws ConnectionFailed {
		try {
			rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
		} catch (Exception e) {
			if (!RTIExecutor.checkLocalHost())
				throw new ConnectionFailed(
						"Connection to the RTIG failed. You are trying to connect to a RTIG of an other machine, but no RTIG was found.");
			else
				throw new ConnectionFailed("Connection to the RTIG failed. There is probably no RTIG running.");
		}
	}

	/**
	 * Create federation service
	 */
	public void createFederation() {
		try {
			String fom = "uav.xml";
			rtia.createFederationExecution("uav_destroy", fom);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Join Federation service
	 */
	public void joinFederation() {
		try {
			String[] joinModules = { "uav.xml" };
			rtia.joinFederationExecution("uav-send", "uav", "uav_destroy", joinModules);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * End correctly the rtia and rtig
	 */
	public void killConnectionAndKillRTIG() {
		try {
			rtiExecutor.killRTIG();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
