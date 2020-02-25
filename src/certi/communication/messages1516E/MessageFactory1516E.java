package certi.communication.messages1516E;

import java.io.IOException;

import certi.communication.CertiException;
import certi.communication.CertiMessage;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import certi.communication.messages.CreateFederationExecution;
import certi.communication.messages.JoinFederationExecution;

public class MessageFactory1516E {
	/**
	 * Instantiate <code>CertiMessage</code> from supplied message type. It does not
	 * use reflection for safety.
	 *
	 * @param messageType message type to instantiate
	 * @return instantiated message
	 */
	public static CertiMessage instantiate(CertiMessageType messageType) {
		// LOGGER.info("Trying to instantiate " + messageType);
		switch (messageType) {
		case OPEN_CONNEXION:
			return new OpenConnexion1516E();
		case CLOSE_CONNEXION:
			return new CloseConnexion1516E();
		case CREATE_FEDERATION_EXECUTION:
			return new CreateFederationExecution();
		case DESTROY_FEDERATION_EXECUTION:
			return new DestroyFederationExecution1516E();
		case REGISTER_FEDERATION_SYNCHRONIZATION_POINT:
			return new RegisterFederationSynchronizationPoint1516E();
		case ANNOUNCE_SYNCHRONIZATION_POINT:
			return new AnnounceSynchronizationPoint1516E();
		case REQUEST_FEDERATION_RESTORE_FAILED:
			return new RequestFederationRestoreFailed1516E();
		case SYNCHRONIZATION_POINT_REGISTRATION_FAILED:
			return new SynchronizationPointRegistrationFailed1516E();
		case SYNCHRONIZATION_POINT_REGISTRATION_SUCCEEDED:
			return new SynchronizationPointRegistrationSucceeded1516E();
		case SYNCHRONIZATION_POINT_ACHIEVED:
			return new SynchronizationPointAchieved1516E();
		case FEDERATION_SYNCHRONIZED:
			return new FederationSynchronized1516E();
		case REQUEST_FEDERATION_RESTORE:
			return new RequestFederationRestore1516E();
		case REQUEST_FEDERATION_RESTORE_SUCCEEDED:
			return new RequestFederationRestoreSucceeded1516E();
		case INITIATE_FEDERATE_RESTORE:
			return new InitiateFederateRestore1516E();
		case INITIATE_FEDERATE_SAVE:
			return new InitiateFederateSave1516E();
		case REQUEST_FEDERATION_SAVE:
			return new RequestFederationSave1516E();
		case UNPUBLISH_OBJECT_CLASS:
			return new UnpublishObjectClass1516E();
		case UNSUBSCRIBE_OBJECT_CLASS:
			return new UnsubscribeObjectClass1516E();
		case START_REGISTRATION_FOR_OBJECT_CLASS:
			return new StartRegistrationForObjectClass1516E();
		case STOP_REGISTRATION_FOR_OBJECT_CLASS:
			return new StopRegistrationForObjectClass1516E();
		case IS_ATTRIBUTE_OWNED_BY_FEDERATE:
			return new IsAttributeOwnedByFederate1516E();
		case QUERY_ATTRIBUTE_OWNERSHIP:
			return new QueryAttributeOwnership1516E();
		case ATTRIBUTE_IS_NOT_OWNED:
			return new AttributeIsNotOwned1516E();
		case INFORM_ATTRIBUTE_OWNERSHIP:
			return new InformAttributeOwnership1516E();
		case NEGOTIATED_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
			return new NegotiatedAttributeOwnershipDivestiture1516E();
		case REQUEST_ATTRIBUTE_OWNERSHIP_ASSUMPTION:
			return new RequestAttributeOwnershipAssumption1516E();
		case ATTRIBUTE_OWNERSHIP_ACQUISITION:
			return new AttributeOwnershipAcquisition1516E();
		case REQUEST_ATTRIBUTE_OWNERSHIP_RELEASE:
			return new RequestAttributeOwnershipRelease1516E();
		case ATTRIBUTE_OWNERSHIP_ACQUISITION_IF_AVAILABLE:
			return new AttributeOwnershipAcquisitionIfAvailable1516E();
		case ATTRIBUTE_OWNERSHIP_ACQUISITION_NOTIFICATION:
			return new AttributeOwnershipAcquisitionNotification1516E();
		case ATTRIBUTE_OWNERSHIP_UNAVAILABLE:
			return new AttributeOwnershipUnavailable1516E();
		case UNCONDITIONAL_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
			return new UnconditionalAttributeOwnershipDivestiture1516E();
		case CANCEL_NEGOTIATED_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
			return new CancelNegotiatedAttributeOwnershipDivestiture1516E();
		case ATTRIBUTE_OWNERSHIP_RELEASE_RESPONSE:
			return new AttributeOwnershipReleaseResponse1516E();
		case CANCEL_ATTRIBUTE_OWNERSHIP_ACQUISITION:
			return new CancelAttributeOwnershipAcquisition1516E();
		case CONFIRM_ATTRIBUTE_OWNERSHIP_ACQUISITION_CANCELLATION:
			return new ConfirmAttributeOwnershipAcquisitionCancellation1516E();
		case ATTRIBUTE_OWNERSHIP_DIVESTITURE_NOTIFICATION:
			return new AttributeOwnershipDivestitureNotification1516E();
		case DDM_ASSOCIATE_REGION:
			return new DdmAssociateRegion1516E();
		case DDM_REGISTER_OBJECT:
			return new DdmRegisterObject1516E();
		case DDM_SUBSCRIBE_ATTRIBUTES:
			return new DdmSubscribeAttributes1516E();
		case DDM_UNASSOCIATE_REGION:
			return new DdmUnassociateRegion1516E();
		case DDM_UNSUBSCRIBE_ATTRIBUTES:
			return new DdmUnsubscribeAttributes1516E();
		case DDM_SUBSCRIBE_INTERACTION:
			return new DdmSubscribeInteraction1516E();
		case DDM_UNSUBSCRIBE_INTERACTION:
			return new DdmUnsubscribeInteraction1516E();
		case GET_ATTRIBUTE_SPACE_HANDLE:
			return new GetAttributeSpaceHandle1516E();
		case DDM_CREATE_REGION:
			return new DdmCreateRegion1516E();
		case GET_INTERACTION_SPACE_HANDLE:
			return new GetInteractionSpaceHandle1516E();
		case JOIN_FEDERATION_EXECUTION:
			return new JoinFederationExecution();
		case PUBLISH_OBJECT_CLASS:
			return new PublishObjectClass1516E();
		case SUBSCRIBE_OBJECT_CLASS_ATTRIBUTES:
			return new SubscribeObjectClassAttributes1516E();
		case REGISTER_OBJECT_INSTANCE:
			return new RegisterObjectInstance1516E();
		case UPDATE_ATTRIBUTE_VALUES:
			return new UpdateAttributeValues1516E();
		case REFLECT_ATTRIBUTE_VALUES:
			return new ReflectAttributeValues1516E();
		case DISCOVER_OBJECT_INSTANCE:
			return new DiscoverObjectInstance1516E();
		case DELETE_OBJECT_INSTANCE:
			return new DeleteObjectInstance1516E();
		case REMOVE_OBJECT_INSTANCE:
			return new RemoveObjectInstance1516E();
		case LOCAL_DELETE_OBJECT_INSTANCE:
			return new LocalDeleteObjectInstance1516E();
		case GET_OBJECT_CLASS_HANDLE:
			return new GetObjectClassHandle1516E();
		case GET_OBJECT_CLASS_NAME:
			return new GetObjectClassName1516E();
		case GET_ATTRIBUTE_HANDLE:
			return new GetAttributeHandle1516E();
		case GET_ATTRIBUTE_NAME:
			return new GetAttributeName1516E();
		case GET_OBJECT_CLASS:
			return new GetObjectClass1516E();
		case GET_SPACE_HANDLE:
			return new GetSpaceHandle1516E();
		case GET_SPACE_NAME:
			return new GetSpaceName1516E();
		case GET_DIMENSION_HANDLE:
			return new GetDimensionHandle1516E();
		case GET_DIMENSION_NAME:
			return new GetDimensionName1516E();
		case SEND_INTERACTION:
			return new SendInteraction1516E();
		case RECEIVE_INTERACTION:
			return new ReceiveInteraction1516E();
		case GET_INTERACTION_CLASS_HANDLE:
			return new GetInteractionClassHandle1516E();
		case GET_INTERACTION_CLASS_NAME:
			return new GetInteractionClassName1516E();
		case GET_PARAMETER_HANDLE:
			return new GetParameterHandle1516E();
		case GET_PARAMETER_NAME:
			return new GetParameterName1516E();
		case CHANGE_ATTRIBUTE_TRANSPORTATION_TYPE:
			return new ChangeAttributeTransportationType1516E();
		case CHANGE_ATTRIBUTE_ORDER_TYPE:
			return new ChangeAttributeOrderType1516E();
		case CHANGE_INTERACTION_TRANSPORTATION_TYPE:
			return new ChangeInteractionTransportationType1516E();
		case CHANGE_INTERACTION_ORDER_TYPE:
			return new ChangeInteractionOrderType1516E();
		case GET_TRANSPORTATION_HANDLE:
			return new GetTransportationHandle1516E();
		case GET_TRANSPORTATION_NAME:
			return new GetTransportationName1516E();
		case GET_ORDERING_HANDLE:
			return new GetOrderingHandle1516E();
		case GET_ORDERING_NAME:
			return new GetOrderingName1516E();
		case DDM_MODIFY_REGION:
			return new DdmModifyRegion1516E();
		case DDM_DELETE_REGION:
			return new DdmDeleteRegion1516E();
		case GET_OBJECT_INSTANCE_HANDLE:
			return new GetObjectInstanceHandle1516E();
		case GET_OBJECT_INSTANCE_NAME:
			return new GetObjectInstanceName1516E();
		case RESIGN_FEDERATION_EXECUTION:
			return new ResignFederationExecution1516E();
		case PUBLISH_INTERACTION_CLASS:
			return new PublishInteractionClass1516E();
		case UNPUBLISH_INTERACTION_CLASS:
			return new UnpublishInteractionClass1516E();
		case SUBSCRIBE_INTERACTION_CLASS:
			return new SubscribeInteractionClass1516E();
		case UNSUBSCRIBE_INTERACTION_CLASS:
			return new UnsubscribeInteractionClass1516E();
		case TURN_INTERACTIONS_ON:
			return new TurnInteractionsOn1516E();
		case TURN_INTERACTIONS_OFF:
			return new TurnInteractionsOff1516E();
		case ENABLE_TIME_REGULATION:
			return new EnableTimeRegulation1516E();
		case DISABLE_TIME_REGULATION:
			return new DisableTimeRegulation1516E();
		case ENABLE_TIME_CONSTRAINED:
			return new EnableTimeConstrained1516E();
		case DISABLE_TIME_CONSTRAINED:
			return new DisableTimeConstrained1516E();
		case ENABLE_CLASS_RELEVANCE_ADVISORY_SWITCH:
			return new EnableClassRelevanceAdvisorySwitch1516E();
		case DISABLE_CLASS_RELEVANCE_ADVISORY_SWITCH:
			return new DisableClassRelevanceAdvisorySwitch1516E();
		case ENABLE_INTERACTION_RELEVANCE_ADVISORY_SWITCH:
			return new EnableInteractionRelevanceAdvisorySwitch1516();
		case DISABLE_INTERACTION_RELEVANCE_ADVISORY_SWITCH:
			return new DisableInteractionRelevanceAdvisorySwitch1516E();
		case ENABLE_ATTRIBUTE_RELEVANCE_ADVISORY_SWITCH:
			return new EnableAttributeRelevanceAdvisorySwitch1516E();
		case DISABLE_ATTRIBUTE_RELEVANCE_ADVISORY_SWITCH:
			return new DisableAttributeRelevanceAdvisorySwitch1516E();
		case ENABLE_ATTRIBUTE_SCOPE_ADVISORY_SWITCH:
			return new EnableAttributeScopeAdvisorySwitch1516E();
		case DISABLE_ATTRIBUTE_SCOPE_ADVISORY_SWITCH:
			return new DisableAttributeScopeAdvisorySwitch1516E();
		case TICK_REQUEST:
			return new TickRequest1516E();
		case REQUEST_CLASS_ATTRIBUTE_VALUE_UPDATE:
			return new RequestClassAttributeValueUpdate1516E();
		case REQUEST_OBJECT_ATTRIBUTE_VALUE_UPDATE:
			return new RequestObjectAttributeValueUpdate1516E();
		case PROVIDE_ATTRIBUTE_VALUE_UPDATE:
			return new ProvideAttributeValueUpdate1516E();
		case MODIFY_LOOKAHEAD:
			return new ModifyLookahead1516E();
		case QUERY_LOOKAHEAD:
			return new QueryLookahead1516E();
		case FEDERATE_SAVE_BEGUN:
			return new FederateSaveBegun1516E();
		case FEDERATE_SAVE_COMPLETE:
			return new FederateSaveComplete1516E();
		case FEDERATE_SAVE_NOT_COMPLETE:
			return new FederateSaveNotComplete1516E();
		case FEDERATE_RESTORE_COMPLETE:
			return new FederateRestoreComplete1516E();
		case FEDERATE_RESTORE_NOT_COMPLETE:
			return new FederateRestoreNotComplete1516E();
		case QUERY_FEDERATE_TIME:
			return new QueryFederateTime1516E();
		case ENABLE_ASYNCHRONOUS_DELIVERY:
			return new EnableAsynchronousDelivery1516E();
		case DISABLE_ASYNCHRONOUS_DELIVERY:
			return new DisableAsynchronousDelivery1516E();
		case QUERY_LBTS:
			return new QueryLbts1516E();
		case TIME_ADVANCE_REQUEST:
			return new TimeAdvanceRequest1516E();
		case TIME_ADVANCE_REQUEST_AVAILABLE:
			return new TimeAdvanceRequestAvailable1516E();
		case NEXT_EVENT_REQUEST:
			return new NextEventRequest1516E();
		case NEXT_EVENT_REQUEST_AVAILABLE:
			return new NextEventRequestAvailable1516E();
		case FLUSH_QUEUE_REQUEST:
			return new FlushQueueRequest1516E();
		case TIME_ADVANCE_GRANT:
			return new TimeAdvanceGrant1516E();
		case TIME_REGULATION_ENABLED:
			return new TimeRegulationEnabled1516E();
		case TIME_CONSTRAINED_ENABLED:
			return new TimeConstrainedEnabled1516E();
		case QUERY_MIN_NEXT_EVENT_TIME:
			return new QueryMinNextEventTime1516E();
		case TICK_REQUEST_STOP:
			return new TickRequestStop1516E();
		case TICK_REQUEST_NEXT:
			return new TickRequestNext1516E();
		case CREATE_FEDERATION_EXECUTION_V4:
			return new CreateFederationExecution1516E();
		case JOIN_FEDERATION_EXECUTION_V4:
			return new JoinFederationExecution1516E();
		default:
			// LOGGER.severe("Received an unknown type of message: " + messageType);
			throw new UnsupportedOperationException("Message is not supported yet.");
		}
	}

	/**
	 * Receives the message The buffer is reseted after returning the message.
	 * 
	 * @param messageBuffer
	 * @return
	 * @throws IOException
	 * @throws CertiException
	 */
	public static CertiMessage readMessage(MessageBuffer messageBuffer) throws IOException, CertiException {
		messageBuffer.receiveData();

		CertiMessage message = instantiate(CertiMessageType.reverseType.get(messageBuffer.readInt())); // read first
																										// integer from
																										// header and
																										// instantiate
																										// class

		message.readMessage(messageBuffer);

		return message;
	}
}
