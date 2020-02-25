/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 * Federate must implement this interface.
 */

public interface FederateAmbassador {

////////////////////////////////////
// Federation Management Services //
////////////////////////////////////

	// 4.7
	void synchronizationPointRegistrationFailed(String synchronizationPointLabel) throws FederateInternalError;

	// 4.7
	void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel) throws FederateInternalError;

	// 4.8
	void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateInternalError;

	// 4.10
	void federationSynchronized(String synchronizationPointLabel) throws FederateInternalError;

	// 4.12
	void initiateFederateSave(String label) throws UnableToPerformSave, FederateInternalError;

	// 4.15
	void federationSaved() throws FederateInternalError;

	// 4.15
	void federationNotSaved() throws FederateInternalError;

	// 4.17
	void requestFederationRestoreSucceeded(String label) throws FederateInternalError;

	// 4.17
	void requestFederationRestoreFailed(String label, String reason) throws FederateInternalError;

	// 4.18
	void federationRestoreBegun() throws FederateInternalError;

	// 4.19
	void initiateFederateRestore(String label, int federateHandle)
			throws SpecifiedSaveLabelDoesNotExist, CouldNotRestore, FederateInternalError;

	// 4.21
	void federationRestored() throws FederateInternalError;

	// 4.21
	void federationNotRestored() throws FederateInternalError;

/////////////////////////////////////
// Declaration Management Services //
/////////////////////////////////////

	// 5.10
	void startRegistrationForObjectClass(int theClass) throws ObjectClassNotPublished, FederateInternalError;

	// 5.11
	void stopRegistrationForObjectClass(int theClass) throws ObjectClassNotPublished, FederateInternalError;

	// 5.12
	void turnInteractionsOn(int theHandle) throws InteractionClassNotPublished, FederateInternalError;

	// 5.13
	void turnInteractionsOff(int theHandle) throws InteractionClassNotPublished, FederateInternalError;

////////////////////////////////
// Object Management Services //
////////////////////////////////

	// 6.3
	void discoverObjectInstance(int theObject, int theObjectClass, String objectName)
			throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError;

	// 6.5
	void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag)
			throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, FederateInternalError;

	// 6.5
	void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag,
			LogicalTime theTime, EventRetractionHandle retractionHandle) throws ObjectNotKnown, AttributeNotKnown,
			FederateOwnsAttributes, InvalidFederationTime, FederateInternalError;

	// 6.7
	void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag)
			throws InteractionClassNotKnown, InteractionParameterNotKnown, FederateInternalError;

	// 6.7
	void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag,
			LogicalTime theTime, EventRetractionHandle eventRetractionHandle)
			throws InteractionClassNotKnown, InteractionParameterNotKnown, InvalidFederationTime, FederateInternalError;

	// 6.9
	void removeObjectInstance(int theObject, byte[] userSuppliedTag) throws ObjectNotKnown, FederateInternalError;

	// 6.9
	void removeObjectInstance(int theObject, byte[] userSuppliedTag, LogicalTime theTime,
			EventRetractionHandle retractionHandle) throws ObjectNotKnown, InvalidFederationTime, FederateInternalError;

	// 6.13
	void attributesInScope(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError;

	// 6.14
	void attributesOutOfScope(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError;

	// 6.16
	void provideAttributeValueUpdate(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError;

	// 6.17
	void turnUpdatesOnForObjectInstance(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotOwned, FederateInternalError;

	// 6.18
	void turnUpdatesOffForObjectInstance(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotOwned, FederateInternalError;

///////////////////////////////////
// Ownership Management Services //
///////////////////////////////////

	// 7.4
	void requestAttributeOwnershipAssumption(int theObject, AttributeHandleSet offeredAttributes,
			byte[] userSuppliedTag) throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned,
			AttributeNotPublished, FederateInternalError;

	// 7.5
	void attributeOwnershipDivestitureNotification(int theObject, AttributeHandleSet releasedAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, AttributeDivestitureWasNotRequested,
			FederateInternalError;

	// 7.6
	void attributeOwnershipAcquisitionNotification(int theObject, AttributeHandleSet securedAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned,
			AttributeNotPublished, FederateInternalError;

	// 7.9
	void attributeOwnershipUnavailable(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown,
			AttributeNotKnown, AttributeAlreadyOwned, AttributeAcquisitionWasNotRequested, FederateInternalError;

	// 7.10
	void requestAttributeOwnershipRelease(int theObject, AttributeHandleSet candidateAttributes, byte[] userSuppliedTag)
			throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError;

	// 7.14
	void confirmAttributeOwnershipAcquisitionCancellation(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned, AttributeAcquisitionWasNotCanceled,
			FederateInternalError;

	// 7.16
	void informAttributeOwnership(int theObject, int theAttribute, int theOwner)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError;

	// 7.16
	void attributeIsNotOwned(int theObject, int theAttribute)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError;

	// 7.16
	void attributeOwnedByRTI(int theObject, int theAttribute)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError;

//////////////////////////////
// Time Management Services //
//////////////////////////////

	// 8.3
	void timeRegulationEnabled(LogicalTime theFederateTime)
			throws InvalidFederationTime, EnableTimeRegulationWasNotPending, FederateInternalError;

	// 8.6
	void timeConstrainedEnabled(LogicalTime theFederateTime)
			throws InvalidFederationTime, EnableTimeConstrainedWasNotPending, FederateInternalError;

	// 8.13
	void timeAdvanceGrant(LogicalTime theTime)
			throws InvalidFederationTime, TimeAdvanceWasNotInProgress, FederateInternalError;

	// 8.22
	void requestRetraction(EventRetractionHandle theHandle) throws EventNotKnown, FederateInternalError;
}
