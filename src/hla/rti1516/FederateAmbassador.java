/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

/**
 * Federate must implement this interface.
 */

public interface FederateAmbassador {

////////////////////////////////////
// Federation Management Services //
////////////////////////////////////

	// 4.7
	void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel) throws FederateInternalError;

	void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
			SynchronizationPointFailureReason reason) throws FederateInternalError;

	// 4.8
	void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateInternalError;

	// 4.10
	void federationSynchronized(String synchronizationPointLabel) throws FederateInternalError;

	// 4.12
	void initiateFederateSave(String label) throws UnableToPerformSave, FederateInternalError;

	void initiateFederateSave(String label, LogicalTime time)
			throws InvalidLogicalTime, UnableToPerformSave, FederateInternalError;

	// 4.15
	void federationSaved() throws FederateInternalError;

	void federationNotSaved(SaveFailureReason reason) throws FederateInternalError;

	// 4.17
	void federationSaveStatusResponse(FederateHandleSaveStatusPair[] response) throws FederateInternalError;

	// 4.19
	void requestFederationRestoreSucceeded(String label) throws FederateInternalError;

	void requestFederationRestoreFailed(String label) throws FederateInternalError;

	// 4.20
	void federationRestoreBegun() throws FederateInternalError;

	// 4.21
	void initiateFederateRestore(String label, FederateHandle federateHandle)
			throws SpecifiedSaveLabelDoesNotExist, CouldNotInitiateRestore, FederateInternalError;

	// 4.23
	void federationRestored() throws FederateInternalError;

	void federationNotRestored(RestoreFailureReason reason) throws FederateInternalError;

	// 4.25
	void federationRestoreStatusResponse(FederateHandleRestoreStatusPair[] response) throws FederateInternalError;

/////////////////////////////////////
// Declaration Management Services //
/////////////////////////////////////

	// 5.10
	void startRegistrationForObjectClass(ObjectClassHandle theClass)
			throws ObjectClassNotPublished, FederateInternalError;

	// 5.11
	void stopRegistrationForObjectClass(ObjectClassHandle theClass)
			throws ObjectClassNotPublished, FederateInternalError;

	// 5.12
	void turnInteractionsOn(InteractionClassHandle theHandle)
			throws InteractionClassNotPublished, FederateInternalError;

	// 5.13
	void turnInteractionsOff(InteractionClassHandle theHandle)
			throws InteractionClassNotPublished, FederateInternalError;

////////////////////////////////
// Object Management Services //
////////////////////////////////

	// 6.3
	void objectInstanceNameReservationSucceeded(String objectName) throws UnknownName, FederateInternalError;

	void objectInstanceNameReservationFailed(String objectName) throws UnknownName, FederateInternalError;

	// 6.5
	void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName)
			throws CouldNotDiscover, ObjectClassNotRecognized, FederateInternalError;

	// 6.7
	void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError;

	void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport,
			RegionHandleSet sentRegions)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError;

	void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError;

	void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, RegionHandleSet sentRegions)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError;

	void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle) throws ObjectInstanceNotKnown,
			AttributeNotRecognized, AttributeNotSubscribed, InvalidLogicalTime, FederateInternalError;

	void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, RegionHandleSet sentRegions)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, InvalidLogicalTime,
			FederateInternalError;

	// 6.9
	void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport)
			throws InteractionClassNotRecognized, InteractionParameterNotRecognized, InteractionClassNotSubscribed,
			FederateInternalError;

	void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport,
			RegionHandleSet sentRegions) throws InteractionClassNotRecognized, InteractionParameterNotRecognized,
			InteractionClassNotSubscribed, FederateInternalError;

	void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering) throws InteractionClassNotRecognized, InteractionParameterNotRecognized,
			InteractionClassNotSubscribed, FederateInternalError;

	void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, RegionHandleSet regions) throws InteractionClassNotRecognized,
			InteractionParameterNotRecognized, InteractionClassNotSubscribed, FederateInternalError;

	void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle messageRetractionHandle)
			throws InteractionClassNotRecognized, InteractionParameterNotRecognized, InteractionClassNotSubscribed,
			InvalidLogicalTime, FederateInternalError;

	void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle messageRetractionHandle, RegionHandleSet sentRegions)
			throws InteractionClassNotRecognized, InteractionParameterNotRecognized, InteractionClassNotSubscribed,
			InvalidLogicalTime, FederateInternalError;

	// 6.11
	void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering)
			throws ObjectInstanceNotKnown, FederateInternalError;

	void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering) throws ObjectInstanceNotKnown, FederateInternalError;

	void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle)
			throws ObjectInstanceNotKnown, InvalidLogicalTime, FederateInternalError;

	// 6.15
	void attributesInScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError;

	// 6.16
	void attributesOutOfScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError;

	// 6.18
	void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError;

	// 6.19
	void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError;

	// 6.20
	void turnUpdatesOffForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError;

///////////////////////////////////
// Ownership Management Services //
///////////////////////////////////

	// 7.4
	void requestAttributeOwnershipAssumption(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes,
			byte[] userSuppliedTag) throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeAlreadyOwned,
			AttributeNotPublished, FederateInternalError;

	// 7.5
	void requestDivestitureConfirmation(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned,
			AttributeDivestitureWasNotRequested, FederateInternalError;

	// 7.7
	void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle theObject, AttributeHandleSet securedAttributes,
			byte[] userSuppliedTag) throws ObjectInstanceNotKnown, AttributeNotRecognized,
			AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned, AttributeNotPublished, FederateInternalError;

	// 7.10
	void attributeOwnershipUnavailable(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeAlreadyOwned,
			AttributeAcquisitionWasNotRequested, FederateInternalError;

	// 7.11
	void requestAttributeOwnershipRelease(ObjectInstanceHandle theObject, AttributeHandleSet candidateAttributes,
			byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError;

	// 7.15
	void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes) throws ObjectInstanceNotKnown, AttributeNotRecognized,
			AttributeAlreadyOwned, AttributeAcquisitionWasNotCanceled, FederateInternalError;

	// 7.17
	void informAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute, FederateHandle theOwner)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, FederateInternalError;

	void attributeIsNotOwned(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, FederateInternalError;

	void attributeIsOwnedByRTI(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, FederateInternalError;

//////////////////////////////
// Time Management Services //
//////////////////////////////

	// 8.3
	void timeRegulationEnabled(LogicalTime time)
			throws InvalidLogicalTime, NoRequestToEnableTimeRegulationWasPending, FederateInternalError;

	// 8.6
	void timeConstrainedEnabled(LogicalTime time)
			throws InvalidLogicalTime, NoRequestToEnableTimeConstrainedWasPending, FederateInternalError;

	// 8.13
	void timeAdvanceGrant(LogicalTime theTime)
			throws InvalidLogicalTime, JoinedFederateIsNotInTimeAdvancingState, FederateInternalError;

	// 8.22
	void requestRetraction(MessageRetractionHandle theHandle) throws FederateInternalError;
}
//end FederateAmbassador

//File: FederateHandle.java
