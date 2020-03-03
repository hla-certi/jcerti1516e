package integrationTests.UrlFddName;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.NotConnected;
import integrationTests.MyFederateAmbassador;

public class ResignFederation {
	CertiRtiAmbassador rtia;
	MyFederateAmbassador mya;
	private final static Logger LOGGER = Logger.getLogger(ResignFederation.class.getName());
	RTIExecutor rtiExecutor;

	/**
	 * Test the create federation service Succeed if connection went well Fail if an
	 * error occurs
	 */
	public static void main(String[] args) {
		ResignFederation test = new ResignFederation();
		try {
			test.executeRTIG();
			test.init();

			// Try to join a federation without being connected
			try {
				test.rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
			} catch (NotConnected e) {
				LOGGER.info("1. Exception NotConnected correctly caught. ");
			}
			test.connection();
			test.createFederation();

			// Try to join a federation without being connected
			try {
				test.rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
			} catch (FederateNotExecutionMember e) {
				LOGGER.info(
						"2. Trying to resign without joinning the federation - Exception FederateNotExecutionMember correctly caught. ");
			}

			test.joinFederation();

			// Resign federation execution without error
			test.rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
			LOGGER.info("3. Resign federation execution worked fine. ");

			// Try to join a federation with we already resign
			try {
				test.rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS);
			} catch (FederateNotExecutionMember e) {
				LOGGER.info(
						"4. Try to resign a federation we already resigned - Exception FederateNotExecutionMember correctly caught. ");
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
			File fdd = new File("uav.xml");
			rtia.createFederationExecution("uav_ResignFederation", fdd.toURI().toURL());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Join Federation service
	 */
	public void joinFederation() {
		try {
			URL[] joinModule = {(new File("uav.xml")).toURI().toURL()};
			rtia.joinFederationExecution("uav", "uav", "uav_ResignFederation", joinModule);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * End correctly the rtia and rtig
	 */
	public void killConnectionAndKillRTIG() {
		try {
			rtia.destroyFederationExecution("uav_ResignFederation");
			rtia.disconnect();
			rtiExecutor.killRTIG();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
