package integrationTests.StringFddName;

import java.util.logging.Logger;

import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.NotConnected;
import integrationTests.MyFederateAmbassador;

public class CreateFederation {
	CertiRtiAmbassador rtia;
	MyFederateAmbassador mya;
	private final static Logger LOGGER = Logger.getLogger(CreateFederation.class.getName());
	RTIExecutor rtiExecutor;

	/**
	 * Test the create federation service Succeed if connection went well Fail if an
	 * error occurs
	 */
	public static void main(String[] args) {
		CreateFederation test = new CreateFederation();
		try {
			test.executeRTIG();
			test.init();
			String fddName = "uav.xml";
			// Try to create a federation before being connected to the rtig
			try {
				test.rtia.createFederationExecution("uav_CreateFederation", fddName);
			} catch (NotConnected eX1) {
				LOGGER.info("1. Exception NotConnected correctly caught. ");
				// LOGGER.log(Level.SEVERE, "Exception:", eX1);
			}
			test.connection();
			// Create a federation without error
			test.rtia.createFederationExecution("uav_CreateFederation", fddName);
			LOGGER.info("2. Federation creation worked fine.");

			try {
				// Try to create a federation with the same name that a existing federation
				test.rtia.createFederationExecution("uav_CreateFederation", fddName);
			} catch (FederationExecutionAlreadyExists eX1) {
				LOGGER.info("3. Exception FederationExecutionAlreadyExists correctly caught.");
			}

			try {
				// Try to create a federation with a bad FOM
				String fomName_error = "error.xml";
				test.rtia.createFederationExecution("uav_CreateFederation_fomError", fomName_error);
			} catch (CouldNotOpenFDD eX2) {
				LOGGER.info("4. Exception CouldNotOpenFDD correctly caught");
			}
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
	public void connection() {
		try {
			rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * End correctly the rtia and rtig
	 */
	public void killConnectionAndKillRTIG() {
		try {
			rtia.destroyFederationExecution("uav_CreateFederation");
			// No need to destroy federation "uav_CreateFederation_fomError", not created
			// because of the CouldNotOpenFDD exception
			rtia.disconnect();
			rtiExecutor.killRTIG();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
