package hla.rti1516e.jlc;

import java.util.Set;

import hla.rti1516e.*;
import hla.rti1516e.exceptions.FederateInternalError;

public class NullFederateAmbassador implements FederateAmbassador
{

	@Override
	public void connectionLost(String faultDescription) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportFederationExecutions(FederationExecutionInformationSet theFederationExecutionInformationSet)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
			SynchronizationPointFailureReason reason) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateFederateSave(String label) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateFederateSave(String label, LogicalTime time) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationSaved() throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationNotSaved(SaveFailureReason reason) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationSaveStatusResponse(FederateHandleSaveStatusPair[] response) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestFederationRestoreSucceeded(String label) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestFederationRestoreFailed(String label) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationRestoreBegun() throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initiateFederateRestore(String label, String federateName, FederateHandle federateHandle)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationRestored() throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationNotRestored(RestoreFailureReason reason) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void federationRestoreStatusResponse(FederateRestoreStatus[] response) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRegistrationForObjectClass(ObjectClassHandle theClass) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnInteractionsOn(InteractionClassHandle theHandle) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnInteractionsOff(InteractionClassHandle theHandle) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectInstanceNameReservationSucceeded(String objectName) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void objectInstanceNameReservationFailed(String objectName) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multipleObjectInstanceNameReservationSucceeded(Set<String> objectNames) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void multipleObjectInstanceNameReservationFailed(Set<String> objectNames) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass,
			String objectName, FederateHandle producingFederate) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
			SupplementalReflectInfo reflectInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, SupplementalReflectInfo reflectInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reflectAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReflectInfo reflectInfo)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport,
			SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, SupplementalReceiveInfo receiveInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInteraction(InteractionClassHandle interactionClass, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag, OrderType sentOrdering, TransportationTypeHandle theTransport, LogicalTime theTime,
			OrderType receivedOrdering, MessageRetractionHandle retractionHandle, SupplementalReceiveInfo receiveInfo)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, SupplementalRemoveInfo removeInfo)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeObjectInstance(ObjectInstanceHandle theObject, byte[] userSuppliedTag, OrderType sentOrdering,
			LogicalTime theTime, OrderType receivedOrdering, MessageRetractionHandle retractionHandle,
			SupplementalRemoveInfo removeInfo) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributesInScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributesOutOfScope(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnUpdatesOnForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			String updateRateDesignator) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void turnUpdatesOffForObjectInstance(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmAttributeTransportationTypeChange(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes, TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportAttributeTransportationType(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmInteractionTransportationTypeChange(InteractionClassHandle theInteraction,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reportInteractionTransportationType(FederateHandle theFederate, InteractionClassHandle theInteraction,
			TransportationTypeHandle theTransportation) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestAttributeOwnershipAssumption(ObjectInstanceHandle theObject,
			AttributeHandleSet offeredAttributes, byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestDivestitureConfirmation(ObjectInstanceHandle theObject, AttributeHandleSet offeredAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeOwnershipAcquisitionNotification(ObjectInstanceHandle theObject,
			AttributeHandleSet securedAttributes, byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeOwnershipUnavailable(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestAttributeOwnershipRelease(ObjectInstanceHandle theObject, AttributeHandleSet candidateAttributes,
			byte[] userSuppliedTag) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void confirmAttributeOwnershipAcquisitionCancellation(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void informAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute,
			FederateHandle theOwner) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeIsNotOwned(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void attributeIsOwnedByRTI(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeRegulationEnabled(LogicalTime time) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeConstrainedEnabled(LogicalTime time) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void requestRetraction(MessageRetractionHandle theHandle) throws FederateInternalError {
		// TODO Auto-generated method stub
		
	}
}


