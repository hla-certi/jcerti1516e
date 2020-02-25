/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

package hla.rti1516e;

import java.util.Set;

import hla.rti1516e.exceptions.FederateInternalError;

public class NullFederateAmbassador implements FederateAmbassador {
	@Override
	public void connectionLost(String faultDescription) throws FederateInternalError {
	}

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
	public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet)
			throws FederateInternalError {
	}

	// 4.12
	@Override
	public void initiateFederateSave(String label) throws FederateInternalError {
	}

	@Override
	public void initiateFederateSave(String label, LogicalTime time) throws FederateInternalError {
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
	public void initiateFederateRestore(String label, String federateName, FederateHandle federateHandle)
			throws FederateInternalError {
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
	public void federationRestoreStatusResponse(FederateRestoreStatus[] response) throws FederateInternalError {
	}

	@Override
	public void reportFederationExecutions(FederationExecutionInformationSet theFederationExecutionInformationSet)
			throws FederateInternalError {
	}

	// 5.10
	@Override
	public void startRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
	}

	// 5.11
	@Override
	public void stopRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
	}

	// 5.12
	@Override
	public void turnInteractionsOn(InteractionClassHandle theHandle) throws FederateInternalError {
	}

	// 5.13
	@Override
	public void turnInteractionsOff(InteractionClassHandle theHandle) throws FederateInternalError {
	}

	// 6.3
	@Override
	public void objectInstanceNameReservationSucceeded(String objectName) throws FederateInternalError {
	}

	@Override
	public void multipleObjectInstanceNameReservationSucceeded(Set<String> objectNames) throws FederateInternalError {
	}

	@Override
	public void objectInstanceNameReservationFailed(String objectName) throws FederateInternalError {
	}

	@Override
	public void multipleObjectInstanceNameReservationFailed(Set<String> objectNames) throws FederateInternalError {
	}

	// 6.5
	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName) throws FederateInternalError {
	}

	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName, FederateHandle producingFederate) throws FederateInternalError {
	}

	// 6.7
	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
			SupplementalReflectInfo reflectInfo) throws FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, SupplementalReflectInfo reflectInfo) throws FederateInternalError {
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReflectInfo reflectInfo)
			throws FederateInternalError {
	}

	// 6.9

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
			SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo)
			throws FederateInternalError {
	}

	// 6.11
	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			SupplementalRemoveInfo removeInfo) throws FederateInternalError {
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, SupplementalRemoveInfo removeInfo)
			throws FederateInternalError {
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle,
			SupplementalRemoveInfo removeInfo) throws FederateInternalError {
	}

	// 6.15
	@Override
	public void attributesInScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
	}

	// 6.16
	@Override
	public void attributesOutOfScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
	}

	// 6.18
	@Override
	public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag) throws FederateInternalError {
	}

	// 6.19
	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
	}

	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			String updateRateDesignator) throws FederateInternalError {
	}

	// 6.20
	@Override
	public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
	}

	// 6.20
	@Override
	public void confirmAttributeTransportationTypeChange(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes, TransportationTypeHandle theTransportation) throws FederateInternalError {
	}

	// 6.20
	@Override
	public void confirmInteractionTransportationTypeChange(InteractionClassHandle theInteraction,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
	}

	// 6.20
	@Override
	public void reportAttributeTransportationType(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
	}

	// 6.20
	@Override
	public void reportInteractionTransportationType(FederateHandle theFederate, InteractionClassHandle theInteraction,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
	}

	// 7.4
	@Override
	public void requestAttributeOwnershipAssumption(ObjectInstanceHandle theObject,
			AttributeHandleSet offeredAttributes, byte[] userSuppliedTag) throws FederateInternalError {
	}

	// 7.5
	@Override
	public void requestDivestitureConfirmation(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes)
			throws FederateInternalError {
	}

	// 7.7
	@Override
	public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle theObject,
			AttributeHandleSet securedAttributes, byte[] userSuppliedTag) throws FederateInternalError {
	}

	// 7.10
	@Override
	public void attributeOwnershipUnavailable(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
	}

	// 7.11
	@Override
	public void requestAttributeOwnershipRelease(ObjectInstanceHandle theObject, AttributeHandleSet candidateAttributes,
			byte[] userSuppliedTag) throws FederateInternalError {
	}

	// 7.15
	@Override
	public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes) throws FederateInternalError {
	}

	// 7.17
	@Override
	public void informAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			FederateHandle theOwner) throws FederateInternalError {
	}

	@Override
	public void attributeIsNotOwned(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws FederateInternalError {
	}

	@Override
	public void attributeIsOwnedByRTI(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws FederateInternalError {
	}

	// 8.3
	@Override
	public void timeRegulationEnabled(LogicalTime time) throws FederateInternalError {
	}

	// 8.6
	@Override
	public void timeConstrainedEnabled(LogicalTime time) throws FederateInternalError {
	}

	// 8.13
	@Override
	public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
	}

	// 8.22
	@Override
	public void requestRetraction(MessageRetractionHandle theHandle) throws FederateInternalError {
	}
}
