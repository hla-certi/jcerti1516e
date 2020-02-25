// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi.communication;

import java.io.IOException;

import certi.communication.messages.*;

public class MessageFactory {

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
			return new OpenConnexion();
		case CLOSE_CONNEXION:
			return new CloseConnexion();
		case CREATE_FEDERATION_EXECUTION:
			return new CreateFederationExecution();
		case DESTROY_FEDERATION_EXECUTION:
			return new DestroyFederationExecution();
		case REGISTER_FEDERATION_SYNCHRONIZATION_POINT:
			return new RegisterFederationSynchronizationPoint();
		case ANNOUNCE_SYNCHRONIZATION_POINT:
			return new AnnounceSynchronizationPoint();
		case REQUEST_FEDERATION_RESTORE_FAILED:
			return new RequestFederationRestoreFailed();
		case SYNCHRONIZATION_POINT_REGISTRATION_FAILED:
			return new SynchronizationPointRegistrationFailed();
		case SYNCHRONIZATION_POINT_REGISTRATION_SUCCEEDED:
			return new SynchronizationPointRegistrationSucceeded();
		case SYNCHRONIZATION_POINT_ACHIEVED:
			return new SynchronizationPointAchieved();
		case FEDERATION_SYNCHRONIZED:
			return new FederationSynchronized();
		case REQUEST_FEDERATION_RESTORE:
			return new RequestFederationRestore();
		case REQUEST_FEDERATION_RESTORE_SUCCEEDED:
			return new RequestFederationRestoreSucceeded();
		case INITIATE_FEDERATE_RESTORE:
			return new InitiateFederateRestore();
		case INITIATE_FEDERATE_SAVE:
			return new InitiateFederateSave();
		case REQUEST_FEDERATION_SAVE:
			return new RequestFederationSave();
		case UNPUBLISH_OBJECT_CLASS:
			return new UnpublishObjectClass();
		case UNSUBSCRIBE_OBJECT_CLASS:
			return new UnsubscribeObjectClass();
		case START_REGISTRATION_FOR_OBJECT_CLASS:
			return new StartRegistrationForObjectClass();
		case STOP_REGISTRATION_FOR_OBJECT_CLASS:
			return new StopRegistrationForObjectClass();
		case IS_ATTRIBUTE_OWNED_BY_FEDERATE:
			return new IsAttributeOwnedByFederate();
		case QUERY_ATTRIBUTE_OWNERSHIP:
			return new QueryAttributeOwnership();
		case ATTRIBUTE_IS_NOT_OWNED:
			return new AttributeIsNotOwned();
		case INFORM_ATTRIBUTE_OWNERSHIP:
			return new InformAttributeOwnership();
		case NEGOTIATED_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
			return new NegotiatedAttributeOwnershipDivestiture();
		case REQUEST_ATTRIBUTE_OWNERSHIP_ASSUMPTION:
			return new RequestAttributeOwnershipAssumption();
		case ATTRIBUTE_OWNERSHIP_ACQUISITION:
			return new AttributeOwnershipAcquisition();
		case REQUEST_ATTRIBUTE_OWNERSHIP_RELEASE:
			return new RequestAttributeOwnershipRelease();
		case ATTRIBUTE_OWNERSHIP_ACQUISITION_IF_AVAILABLE:
			return new AttributeOwnershipAcquisitionIfAvailable();
		case ATTRIBUTE_OWNERSHIP_ACQUISITION_NOTIFICATION:
			return new AttributeOwnershipAcquisitionNotification();
		case ATTRIBUTE_OWNERSHIP_UNAVAILABLE:
			return new AttributeOwnershipUnavailable();
		case UNCONDITIONAL_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
			return new UnconditionalAttributeOwnershipDivestiture();
		case CANCEL_NEGOTIATED_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
			return new CancelNegotiatedAttributeOwnershipDivestiture();
		case ATTRIBUTE_OWNERSHIP_RELEASE_RESPONSE:
			return new AttributeOwnershipReleaseResponse();
		case CANCEL_ATTRIBUTE_OWNERSHIP_ACQUISITION:
			return new CancelAttributeOwnershipAcquisition();
		case CONFIRM_ATTRIBUTE_OWNERSHIP_ACQUISITION_CANCELLATION:
			return new ConfirmAttributeOwnershipAcquisitionCancellation();
		case ATTRIBUTE_OWNERSHIP_DIVESTITURE_NOTIFICATION:
			return new AttributeOwnershipDivestitureNotification();
		case DDM_ASSOCIATE_REGION:
			return new DdmAssociateRegion();
		case DDM_REGISTER_OBJECT:
			return new DdmRegisterObject();
		case DDM_SUBSCRIBE_ATTRIBUTES:
			return new DdmSubscribeAttributes();
		case DDM_UNASSOCIATE_REGION:
			return new DdmUnassociateRegion();
		case DDM_UNSUBSCRIBE_ATTRIBUTES:
			return new DdmUnsubscribeAttributes();
		case DDM_SUBSCRIBE_INTERACTION:
			return new DdmSubscribeInteraction();
		case DDM_UNSUBSCRIBE_INTERACTION:
			return new DdmUnsubscribeInteraction();
		case GET_ATTRIBUTE_SPACE_HANDLE:
			return new GetAttributeSpaceHandle();
		case DDM_CREATE_REGION:
			return new DdmCreateRegion();
		case GET_INTERACTION_SPACE_HANDLE:
			return new GetInteractionSpaceHandle();
		case JOIN_FEDERATION_EXECUTION:
			return new JoinFederationExecution();
		case PUBLISH_OBJECT_CLASS:
			return new PublishObjectClass();
		case SUBSCRIBE_OBJECT_CLASS_ATTRIBUTES:
			return new SubscribeObjectClassAttributes();
		case REGISTER_OBJECT_INSTANCE:
			return new RegisterObjectInstance();
		case UPDATE_ATTRIBUTE_VALUES:
			return new UpdateAttributeValues();
		case REFLECT_ATTRIBUTE_VALUES:
			return new ReflectAttributeValues();
		case DISCOVER_OBJECT_INSTANCE:
			return new DiscoverObjectInstance();
		case DELETE_OBJECT_INSTANCE:
			return new DeleteObjectInstance();
		case REMOVE_OBJECT_INSTANCE:
			return new RemoveObjectInstance();
		case LOCAL_DELETE_OBJECT_INSTANCE:
			return new LocalDeleteObjectInstance();
		case GET_OBJECT_CLASS_HANDLE:
			return new GetObjectClassHandle();
		case GET_OBJECT_CLASS_NAME:
			return new GetObjectClassName();
		case GET_ATTRIBUTE_HANDLE:
			return new GetAttributeHandle();
		case GET_ATTRIBUTE_NAME:
			return new GetAttributeName();
		case GET_OBJECT_CLASS:
			return new GetObjectClass();
		case GET_SPACE_HANDLE:
			return new GetSpaceHandle();
		case GET_SPACE_NAME:
			return new GetSpaceName();
		case GET_DIMENSION_HANDLE:
			return new GetDimensionHandle();
		case GET_DIMENSION_NAME:
			return new GetDimensionName();
		case SEND_INTERACTION:
			return new SendInteraction();
		case RECEIVE_INTERACTION:
			return new ReceiveInteraction();
		case GET_INTERACTION_CLASS_HANDLE:
			return new GetInteractionClassHandle();
		case GET_INTERACTION_CLASS_NAME:
			return new GetInteractionClassName();
		case GET_PARAMETER_HANDLE:
			return new GetParameterHandle();
		case GET_PARAMETER_NAME:
			return new GetParameterName();
		case CHANGE_ATTRIBUTE_TRANSPORTATION_TYPE:
			return new ChangeAttributeTransportationType();
		case CHANGE_ATTRIBUTE_ORDER_TYPE:
			return new ChangeAttributeOrderType();
		case CHANGE_INTERACTION_TRANSPORTATION_TYPE:
			return new ChangeInteractionTransportationType();
		case CHANGE_INTERACTION_ORDER_TYPE:
			return new ChangeInteractionOrderType();
		case GET_TRANSPORTATION_HANDLE:
			return new GetTransportationHandle();
		case GET_TRANSPORTATION_NAME:
			return new GetTransportationName();
		case GET_ORDERING_HANDLE:
			return new GetOrderingHandle();
		case GET_ORDERING_NAME:
			return new GetOrderingName();
		case DDM_MODIFY_REGION:
			return new DdmModifyRegion();
		case DDM_DELETE_REGION:
			return new DdmDeleteRegion();
		case GET_OBJECT_INSTANCE_HANDLE:
			return new GetObjectInstanceHandle();
		case GET_OBJECT_INSTANCE_NAME:
			return new GetObjectInstanceName();
		case RESIGN_FEDERATION_EXECUTION:
			return new ResignFederationExecution();
		case PUBLISH_INTERACTION_CLASS:
			return new PublishInteractionClass();
		case UNPUBLISH_INTERACTION_CLASS:
			return new UnpublishInteractionClass();
		case SUBSCRIBE_INTERACTION_CLASS:
			return new SubscribeInteractionClass();
		case UNSUBSCRIBE_INTERACTION_CLASS:
			return new UnsubscribeInteractionClass();
		case TURN_INTERACTIONS_ON:
			return new TurnInteractionsOn();
		case TURN_INTERACTIONS_OFF:
			return new TurnInteractionsOff();
		case ENABLE_TIME_REGULATION:
			return new EnableTimeRegulation();
		case DISABLE_TIME_REGULATION:
			return new DisableTimeRegulation();
		case ENABLE_TIME_CONSTRAINED:
			return new EnableTimeConstrained();
		case DISABLE_TIME_CONSTRAINED:
			return new DisableTimeConstrained();
		case ENABLE_CLASS_RELEVANCE_ADVISORY_SWITCH:
			return new EnableClassRelevanceAdvisorySwitch();
		case DISABLE_CLASS_RELEVANCE_ADVISORY_SWITCH:
			return new DisableClassRelevanceAdvisorySwitch();
		case ENABLE_INTERACTION_RELEVANCE_ADVISORY_SWITCH:
			return new EnableInteractionRelevanceAdvisorySwitch();
		case DISABLE_INTERACTION_RELEVANCE_ADVISORY_SWITCH:
			return new DisableInteractionRelevanceAdvisorySwitch();
		case ENABLE_ATTRIBUTE_RELEVANCE_ADVISORY_SWITCH:
			return new EnableAttributeRelevanceAdvisorySwitch();
		case DISABLE_ATTRIBUTE_RELEVANCE_ADVISORY_SWITCH:
			return new DisableAttributeRelevanceAdvisorySwitch();
		case ENABLE_ATTRIBUTE_SCOPE_ADVISORY_SWITCH:
			return new EnableAttributeScopeAdvisorySwitch();
		case DISABLE_ATTRIBUTE_SCOPE_ADVISORY_SWITCH:
			return new DisableAttributeScopeAdvisorySwitch();
		case TICK_REQUEST:
			return new TickRequest();
		case REQUEST_CLASS_ATTRIBUTE_VALUE_UPDATE:
			return new RequestClassAttributeValueUpdate();
		case REQUEST_OBJECT_ATTRIBUTE_VALUE_UPDATE:
			return new RequestObjectAttributeValueUpdate();
		case PROVIDE_ATTRIBUTE_VALUE_UPDATE:
			return new ProvideAttributeValueUpdate();
		case MODIFY_LOOKAHEAD:
			return new ModifyLookahead();
		case QUERY_LOOKAHEAD:
			return new QueryLookahead();
		case FEDERATE_SAVE_BEGUN:
			return new FederateSaveBegun();
		case FEDERATE_SAVE_COMPLETE:
			return new FederateSaveComplete();
		case FEDERATE_SAVE_NOT_COMPLETE:
			return new FederateSaveNotComplete();
		case FEDERATE_RESTORE_COMPLETE:
			return new FederateRestoreComplete();
		case FEDERATE_RESTORE_NOT_COMPLETE:
			return new FederateRestoreNotComplete();
		case QUERY_FEDERATE_TIME:
			return new QueryFederateTime();
		case ENABLE_ASYNCHRONOUS_DELIVERY:
			return new EnableAsynchronousDelivery();
		case DISABLE_ASYNCHRONOUS_DELIVERY:
			return new DisableAsynchronousDelivery();
		case QUERY_LBTS:
			return new QueryLbts();
		case TIME_ADVANCE_REQUEST:
			return new TimeAdvanceRequest();
		case TIME_ADVANCE_REQUEST_AVAILABLE:
			return new TimeAdvanceRequestAvailable();
		case NEXT_EVENT_REQUEST:
			return new NextEventRequest();
		case NEXT_EVENT_REQUEST_AVAILABLE:
			return new NextEventRequestAvailable();
		case FLUSH_QUEUE_REQUEST:
			return new FlushQueueRequest();
		case TIME_ADVANCE_GRANT:
			return new TimeAdvanceGrant();
		case TIME_REGULATION_ENABLED:
			return new TimeRegulationEnabled();
		case TIME_CONSTRAINED_ENABLED:
			return new TimeConstrainedEnabled();
		case QUERY_MIN_NEXT_EVENT_TIME:
			return new QueryMinNextEventTime();
		case TICK_REQUEST_STOP:
			return new TickRequestStop();
		case TICK_REQUEST_NEXT:
			return new TickRequestNext();
		default:
			// LOGGER.severe("Received an unknown type of message: " + messageType);
			throw new UnsupportedOperationException("Message is not supported yet.");
		}
	}

	/**
	 * Receives the message and then parse it. The buffer is reset after returning
	 * the message.
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
