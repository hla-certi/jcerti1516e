/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti.jlc;

import hla.rti.AttributeAcquisitionWasNotCanceled;
import hla.rti.AttributeAcquisitionWasNotRequested;
import hla.rti.AttributeAlreadyOwned;
import hla.rti.AttributeDivestitureWasNotRequested;
import hla.rti.AttributeHandleSet;
import hla.rti.AttributeNotKnown;
import hla.rti.AttributeNotOwned;
import hla.rti.AttributeNotPublished;
import hla.rti.CouldNotDiscover;
import hla.rti.CouldNotRestore;
import hla.rti.EnableTimeConstrainedWasNotPending;
import hla.rti.EnableTimeRegulationWasNotPending;
import hla.rti.EventNotKnown;
import hla.rti.EventRetractionHandle;
import hla.rti.FederateAmbassador;
import hla.rti.FederateInternalError;
import hla.rti.FederateOwnsAttributes;
import hla.rti.InteractionClassNotKnown;
import hla.rti.InteractionClassNotPublished;
import hla.rti.InteractionParameterNotKnown;
import hla.rti.InvalidFederationTime;
import hla.rti.LogicalTime;
import hla.rti.ObjectClassNotKnown;
import hla.rti.ObjectClassNotPublished;
import hla.rti.ObjectNotKnown;
import hla.rti.ReceivedInteraction;
import hla.rti.ReflectedAttributes;
import hla.rti.SpecifiedSaveLabelDoesNotExist;
import hla.rti.TimeAdvanceWasNotInProgress;
import hla.rti.UnableToPerformSave;

/**
 * Provides empty implementations for all methods in FederateAmbassador.
 */
public class NullFederateAmbassador implements FederateAmbassador {
	// 4.7
	@Override
	public void synchronizationPointRegistrationFailed(String synchronizationPointLabel) throws FederateInternalError {
	}

	// 4.7
	@Override
	public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
			throws FederateInternalError {
	}

	// 4.8
	@Override
	public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateInternalError {
	}

	// 4.10
	@Override
	public void federationSynchronized(String synchronizationPointLabel) throws FederateInternalError {
	}

	// 4.12
	@Override
	public void initiateFederateSave(String label) throws UnableToPerformSave, FederateInternalError {
	}

	// 4.15
	@Override
	public void federationSaved() throws FederateInternalError {
	}

	// 4.15
	@Override
	public void federationNotSaved() throws FederateInternalError {
	}

	// 4.17
	@Override
	public void requestFederationRestoreSucceeded(String label) throws FederateInternalError {
	}

	// 4.17
	@Override
	public void requestFederationRestoreFailed(String label, String reason) throws FederateInternalError {
	}

	// 4.18
	@Override
	public void federationRestoreBegun() throws FederateInternalError {
	}

	// 4.19
	@Override
	public void initiateFederateRestore(String label, int federateHandle)
			throws SpecifiedSaveLabelDoesNotExist, CouldNotRestore, FederateInternalError {
	}

	// 4.21
	@Override
	public void federationRestored() throws FederateInternalError {
	}

	// 4.21
	@Override
	public void federationNotRestored() throws FederateInternalError {
	}

	// 5.10
	@Override
	public void startRegistrationForObjectClass(int theClass) throws ObjectClassNotPublished, FederateInternalError {
	}

	// 5.11
	@Override
	public void stopRegistrationForObjectClass(int theClass) throws ObjectClassNotPublished, FederateInternalError {
	}

	// 5.12
	@Override
	public void turnInteractionsOn(int theHandle) throws InteractionClassNotPublished, FederateInternalError {
	}

	// 5.13
	@Override
	public void turnInteractionsOff(int theHandle) throws InteractionClassNotPublished, FederateInternalError {
	}

	// 6.3
	@Override
	public void discoverObjectInstance(int theObject, int theObjectClass, String objectName)
			throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError {
	}

	// 6.5
	@Override
	public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag)
			throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, FederateInternalError {
	}

	// 6.5
	@Override
	public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag,
			LogicalTime theTime, EventRetractionHandle retractionHandle) throws ObjectNotKnown, AttributeNotKnown,
			FederateOwnsAttributes, InvalidFederationTime, FederateInternalError {
	}

	// 6.7
	@Override
	public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag)
			throws InteractionClassNotKnown, InteractionParameterNotKnown, FederateInternalError {
	}

	// 6.7
	@Override
	public void receiveInteraction(int interactionClass, ReceivedInteraction theInteraction, byte[] userSuppliedTag,
			LogicalTime theTime, EventRetractionHandle eventRetractionHandle) throws InteractionClassNotKnown,
			InteractionParameterNotKnown, InvalidFederationTime, FederateInternalError {
	}

	// 6.9
	@Override
	public void removeObjectInstance(int theObject, byte[] userSuppliedTag)
			throws ObjectNotKnown, FederateInternalError {
	}

	// 6.9
	@Override
	public void removeObjectInstance(int theObject, byte[] userSuppliedTag, LogicalTime theTime,
			EventRetractionHandle retractionHandle)
			throws ObjectNotKnown, InvalidFederationTime, FederateInternalError {
	}

	// 6.13
	@Override
	public void attributesInScope(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {
	}

	// 6.14
	@Override
	public void attributesOutOfScope(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {
	}

	// 6.16
	@Override
	public void provideAttributeValueUpdate(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError {
	}

	// 6.17
	@Override
	public void turnUpdatesOnForObjectInstance(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotOwned, FederateInternalError {
	}

	// 6.18
	@Override
	public void turnUpdatesOffForObjectInstance(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotOwned, FederateInternalError {
	}

	// 7.4
	@Override
	public void requestAttributeOwnershipAssumption(int theObject, AttributeHandleSet offeredAttributes,
			byte[] userSuppliedTag) throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned,
			AttributeNotPublished, FederateInternalError {
	}

	// 7.5
	@Override
	public void attributeOwnershipDivestitureNotification(int theObject, AttributeHandleSet releasedAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, AttributeDivestitureWasNotRequested,
			FederateInternalError {
	}

	// 7.6
	@Override
	public void attributeOwnershipAcquisitionNotification(int theObject, AttributeHandleSet securedAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeAcquisitionWasNotRequested, AttributeAlreadyOwned,
			AttributeNotPublished, FederateInternalError {
	}

	// 7.9
	@Override
	public void attributeOwnershipUnavailable(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown,
			AttributeNotKnown, AttributeAlreadyOwned, AttributeAcquisitionWasNotRequested, FederateInternalError {
	}

	// 7.10
	@Override
	public void requestAttributeOwnershipRelease(int theObject, AttributeHandleSet candidateAttributes,
			byte[] userSuppliedTag) throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError {
	}

	// 7.14
	@Override
	public void confirmAttributeOwnershipAcquisitionCancellation(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotKnown, AttributeAlreadyOwned, AttributeAcquisitionWasNotCanceled,
			FederateInternalError {
	}

	// 7.16
	@Override
	public void informAttributeOwnership(int theObject, int theAttribute, int theOwner)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {
	}

	// 7.16
	@Override
	public void attributeIsNotOwned(int theObject, int theAttribute)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {
	}

	// 7.16
	@Override
	public void attributeOwnedByRTI(int theObject, int theAttribute)
			throws ObjectNotKnown, AttributeNotKnown, FederateInternalError {
	}

	// 8.3
	@Override
	public void timeRegulationEnabled(LogicalTime theFederateTime)
			throws InvalidFederationTime, EnableTimeRegulationWasNotPending, FederateInternalError {
	}

	// 8.6
	@Override
	public void timeConstrainedEnabled(LogicalTime theFederateTime)
			throws InvalidFederationTime, EnableTimeConstrainedWasNotPending, FederateInternalError {
	}

	// 8.13
	@Override
	public void timeAdvanceGrant(LogicalTime theTime)
			throws InvalidFederationTime, TimeAdvanceWasNotInProgress, FederateInternalError {
	}

	// 8.22
	@Override
	public void requestRetraction(EventRetractionHandle theHandle) throws EventNotKnown, FederateInternalError {
	}
}
