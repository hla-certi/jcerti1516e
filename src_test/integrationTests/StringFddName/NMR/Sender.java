package integrationTests.StringFddName.NMR;

import java.io.IOException;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateIsExecutionMember;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.HLAASCIIstringImpl;

/**
 * Class Sender witch create a new specific FederateAmbassador And manage
 * connection to a federation to send values
 */
public class Sender {

	private static final double BLOCKING_TIME = 0.1;
	private CertiRtiAmbassador rtia_send;
	private SenderFederateAmbassador mya_send;
	private boolean senderIsCreator;
	private String fddName = "uav.xml";

	// Set times values
	private double updateTimeNMRSender; // Set with parameter expectedTime, to check if the time is correct at test end
										// (in Receiver)
	private final double timeStepNMRsender = 10.0;
	private final double lookaheadSender = 0.1;

	// FIXME: for the clarity of the code:
	// - this Sender federate has the parameter updateTime, that must be used in its
	// code
	// - it will send this value that will be considered as a "expected" value for
	// the Receiver
	// But be carefull with the names:
	// - the expected value RAV(t') for the Receiver corresponds to the uav(t') made
	// by the Sender, t'=localtime + updateTime

	/**
	 * Function called by the test to create a new federate and listen a federation
	 * to receive and check values
	 * 
	 * @param expectedFloat  : float value expected to received by the RAV
	 * @param expectedString : String value expected to received by the RAV
	 * @param updateTime     : updateTime for the federate sender. This time is set
	 *                       here because it used to made the UAV and we need to
	 *                       know UAV time to check if RAV time if correct
	 * @return true if everything work fine, false if not. This result is used to
	 *         defined test result
	 */
	public boolean send(float expectedFloat, String expectedString, LogicalTime updateTime)
			throws IOException, InterruptedException {

		updateTimeNMRSender = ((CertiLogicalTime1516E) updateTime).getTime();

		// Initialize values
		RtiFactory factory = null;
		try {
			factory = RtiFactoryFactory.getRtiFactory();
		} catch (RTIinternalError rtIinternalError) {
			rtIinternalError.printStackTrace();
			return false;
		}
		try {
			rtia_send = (CertiRtiAmbassador) factory.getRtiAmbassador();
		} catch (RTIinternalError rtIinternalError) {
			rtIinternalError.printStackTrace();
			return false;

		}
		mya_send = new SenderFederateAmbassador();

		// Connect to the RTIG
		SendandReceiveValues_NMR.LOGGER.info("Sender ------- 1. Get a link to the RTI");
		try {
			rtia_send.connect(mya_send, CallbackModel.HLA_IMMEDIATE);
		} catch (Exception e) {
			if (!RTIExecutor.checkLocalHost())
				SendandReceiveValues_NMR.LOGGER.severe(
						"Connection to the RTIG failed. You are trying to connect to a RTIG of an other machine, but no RTIG was found.");
			else
				SendandReceiveValues_NMR.LOGGER
						.severe("Connection to the RTIG failed. There is probably no RTIG running.");
			return false;
		}

		// Create Federation
		// File fom = new File("uav.xml");
		String federationExecutionName = "testNMR2";
		try {
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- 2. Create federation- " + federationExecutionName);
			rtia_send.createFederationExecution(federationExecutionName, fddName);
			senderIsCreator = true;
		} catch (FederationExecutionAlreadyExists ex) {
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- Can't create federation. It already exists.");
			senderIsCreator = false;
		} catch (ErrorReadingFDD | CouldNotOpenFDD | NotConnected | RTIinternalError ex) {
			return false;
		}

		// Join Federation
		String[] joinModules = { "uav.xml" };
		String federateName = "fed-send";
		String federateType = "uav";
		try {
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- 3. Join federation");
			rtia_send.joinFederationExecution(federateName, federateType, federationExecutionName, joinModules);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		mya_send.isCreator = senderIsCreator;

		// Initialize Federate Ambassador
		try {
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- 4. Initialize Federate Ambassador");
			// Because UAV is made with time t = updateTime
			mya_send.initialize(rtia_send, timeStepNMRsender, updateTimeNMRSender, lookaheadSender);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if (mya_send.isCreator) {
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- 5.1 Wait Receiver in Synchronization Point");
			while (SendandReceiveValues_NMR.syncroPoint.get() == false) {
				// Wait Receiver in Synchronization Point
				Thread.sleep(100);
			}
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- 5.2 Synchronization Point achieved in receiver");
			// System.in.read();

			HLAASCIIstring s = new HLAASCIIstringImpl("InitSync");
			byte[] tagsyns = new byte[s.getEncodedLength()];
			ByteWrapper bw = new ByteWrapper(tagsyns);
			s.encode(bw);

			try {
				rtia_send.registerFederationSynchronizationPoint(mya_send.synchronizationPointName, tagsyns);
			} catch (Exception e) {
				e.printStackTrace();
				return false;

			}

			// Wait synchronization point succeeded callback
			while (!mya_send.synchronizationSuccess && !mya_send.synchronizationFailed) {
				try {
					rtia_send.evokeCallback(BLOCKING_TIME);
				} catch (Exception e) {
					e.printStackTrace();
					return false;

				}
			}
		} else {
			SendandReceiveValues_NMR.syncroPoint.set(true);
		}

		// Wait synchronization point announcement (announceSynchronizationPoint
		// callback)
		while (!mya_send.inPause) {
			try {
				rtia_send.evokeCallback(BLOCKING_TIME);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		// Informs the RTIG it is aware of the synchronization point
		// "synchronizationPointName"
		try {
			rtia_send.synchronizationPointAchieved(mya_send.synchronizationPointName);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		// Wait the callback federationSynchronized()
		while (mya_send.inPause) {
			try {
				rtia_send.evokeCallback(BLOCKING_TIME);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		SendandReceiveValues_NMR.LOGGER.info("Sender ------- 6. UAV Loop");
//        int i = 1; //0 with stopTime  4
//        while (i-- > 0) {
		// Text attribute
		HLAASCIIstring text = new HLAASCIIstringImpl(expectedString);
		byte[] textAttribute = new byte[text.getEncodedLength()];
		ByteWrapper textWrapper = new ByteWrapper(textAttribute);
		text.encode(textWrapper);

		// Float attribute
		HLAfloat32BE value = new BasicHLAfloat32BEImpl(expectedFloat);
		byte[] fomAttribute = new byte[value.getEncodedLength()];
		ByteWrapper fomWrapper = new ByteWrapper(fomAttribute);
		value.encode(fomWrapper);

		AttributeHandleValueMap attr = null;
		try {
			attr = rtia_send.getAttributeHandleValueMapFactory().create(2);
		} catch (FederateNotExecutionMember federateNotExecutionMember) {
			federateNotExecutionMember.printStackTrace();
			return false;
		} catch (NotConnected notConnected) {
			notConnected.printStackTrace();
			return false;
		}

		attr.put(mya_send.textAttributeHandle, textAttribute);
		attr.put(mya_send.fomAttributeHandle, fomAttribute);

		// Tag
		HLAASCIIstring tag = new HLAASCIIstringImpl("update");
		byte[] tagBuffer = new byte[tag.getEncodedLength()];
		ByteWrapper tagWrapper = new ByteWrapper(tagBuffer);
		text.encode(tagWrapper);

		// The UAV must be executed outside an advance time loop. The
		// timestamp 'updateTime' is updated when a TAG is received.
		SendandReceiveValues_NMR.LOGGER
				.info("Sender ------- 6.1 UAV with time = " + ((CertiLogicalTime1516E) mya_send.updateTime).getTime());
		try {
			rtia_send.updateAttributeValues(mya_send.myObject, attr, tagBuffer, mya_send.updateTime);
		} catch (Exception e) {
			SendandReceiveValues_NMR.LOGGER
					.info("Sender ------- Timestamp must be bigger than (localHlaTime + lookahead)");
			return false;
		}
		// The federate ask to advance to (current logical time + timeStep)
		SendandReceiveValues_NMR.LOGGER
				.info("Sender ------- 6.2 NMR with time = " + ((CertiLogicalTime1516E) mya_send.timeAdvance).getTime());
		try {
			rtia_send.nextMessageRequest(mya_send.timeAdvance);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		while (!mya_send.timeAdvanceGranted) {
			try {
				rtia_send.evokeCallback(BLOCKING_TIME);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		mya_send.timeAdvanceGranted = false;
		Thread.sleep(1000);
//        }
		SendandReceiveValues_NMR.LOGGER.info("Sender ------- 7. Resign federation execution");
		try {
			rtia_send.resignFederationExecution(ResignAction.DELETE_OBJECTS);
		} catch (InvalidResignAction invalidResignAction) {
			invalidResignAction.printStackTrace();
		} catch (OwnershipAcquisitionPending ownershipAcquisitionPending) {
			ownershipAcquisitionPending.printStackTrace();
		} catch (FederateOwnsAttributes federateOwnsAttributes) {
			federateOwnsAttributes.printStackTrace();
		} catch (FederateNotExecutionMember federateNotExecutionMember) {
			federateNotExecutionMember.printStackTrace();
		} catch (NotConnected notConnected) {
			notConnected.printStackTrace();
		} catch (CallNotAllowedFromWithinCallback callNotAllowedFromWithinCallback) {
			callNotAllowedFromWithinCallback.printStackTrace();
		} catch (RTIinternalError rtIinternalError) {
			rtIinternalError.printStackTrace();
		}

		SendandReceiveValues_NMR.LOGGER.info("Sender ------- 8. Destroy federation execution - nofail");
		// Uses a loop for destroying the federation (with a delay if
		// there are other federates that did not resign yet).
		boolean federationIsActive = true;
		try {
			while (federationIsActive) {
				try {
					rtia_send.destroyFederationExecution(federationExecutionName);
					federationIsActive = false;
					SendandReceiveValues_NMR.LOGGER.warning("Sender ------- Federation destroyed by this federate");
				} catch (FederationExecutionDoesNotExist dne) {
					SendandReceiveValues_NMR.LOGGER.warning(
							"Sender ------- Federation execution doesn't exist.  May be the Federation was destroyed by some other federate.");
					federationIsActive = false;
				} catch (FederatesCurrentlyJoined e) {
					SendandReceiveValues_NMR.LOGGER.info("Sender ------- Couldn't destroy federation");
				} catch (RTIinternalError e) {
					SendandReceiveValues_NMR.LOGGER.severe("Sender ------- RTIinternalError: " + e.getMessage());
					e.printStackTrace();
				} catch (NotConnected notConnected) {
					notConnected.printStackTrace();
				}
			}
		} finally {
			SendandReceiveValues_NMR.LOGGER.info("Sender ------- 9. Disconect from the rti");
			try {
				rtia_send.disconnect();
			} catch (FederateIsExecutionMember federateIsExecutionMember) {
				federateIsExecutionMember.printStackTrace();
				return false;
			} catch (CallNotAllowedFromWithinCallback callNotAllowedFromWithinCallback) {
				callNotAllowedFromWithinCallback.printStackTrace();
				return false;
			} catch (RTIinternalError rtIinternalError) {
				rtIinternalError.printStackTrace();
				return false;
			}
		}
		return true;

	}

}
