package certi1516e.Flex;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiLogicalTimeInterval1516E;
import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
//import certi1516e.UavSendString.MyFederateAmbassador;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.CallbackModel;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.OrderType;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.ResignAction;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.RtiFactoryFactory;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.TransportationTypeHandle;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.AlreadyConnected;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyEnabled;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.CallNotAllowedFromWithinCallback;
import hla.rti1516e.exceptions.ConnectionFailed;
import hla.rti1516e.exceptions.CouldNotCreateLogicalTimeFactory;
import hla.rti1516e.exceptions.CouldNotOpenFDD;
import hla.rti1516e.exceptions.ErrorReadingFDD;
import hla.rti1516e.exceptions.FederateAlreadyExecutionMember;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateNameAlreadyInUse;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.FederateOwnsAttributes;
import hla.rti1516e.exceptions.FederatesCurrentlyJoined;
import hla.rti1516e.exceptions.FederationExecutionAlreadyExists;
import hla.rti1516e.exceptions.FederationExecutionDoesNotExist;
import hla.rti1516e.exceptions.InconsistentFDD;
import hla.rti1516e.exceptions.InvalidLocalSettingsDesignator;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.InvalidResignAction;
import hla.rti1516e.exceptions.LogicalTimeAlreadyPassed;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.ObjectInstanceNotKnown;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.exceptions.SynchronizationPointLabelNotAnnounced;
import hla.rti1516e.exceptions.UnsupportedCallbackModel;
import hla.rti1516e.impl.CertiAttributeHandleSet;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.HLAASCIIstringImpl;
import hla.rti1516e.jlc.NullFederateAmbassador;

///////////////////////
// FlexUAV-RECEIVEstring //
///////////////////////

/**
 * This class implements a HLA federate. It is based on the JCERTI demo
 * compliant to HLA 1.3. It extends that class for be compliant with HLA
 * 1516-2010 Extended (HLA 1516e), and introduces some parameters. If there is
 * no RTIG running, it launches the RTIG (if launched first), otherwise it uses
 * the existent one (on the machine specified by $CERTI_HOST. It creates a
 * federation called federationExecutionName (if launched first), then joins
 * this federation, advances its logical time with other federates and updates
 * attributes of an instance of a class.
 * </p>
 * <p>
 * The synchronization point 'InitSync' is registered by the first launched
 * federate. After launching the second federate, the user must press 'Enter' in
 * the terminal where the first one was launched. The first federate then sends
 * synchronizationPointAchieved() and the RTI provides the callback
 * federationSynchronized() for both federates.
 * </p>
 * <p>
 * This federate is called by the following command line, e.g.: ant
 * -DtimeStep=20 -DupdateTime=5 -Dlookahead=1 UAVSend1516eString-run The
 * parameter DupdateTime is not used in this federate.
 * <ul>
 * <li>lookahead: according to HLA, the federate promises it will not send any
 * message in the interval (h, h+lookahead), where 'h' is the current logical
 * time.
 * <li>timeStep: the federate will ask to advance its time by steps with this
 * value, TAR(h + timeStep), 'h' is the current logical time.
 * <li>updateTime: the federate will update attributes with timestamp t = (h +
 * updateTime), 'h' is the current logical time. For a correct execution,
 * updateTime > lookahead. Otherwise the exception INVALID_FEDERATION_TIME is
 * raised.
 * </ul>
 * </p>
 * <p>
 * The time advance phase in HLA is a two-step process: 1) a federate sends a
 * time advance request service and 2) waits for the time to be granted by the
 * timeAdvanceGrant (TAG) service. Federates using TAR are called time- stepped
 * federates. If TAR is being used, the time returned always equals the asked
 * time h'.
 */

public class UavReceive {

	private final static Logger LOGGER = Logger.getLogger(UavReceive.class.getName());
	// private static final double stopTime = 50; //If used in "RAV loop"

	// If it is the first launched federate it launches the RTIG, otherwise it
	// connects to
	// the running one (according to $CERTI_HOST).
	RTIExecutor rtiExecutor;

	/** The sync point all federates will sync up on before starting */
	public static final String READY_TO_RUN = "ReadyToRun";

	private AttributeHandle textAttributeHandle;
	private AttributeHandle fomAttributeHandle;

	/**
	 * Run a federate since its creation to its destruction Reflects values of two
	 * attributes (float and string) from a federation
	 */
	public void runFederate(double timeStepArg, double updateTimeArg, double lookaheadArg)
			throws RTIinternalError, ConnectionFailed, InvalidLocalSettingsDesignator, UnsupportedCallbackModel,
			AlreadyConnected, CallNotAllowedFromWithinCallback, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD,
			NotConnected, IOException, CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse,
			FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress, FederateAlreadyExecutionMember,
			NameNotFound, FederateNotExecutionMember, ObjectClassNotDefined, AttributeNotDefined,
			OwnershipAcquisitionPending, ObjectClassNotPublished, InvalidObjectClassHandle, ObjectInstanceNameInUse,
			ObjectInstanceNameNotReserved, InvalidResignAction, FederateOwnsAttributes {

		LOGGER.info("        UAV-RECEIVEstring");

		LOGGER.info("     0. Launches the RTI if no one is running and CERTI_HOST=localhost");
		executeRTIG();

		LOGGER.info("     1. Get a link to the RTI");

		RtiFactory factory = RtiFactoryFactory.getRtiFactory();
		CertiRtiAmbassador rtia = (CertiRtiAmbassador) factory.getRtiAmbassador();
		MyFederateAmbassador mya = new MyFederateAmbassador();

		try {
			rtia.connect(mya, CallbackModel.HLA_IMMEDIATE);
		} catch (Exception e) {
			if (!(RTIExecutor.checkLocalHost()))
				throw new ConnectionFailed(
						"Connection to the RTIG failed. You are trying to connect to a RTIG on a remote machine, but no RTIG was found.");
			else
				throw new ConnectionFailed("Connection to the RTIG failed. There is probably no RTIG running.");
		}

		boolean flagCreator;
		String federationExecutionName = "uav";
		String federateName = "uav-receiveString";
		String federateType = "uav";

		System.out.println();
		LOGGER.info(
				"     2. Federate " + federateName + " creates federation " + federationExecutionName + " - nofail");

		// The first launched federate creates the federation execution
		try {
			String fomName = "uav.xml";
			rtia.createFederationExecution(federationExecutionName, fomName);
			flagCreator = true;
		} catch (FederationExecutionAlreadyExists ex) {
			LOGGER.warning("Can't create federation. It already exists.");
			flagCreator = false;
		}
		System.out.println();

		LOGGER.info("     3. Federate " + federateName + " joins federation " + federationExecutionName);
		// FIXME Does not work with joinModules
		// rtia.joinFederationExecution(federateName, federateType,
		// federationExecutionName, joinModules);
		rtia.joinFederationExecution(federateName, federateType, federationExecutionName);
		// rtia_receive.joinFederationExecution(federateName, federateType,
		// federationExecutionName);

		mya.isCreator = flagCreator;
		System.out.println();
		LOGGER.info("     4. Initialize Federate Ambassador for federate " + federateName);
		mya.initialize(rtia, timeStepArg, updateTimeArg, lookaheadArg);
		System.out.println();

		// The first launched federate also registers the synchronization point.
		// It waits the user launches the second federate, come back and press
		// 'Enter'.
		if (mya.isCreator) {
			LOGGER.info(
					"     5 After launch the other federate, press 'Enter' so this federate can register the Synchronization Point ");
			System.in.read(); // This method blocks until input data is available.

			HLAASCIIstring s = new HLAASCIIstringImpl(mya.synchronizationPointName);

			byte[] tagsyns = new byte[s.getEncodedLength()];
			ByteWrapper bw = new ByteWrapper(tagsyns);
			s.encode(bw);
			rtia.registerFederationSynchronizationPoint(mya.synchronizationPointName, tagsyns);

			// Wait synchronization point succeeded callback
			while (!mya.synchronizationSuccess && !mya.synchronizationFailed) {
				rtia.evokeCallback(BLOCKING_TIME);
			}
		} else {
			LOGGER.info("     5.1  Waiting for the creator of the Federation to register the Sync Point ");
		}

		// Wait synchronization point announcement (announceSynchronizationPoint
		// callback)
		while (!mya.inPause) {
			rtia.evokeCallback(BLOCKING_TIME);
		}

		// Informs the RTIG it is aware of the synchronization point
		// "synchronizationPointName"
		try {
			rtia.synchronizationPointAchieved(mya.synchronizationPointName);
		} catch (SynchronizationPointLabelNotAnnounced synchronizationPointLabelNotAnnounced) {
			synchronizationPointLabelNotAnnounced.printStackTrace();
		}

		// Wait the callback federationSynchronized()
		while (mya.inPause) {
			rtia.evokeCallback(BLOCKING_TIME);
		}

		LOGGER.info("     6 RAV Loop");
		// According to the relation between the timeStep of both
		// federates, UAVReceive can have a lot of "FEDERATES_CURRENTLY_JOINED"
		// messages, before the federation is destroyed by UavSend. The results
		// are almost the same using while(i-- > 0) or while (... < stopTime).
		// For example, for UavSend-timeStep=10 and UavReceive-timeStep=X, if
		// X>14, there are a few messages. But there are hundred of messages if X<13.
		// FIXME: may be this can be minimized.

		// The federate ask to advance to (current logical time + timeStep).
		// The RAV are received in the TAR-TAG loop.

		int i = 5; // 3
		while (i-- > 0) {
			// while (((CertiLogicalTime1516E) mya.timeAdvance).getTime() < stopTime) {
			LOGGER.info("     6.1 TAR with time=" + ((CertiLogicalTime1516E) mya.timeAdvance).getTime());
			try {
				rtia.timeAdvanceRequest(mya.timeAdvance);
				while (!mya.timeAdvanceGranted) {
					LOGGER.info(" TAR evokecallback");
					try {
						rtia.evokeCallback(BLOCKING_TIME);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				mya.timeAdvanceGranted = false;
			} catch (LogicalTimeAlreadyPassed logicalTimeAlreadyPassed) {
				logicalTimeAlreadyPassed.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// FIXME: need to unsubscribeObjectClass to and unpublishObjectClass HLA?
		// Or DELETE_OBJECTS_THEN_DIVEST do also this?

		System.out.println();

		LOGGER.info("     7 Resign federation execution");
		// FIXME do we need DELETE_OBJECTS_THEN_DIVEST or DELETE_OBJECTS
		rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS_THEN_DIVEST);

		// Any federate can destroy the federation if there is no other
		// joined federate.

		LOGGER.info("     8 Try to destroy federation execution - nofail");
		// Uses a loop with for destroying the federation (with a delay if
		// there are other federates that did not resign yet).
		boolean federationIsActive = true;
		try {
			while (federationIsActive) {
				try {
					rtia.destroyFederationExecution(federationExecutionName);
					federationIsActive = false;
					LOGGER.warning("Federation destroyed by this federate");
					try {
						// Give the other federates a chance to finish.
						Thread.sleep(2000l);
					} catch (InterruptedException e1) {
						// Ignore.
					}
				} catch (FederationExecutionDoesNotExist ex) {
					LOGGER.warning("Federation execution does not exists;"
							+ "May be the Federation was destroyed by some other federate.");
					federationIsActive = false;
				} catch (RTIinternalError e) {
					System.out.println("RTIinternalError: " + e.getMessage());
				} catch (FederatesCurrentlyJoined e) {
					LOGGER.warning(
							"Federates currently joined - can't destroy the execution. Wait some time and try again to destroy the federation.");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e2) {
						//Just ignore sleeping if there is a problem
					}
				}
			}
		} finally {
			LOGGER.info("    9.1 Disconnect from the rti.");
			try {
				// FIXME may be put a catch for disconnect?
				rtia.disconnect();
				// rtiExecutor.killRTIG();
				if (mya.isCreator) {
					LOGGER.info("   9.2 Kill the RTIG (isCreator=" + mya.isCreator
							+ ") only if RTIG was not launched before by hand on localhost or remote.");
					Thread.sleep(1); // Let time to other federate to disconnect from the rti
					rtiExecutor.killRTIG();
				} else {
					LOGGER.info("  9.2. Cannot kill the RTIG (isCreator=" + mya.isCreator + ").");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
//            try {
//                LOGGER.info("     9 Disconnect from the rti");
//                rtia.disconnect();
//            } catch (FederateIsExecutionMember federateIsExecutionMember) {
//                LOGGER.info( "Disconnecting failed" );
//                federateIsExecutionMember.printStackTrace();
//            } 

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

	public static void main(String[] args) throws Exception {
		try {
			double timeStepArg = Double.valueOf(args[2]);
			double updateTimeArg = Double.valueOf(args[3]);
			double lookahead = Double.valueOf(args[4]);
			new UavReceive().runFederate(timeStepArg, updateTimeArg, lookahead);
		} catch (NumberFormatException | ArrayIndexOutOfBoundsException exception) {
			// Default values
			new UavReceive().runFederate(10.0, 0.2, 0.1);
		}
	}

	/**
	 * Implementation of a FederateAmbassador
	 */

	private static double BLOCKING_TIME = 0.1;

	private class MyFederateAmbassador extends NullFederateAmbassador {

		public LogicalTime localHlaTime;
		public LogicalTimeInterval lookahead;
		public LogicalTime timeStep;
		public LogicalTime timeAdvance;
		public LogicalTime updateTime;
		public double updateTime1;
		public boolean timeAdvanceGranted;
		public boolean synchronizationSuccess;
		public boolean synchronizationFailed;
		public boolean inPause;
		public boolean isCreator;
		private String synchronizationPointName = "InitSync";
		private RTIambassador rtia;
		private AttributeHandleSet attributes;

		/**
		 * Initialization of all attributs, publish and register objects Enable time
		 * regulation
		 * 
		 * @param rtia : RTI ambassador
		 */
		public void initialize(RTIambassador rtia, double timeStepArg, double updateTimeArg, double lookaheadArg)
				throws NameNotFound, FederateNotExecutionMember, RTIinternalError, ObjectClassNotDefined,
				AttributeNotDefined, OwnershipAcquisitionPending, SaveInProgress, RestoreInProgress,
				// ConcurrentAccessAttempted,
				ObjectClassNotPublished, NotConnected, InvalidObjectClassHandle, ObjectInstanceNameInUse,
				ObjectInstanceNameNotReserved
		// ObjectAlreadyRegistered
		{
			this.rtia = rtia;
			try {
				rtia.enableAsynchronousDelivery();
			} catch (AsynchronousDeliveryAlreadyEnabled asynchronousDeliveryAlreadyEnabled) {
				asynchronousDeliveryAlreadyEnabled.printStackTrace();
			}

			ObjectClassHandle classHandle = rtia.getObjectClassHandle("SampleClass");

			textAttributeHandle = rtia.getAttributeHandle(classHandle, "TextAttribute");
			fomAttributeHandle = rtia.getAttributeHandle(classHandle, "FOMAttribute");

			attributes = new CertiAttributeHandleSet();
			attributes.add(textAttributeHandle);
			attributes.add(fomAttributeHandle);

			rtia.subscribeObjectClassAttributes(classHandle, attributes);

			localHlaTime = new CertiLogicalTime1516E(0.0);
			lookahead = new CertiLogicalTimeInterval1516E(lookaheadArg);

			updateTime1 = updateTimeArg;
			timeStep = new CertiLogicalTime1516E(timeStepArg);
			updateTime = new CertiLogicalTime1516E(updateTimeArg);

			// The time is advanced by adding localHlaTime + timeStep; starts with
			// (0.0+timeStepArg)
			// timeAdvance = new CertiLogicalTime1516E(timeStepArg);
			timeAdvance = new CertiLogicalTime1516E(((CertiLogicalTime1516E) timeStep).getTime());

			timeAdvanceGranted = false;

			try {
				rtia.enableTimeRegulation(lookahead);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				rtia.enableTimeConstrained();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		/**
		 * The Announce Synchronization Point † service shall inform a joined federate
		 * of the existence of a new synchronization point label. When a synchronization
		 * point label has been registered with the Register Federation Synchronization
		 * Point service, the RTI shall invoke the Announce Synchronization Point †
		 * service, either at all the joined federates in the federation execution or at
		 * the specified set of joined federates, to inform them of the label existence.
		 * The joined federates informed of the existence of a given synchronization
		 * point label via the Announce Synchronization Point † service shall form the
		 * synchronization set for that point. If the optional set of joined federate
		 * designators was null or not provided when the synchronization point label was
		 * registered, the RTI shall also invoke the Announce Synchronization Point †
		 * service at all federates that join the federation execution after the
		 * synchronization label was registered but before the RTI has ascertained that
		 * all joined federates that were informed of the synchronization label
		 * existence have invoked the Synchronization Point Achieved service. These
		 * newly joining federates shall also become part of the synchronization set for
		 * that point. Joined federates that resign from the federation execution after
		 * the announcement of a synchronization point but before the federation
		 * synchronizes at that point shall be removed from the synchronization set. The
		 * user-supplied tag supplied by the Announce Synchronization Point † service
		 * shall be the tag that was supplied to the corresponding Register Federation
		 * Synchronization Point service invocation.
		 * 
		 * @param synchronizationPointLabel : Synchronization point label.
		 * @param userSuppliedTag           : User-supplied tag.
		 */
		@Override
		public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
				throws FederateInternalError {
			inPause = true;
		}

		/**
		 * The Federation Synchronized † service shall inform the joined federate that
		 * all joined federates in the synchronization set of the specified
		 * synchronization point have invoked the Synchronization Point Achieved service
		 * for that point. This service shall be invoked at all joined federates that
		 * are in the synchronization set for that point to indicate that the joined
		 * federates in the synchronization set have synchronized at that point. Once
		 * the synchronization set for a point synchronizes (i.e., the Federation
		 * Synchronized † service has been invoked at all joined federates in the set),
		 * that point shall no longer be registered, and the synchronization set for
		 * that point shall no longer exist. The set of joined federate designators
		 * indicates which federates in the synchronization set invoked the
		 * Synchronization Point Achieved service with an indication of an unsuccessful
		 * synchronization (via the synchronization-success indicator).
		 * 
		 * @param synchronizationPointLabel : Synchronization point label.
		 * @param failedToSyncSet           : Set of joined federate designators.
		 */
		@Override
		public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet)
				throws FederateInternalError {
			inPause = false;
		}

		/**
		 * The Confirm Synchronization Point Registration † service shall indicate to
		 * the requesting joined federate the status of a requested federation
		 * synchronization point registration. This service shall be invoked in response
		 * to a Register Federation Synchronization Point service invocation. A
		 * registration-success indicator argument indicating success shall mean the
		 * label has been successfully registered. If the registration-success indicator
		 * argument indicates failure, the failure reason argument shall be provided to
		 * identify the reason that the synchronization point registration failed.
		 * Possible reasons for the synchronization point registration failure are the
		 * following: — The specified label is already in use. — A synchronization set
		 * member is not a joined federate. A registration attempt that ends with a
		 * negative success indicator shall have no other effect on the federation
		 * execution.
		 * 
		 * @param synchronizationPointLabel : Synchronization point label.
		 */
		@Override
		public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
				throws FederateInternalError {
			synchronizationSuccess = true;
		}

		/**
		 * The Confirm Synchronization Point Registration † service shall indicate to
		 * the requesting joined federate the status of a requested federation
		 * synchronization point registration. This service shall be invoked in response
		 * to a Register Federation Synchronization Point service invocation. A
		 * registration-success indicator argument indicating success shall mean the
		 * label has been successfully registered. If the registration-success indicator
		 * argument indicates failure, the failure reason argument shall be provided to
		 * identify the reason that the synchronization point registration failed.
		 * Possible reasons for the synchronization point registration failure are the
		 * following: — The specified label is already in use. — A synchronization set
		 * member is not a joined federate. A registration attempt that ends with a
		 * negative success indicator shall have no other effect on the federation
		 * execution.
		 * 
		 * @param synchronizationPointLabel : Synchronization point label.
		 * @param reason                    : Optional failure reason.
		 */
		@Override
		public void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
				SynchronizationPointFailureReason reason) throws FederateInternalError {
			synchronizationFailed = true;
		}

		/**
		 * The Discover Object Instance † service shall inform the joined federate to
		 * discover an object instance. Object instance discovery is described in 6.1.
		 * The object instance handle shall be unique to the federation execution and
		 * shall be uniform (see 6.8) throughout the federation execution. If the Convey
		 * Producing Federate Switch for this joined federate is enabled, the producing
		 * joined federate argument shall contain the designator for the joined federate
		 * that registered the object instance. This producing joined federate may not
		 * own instance attributes that caused the discovery, and, in fact, it may be no
		 * longer joined to the federation execution.
		 * 
		 * @param theObject      : Object instance handle.
		 * @param theObjectClass : Object class designator.
		 * @param objectName     : Object instance name.
		 * @throws FederateInternalError
		 */
		@Override
		public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
				String objectName) throws FederateInternalError {
			try {
				LOGGER.info("Discover: " + objectName);
				rtia.requestAttributeValueUpdate(theObject, attributes, null);
			} catch (ObjectInstanceNotKnown ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (AttributeNotDefined ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (FederateNotExecutionMember ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (SaveInProgress ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (RestoreInProgress ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (RTIinternalError ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (NotConnected ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			}
		}

		@Override
		// FIXME: use a reflectAttributeValues with timestamp, and print the value of
		// the timestamp
		public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
				byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
				LogicalTime theTime, OrderType receivedOrdering, SupplementalReflectInfo reflectInfo)
				throws FederateInternalError {
			try {
				Set<AttributeHandle> attributeHandleSet = theAttributes.keySet();
				LOGGER.info(" RAV with time= " + ((CertiLogicalTime1516E) theTime).getTime());
				for (AttributeHandle attributeHandle : attributeHandleSet) {
					if (attributeHandle.hashCode() == textAttributeHandle.hashCode()) {
						HLAASCIIstring value = new HLAASCIIstringImpl();
						ByteWrapper bw = theAttributes.getValueReference(attributeHandle);
						value.decode(bw);
						LOGGER.info("     --> Attribute text : " + value.getValue());
					}
					if (attributeHandle.hashCode() == fomAttributeHandle.hashCode()) {
						HLAfloat32BE value = new BasicHLAfloat32BEImpl();
						ByteWrapper bw = theAttributes.getValueReference(attributeHandle);
						value.decode(bw);
						LOGGER.info("     --> Attribute fom : " + value.getValue());
					}
				}
			} catch (DecoderException e) {
				LOGGER.log(Level.SEVERE, "Exception:", e);
				e.printStackTrace();
			}
		}

		/**
		 * Invocation of the Time Advance Grant † service shall indicate that a prior
		 * request to advance the joined federate’s logical time has been honored. The
		 * argument of this service shall indicate that the logical time for the joined
		 * federate has been advanced to this value. If the grant is issued in response
		 * to invocation of a Next Message Request or Time Advance Request service, the
		 * RTI shall guarantee that no additional TSO messages shall be delivered in the
		 * future with timestamps less than or equal to this value. If the grant is in
		 * response to an invocation of a Time Advance Request Available, Next Message
		 * Request Available, or Flush Queue Request service, the RTI shall guarantee
		 * that no additional TSO messages shall be delivered in the future with
		 * timestamps less than the value of the grant.
		 * 
		 * @param theTime : Logical time.
		 */
		@Override
		public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
			localHlaTime = new CertiLogicalTime1516E(((CertiLogicalTime1516E) theTime).getTime());
			timeAdvance = new CertiLogicalTime1516E(
					((CertiLogicalTime1516E) localHlaTime).getTime() + ((CertiLogicalTime1516E) timeStep).getTime());
			updateTime = new CertiLogicalTime1516E(((CertiLogicalTime1516E) localHlaTime).getTime() + updateTime1);
			LOGGER.info("     6.2 TAG with time=" + ((CertiLogicalTime1516E) theTime).getTime());
			LOGGER.info("\n");
			timeAdvanceGranted = true;
		}

		/**
		 * The Provide Attribute Value Update † service requests the current values for
		 * attributes owned by the joined federate for a given object instance. The
		 * owning joined federate responds to the Provide Attribute Value Update †
		 * service with an invocation of the Update Attribute Values service to provide
		 * the requested instance attribute values to the federation. The owning joined
		 * federate is not required to provide the values. The user-supplied tag
		 * argument supplied to the corresponding Request Attribute Value Update service
		 * invocation shall be provided with all corresponding Provide Attribute Value
		 * Update † service invocations.
		 * 
		 * @param theObject       : Object instance designator.
		 * @param theAttributes   : Set of attribute designators.
		 * @param userSuppliedTag : User-supplied tag.
		 */
		@Override
		public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
				byte[] userSuppliedTag) throws FederateInternalError {
			LOGGER.info("Object handle : " + theObject);
			LOGGER.info("Attributes : ");
			for (AttributeHandle attributeHandle : theAttributes) {
				LOGGER.info(attributeHandle.toString());
			}
			System.out.println();
		}
	}
}
