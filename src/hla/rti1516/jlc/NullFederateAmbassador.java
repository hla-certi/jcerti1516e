/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516.jlc;

import hla.rti1516.AttributeAcquisitionWasNotCanceled;
import hla.rti1516.AttributeAcquisitionWasNotRequested;
import hla.rti1516.AttributeAlreadyOwned;
import hla.rti1516.AttributeDivestitureWasNotRequested;
import hla.rti1516.AttributeHandle;
import hla.rti1516.AttributeHandleSet;
import hla.rti1516.AttributeHandleValueMap;
import hla.rti1516.AttributeNotOwned;
import hla.rti1516.AttributeNotPublished;
import hla.rti1516.AttributeNotRecognized;
import hla.rti1516.AttributeNotSubscribed;
import hla.rti1516.CouldNotDiscover;
import hla.rti1516.CouldNotInitiateRestore;
import hla.rti1516.FederateAmbassador;
import hla.rti1516.FederateHandle;
import hla.rti1516.FederateHandleRestoreStatusPair;
import hla.rti1516.FederateHandleSaveStatusPair;
import hla.rti1516.FederateInternalError;
import hla.rti1516.InteractionClassHandle;
import hla.rti1516.InteractionClassNotPublished;
import hla.rti1516.InteractionClassNotRecognized;
import hla.rti1516.InteractionClassNotSubscribed;
import hla.rti1516.InteractionParameterNotRecognized;
import hla.rti1516.InvalidLogicalTime;
import hla.rti1516.JoinedFederateIsNotInTimeAdvancingState;
import hla.rti1516.LogicalTime;
import hla.rti1516.MessageRetractionHandle;
import hla.rti1516.NoRequestToEnableTimeConstrainedWasPending;
import hla.rti1516.NoRequestToEnableTimeRegulationWasPending;
import hla.rti1516.ObjectClassHandle;
import hla.rti1516.ObjectClassNotPublished;
import hla.rti1516.ObjectClassNotRecognized;
import hla.rti1516.ObjectInstanceHandle;
import hla.rti1516.ObjectInstanceNotKnown;
import hla.rti1516.OrderType;
import hla.rti1516.ParameterHandleValueMap;
import hla.rti1516.RegionHandleSet;
import hla.rti1516.RestoreFailureReason;
import hla.rti1516.SaveFailureReason;
import hla.rti1516.SpecifiedSaveLabelDoesNotExist;
import hla.rti1516.SynchronizationPointFailureReason;
import hla.rti1516.TransportationType;
import hla.rti1516.UnableToPerformSave;
import hla.rti1516.UnknownName;

public class NullFederateAmbassador implements FederateAmbassador {
	// 4.7
	@Override
	public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
			throws FederateInternalError {
	}

	@Override
	public void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
			SynchronizationPointFailureReason reason) throws FederateInternalError {
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

	@Override
	public void initiateFederateSave(String label, LogicalTime time)
			throws InvalidLogicalTime, UnableToPerformSave, FederateInternalError {
	}

	// 4.15
	@Override
	public void federationSaved() throws FederateInternalError {
	}

	@Override
	public void federationNotSaved(SaveFailureReason reason) throws FederateInternalError {
	}

	// 4.17
	@Override
	public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] response) throws FederateInternalError {
	}

	// 4.19
	@Override
	public void requestFederationRestoreSucceeded(String label) throws FederateInternalError {
	}

	@Override
	public void requestFederationRestoreFailed(String label) throws FederateInternalError {
	}

	// 4.20
	@Override
	public void federationRestoreBegun() throws FederateInternalError {
	}

	// 4.21
	@Override
	public void initiateFederateRestore(String label, FederateHandle federateHandle)
			throws SpecifiedSaveLabelDoesNotExist, CouldNotInitiateRestore, FederateInternalError {
	}

	// 4.23
	@Override
	public void federationRestored() throws FederateInternalError {
	}

	@Override
	public void federationNotRestored(RestoreFailureReason reason) throws FederateInternalError {
	}

	// 4.25
	@Override
	public void federationRestoreStatusResponse(FederateHandleRestoreStatusPair[] response)
			throws FederateInternalError {
	}

	// 5.10
	@Override
	public void startRegistrationForObjectClass(ObjectClassHandle theClass)
			throws ObjectClassNotPublished, FederateInternalError {
	}

	// 5.11
	@Override
	public void stopRegistrationForObjectClass(ObjectClassHandle theClass)
			throws ObjectClassNotPublished, FederateInternalError {
	}

	// 5.12
	@Override
	public void turnInteractionsOn(InteractionClassHandle theHandle)
			throws InteractionClassNotPublished, FederateInternalError {
	}

	// 5.13
	@Override
	public void turnInteractionsOff(InteractionClassHandle theHandle)
			throws InteractionClassNotPublished, FederateInternalError {
	}

	// 6.3
	@Override
	public void objectInstanceNameReservationSucceeded(String objectName) throws UnknownName, FederateInternalError {
	}

	@Override
	public void objectInstanceNameReservationFailed(String objectName) throws UnknownName, FederateInternalError {
	}

	// 6.5
	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName) throws CouldNotDiscover, ObjectClassNotRecognized, FederateInternalError {
	}

	// 6.7
	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport,
			RegionHandleSet sentRegions)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, RegionHandleSet sentRegions)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle) throws ObjectInstanceNotKnown,
			AttributeNotRecognized, AttributeNotSubscribed, InvalidLogicalTime, FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, RegionHandleSet sentRegions)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, InvalidLogicalTime,
			FederateInternalError {
	}

	// 6.9
	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport)
			throws InteractionClassNotRecognized, InteractionParameterNotRecognized, InteractionClassNotSubscribed,
			FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport,
			RegionHandleSet sentRegions) throws InteractionClassNotRecognized, InteractionParameterNotRecognized,
			InteractionClassNotSubscribed, FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering) throws InteractionClassNotRecognized, InteractionParameterNotRecognized,
			InteractionClassNotSubscribed, FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, RegionHandleSet regions) throws InteractionClassNotRecognized,
			InteractionParameterNotRecognized, InteractionClassNotSubscribed, FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle messageRetractionHandle)
			throws InteractionClassNotRecognized, InteractionParameterNotRecognized, InteractionClassNotSubscribed,
			InvalidLogicalTime, FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationType theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle messageRetractionHandle, RegionHandleSet sentRegions)
			throws InteractionClassNotRecognized, InteractionParameterNotRecognized, InteractionClassNotSubscribed,
			InvalidLogicalTime, FederateInternalError {
	}

	// 6.11
	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering)
			throws ObjectInstanceNotKnown, FederateInternalError {
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering) throws ObjectInstanceNotKnown, FederateInternalError {
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle)
			throws ObjectInstanceNotKnown, InvalidLogicalTime, FederateInternalError {
	}

	// 6.15
	@Override
	public void attributesInScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError {
	}

	// 6.16
	@Override
	public void attributesOutOfScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotSubscribed, FederateInternalError {
	}

	// 6.18
	@Override
	public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError {
	}

	// 6.19
	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError {
	}

	// 6.20
	@Override
	public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError {
	}

	// 7.4
	@Override
	public void requestAttributeOwnershipAssumption(ObjectInstanceHandle theObject,
			AttributeHandleSet offeredAttributes, byte[] userSuppliedTag) throws ObjectInstanceNotKnown,
			AttributeNotRecognized, AttributeAlreadyOwned, AttributeNotPublished, FederateInternalError {
	}

	// 7.5
	@Override
	public void requestDivestitureConfirmation(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned,
			AttributeDivestitureWasNotRequested, FederateInternalError {
	}

	// 7.7
	@Override
	public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle theObject,
			AttributeHandleSet securedAttributes, byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeAcquisitionWasNotRequested,
			AttributeAlreadyOwned, AttributeNotPublished, FederateInternalError {
	}

	// 7.10
	@Override
	public void attributeOwnershipUnavailable(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeAlreadyOwned,
			AttributeAcquisitionWasNotRequested, FederateInternalError {
	}

	// 7.11
	@Override
	public void requestAttributeOwnershipRelease(ObjectInstanceHandle theObject, AttributeHandleSet candidateAttributes,
			byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, AttributeNotOwned, FederateInternalError {
	}

	// 7.15
	@Override
	public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes) throws ObjectInstanceNotKnown, AttributeNotRecognized,
			AttributeAlreadyOwned, AttributeAcquisitionWasNotCanceled, FederateInternalError {
	}

	// 7.17
	@Override
	public void informAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			FederateHandle theOwner) throws ObjectInstanceNotKnown, AttributeNotRecognized, FederateInternalError {
	}

	@Override
	public void attributeIsNotOwned(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, FederateInternalError {
	}

	@Override
	public void attributeIsOwnedByRTI(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws ObjectInstanceNotKnown, AttributeNotRecognized, FederateInternalError {
	}

	// 8.3
	@Override
	public void timeRegulationEnabled(LogicalTime time)
			throws InvalidLogicalTime, NoRequestToEnableTimeRegulationWasPending, FederateInternalError {
	}

	// 8.6
	@Override
	public void timeConstrainedEnabled(LogicalTime time)
			throws InvalidLogicalTime, NoRequestToEnableTimeConstrainedWasPending, FederateInternalError {
	}

	// 8.13
	@Override
	public void timeAdvanceGrant(LogicalTime theTime)
			throws InvalidLogicalTime, JoinedFederateIsNotInTimeAdvancingState, FederateInternalError {
	}

	// 8.22
	@Override
	public void requestRetraction(MessageRetractionHandle theHandle) throws FederateInternalError {
	}
}
