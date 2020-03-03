package integrationTests.StringFddName.NMR;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiLogicalTimeInterval1516E;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.SynchronizationPointFailureReason;
import hla.rti1516e.exceptions.AsynchronousDeliveryAlreadyEnabled;
import hla.rti1516e.exceptions.AttributeNotDefined;
import hla.rti1516e.exceptions.FederateInternalError;
import hla.rti1516e.exceptions.FederateNotExecutionMember;
import hla.rti1516e.exceptions.InvalidObjectClassHandle;
import hla.rti1516e.exceptions.NameNotFound;
import hla.rti1516e.exceptions.NotConnected;
import hla.rti1516e.exceptions.ObjectClassNotDefined;
import hla.rti1516e.exceptions.ObjectClassNotPublished;
import hla.rti1516e.exceptions.ObjectInstanceNameInUse;
import hla.rti1516e.exceptions.ObjectInstanceNameNotReserved;
import hla.rti1516e.exceptions.OwnershipAcquisitionPending;
import hla.rti1516e.exceptions.RTIinternalError;
import hla.rti1516e.exceptions.RestoreInProgress;
import hla.rti1516e.exceptions.SaveInProgress;
import hla.rti1516e.impl.CertiAttributeHandleSet;
import hla.rti1516e.jlc.NullFederateAmbassador;

/**
 * FederateAmbassador specific to send values to a federation
 */
public class SenderFederateAmbassador extends NullFederateAmbassador {
	public boolean isCreator;
	public LogicalTime localHlaTime;
	public LogicalTimeInterval lookahead;
	public LogicalTime timeStep;
	public LogicalTime timeAdvance;
	public double updateTime1;
	public LogicalTime updateTime;
	public boolean timeAdvanceGranted;
	public boolean timeRegulator;
	public boolean timeConstrained;
	public boolean synchronizationSuccess;
	public boolean synchronizationFailed;
	public boolean inPause;
	public String synchronizationPointName = "InitSync";

	public ObjectInstanceHandle myObject;
	public AttributeHandle textAttributeHandle;
	public AttributeHandle fomAttributeHandle;

	/**
	 * The Start Registration For Object Class † service shall notify the joined
	 * federate that registration of new object instances of the specified object
	 * class is advised because at least one of the class attributes that the joined
	 * federate is publishing at this object class is actively subscribed to at the
	 * specified object class or at a superclass of the specified object class by at
	 * least one other joined federate in the federation execution. The joined
	 * federate should commence with registration of object instances of the
	 * specified class. Generation of the Start Registration For Object Class †
	 * service advisory shall be controlled using the Enable/Disable Object Class
	 * Relevance Advisory Switch services. The Start Registration For Object Class †
	 * service shall be invoked only when the Object Class Relevance Advisory Switch
	 * is enabled for the joined federate.
	 *
	 * @param theClass : Object class designator.
	 */
	@Override
	public void startRegistrationForObjectClass(ObjectClassHandle theClass) {
		SendandReceiveValues_NMR.LOGGER.info("Object class: " + theClass);
	}

	/**
	 * Initialization of all attributes, publish and register objects Enable time
	 * regulation
	 *
	 * @param rtia : RTI ambassador
	 */
	public void initialize(RTIambassador rtia, double timeStepArg, double updateTimeArg, double lookaheadArg)
			throws NameNotFound, FederateNotExecutionMember, RTIinternalError, ObjectClassNotDefined,
			AttributeNotDefined, OwnershipAcquisitionPending, SaveInProgress, RestoreInProgress,
			ObjectClassNotPublished, NotConnected, InvalidObjectClassHandle, ObjectInstanceNameInUse,
			ObjectInstanceNameNotReserved {
		SendandReceiveValues_NMR.LOGGER.info("     4.1 Get object class handle");
		// The uav.xml has a class 'SampleClass' and attributes
		// TextAttribute and FOMAttribute
		ObjectClassHandle classHandle = rtia.getObjectClassHandle("SampleClass");

		SendandReceiveValues_NMR.LOGGER.info("     4.2 Get atribute handles");
		textAttributeHandle = rtia.getAttributeHandle(classHandle, "TextAttribute");
		fomAttributeHandle = rtia.getAttributeHandle(classHandle, "FOMAttribute");

		AttributeHandleSet attributes = new CertiAttributeHandleSet();
		attributes.add(textAttributeHandle);
		attributes.add(fomAttributeHandle);

		try {
			rtia.enableAsynchronousDelivery();
		} catch (AsynchronousDeliveryAlreadyEnabled asynchronousDeliveryAlreadyEnabled) {
			asynchronousDeliveryAlreadyEnabled.printStackTrace();
		}

		SendandReceiveValues_NMR.LOGGER.info("     4.3 Publish object");
		rtia.publishObjectClassAttributes(classHandle, attributes);

		SendandReceiveValues_NMR.LOGGER.info("     4.4 Register object instance");
		myObject = rtia.registerObjectInstance(classHandle, "HAF");

		SendandReceiveValues_NMR.LOGGER
				.info("     4.5. Set time management configuration (Regulator with lookahed and Constrained)");

		localHlaTime = new CertiLogicalTime1516E(0.0);
		lookahead = new CertiLogicalTimeInterval1516E(lookaheadArg);

		updateTime1 = updateTimeArg;
		timeStep = new CertiLogicalTime1516E(timeStepArg);
		updateTime = new CertiLogicalTime1516E(updateTimeArg);
		// The time is advanced by adding localHlaTime + timeStep; starts with
		// (0.0+timeStepArg)
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
		SendandReceiveValues_NMR.LOGGER.info("Discover Object Instance : " + "Object = " + theObject.toString()
				+ ", Object class = " + theObjectClass.toString() + ", Object name = " + objectName);
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
	 * @param theObject         : Object instance handle.
	 * @param theObjectClass    : Object class designator.
	 * @param objectName        : Object instance name.
	 * @param producingFederate : Optional producing joined federate designator.
	 * @throws FederateInternalError
	 */
	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName, FederateHandle producingFederate) throws FederateInternalError {
		SendandReceiveValues_NMR.LOGGER.info("Discover Object Instance : " + "Object = " + theObject.toString()
				+ ", Object class = " + theObjectClass.toString() + ", Object name = " + objectName
				+ ", Producing federate = " + producingFederate.toString());
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
		SendandReceiveValues_NMR.LOGGER
				.info("Sender ------- 6.3 TAG with time = " + ((CertiLogicalTime1516E) theTime).getTime());
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
		SendandReceiveValues_NMR.LOGGER.info("Object handle hashCode : " + theObject.hashCode());
		SendandReceiveValues_NMR.LOGGER.info("Attributes : ");
		for (AttributeHandle attributeHandle : theAttributes) {
			SendandReceiveValues_NMR.LOGGER.info("Hash : " + attributeHandle.hashCode());
		}
	}
}
