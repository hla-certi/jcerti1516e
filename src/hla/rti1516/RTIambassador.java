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
Memory Management Conventions for Parameters

All Java parameters, including object references, are passed by value.
Therefore there is no need to specify further conventions for primitive types.

Unless otherwise noted, reference parameters adhere to the following convention:
The referenced object is created (or acquired) by the caller. The callee must
copy during the call anything it wishes to save beyond the completion of the
call.

Unless otherwise noted, a reference returned from a method represents a new
object created by the callee. The caller is free to modify the object whose
reference is returned.


*/

/**
 * The RTI presents this interface to the federate. RTI implementer must
 * implement this.
 */

public interface RTIambassador {

////////////////////////////////////
// Federation Management Services //
////////////////////////////////////

	// 4.2
	void createFederationExecution(String federationExecutionName, java.net.URL fdd)
			throws FederationExecutionAlreadyExists, CouldNotOpenFDD, ErrorReadingFDD, RTIinternalError;

	// 4.3
	void destroyFederationExecution(String federationExecutionName)
			throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, RTIinternalError;

	// 4.4
	FederateHandle joinFederationExecution(String federateType, String federationExecutionName,
			FederateAmbassador federateReference, MobileFederateServices serviceReferences)
			throws FederateAlreadyExecutionMember, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress,
			RTIinternalError;

	// 4.5
	void resignFederationExecution(ResignAction resignAction)
			throws OwnershipAcquisitionPending, FederateOwnsAttributes, FederateNotExecutionMember, RTIinternalError;

	// 4.6
	void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag,
			FederateHandleSet synchronizationSet)
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 4.9
	void synchronizationPointAchieved(String synchronizationPointLabel) throws SynchronizationPointLabelNotAnnounced,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 4.11
	void requestFederationSave(String label)
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	void requestFederationSave(String label, LogicalTime theTime) throws LogicalTimeAlreadyPassed, InvalidLogicalTime,
			FederateUnableToUseTime, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 4.13
	void federateSaveBegun() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress, RTIinternalError;

	// 4.14
	void federateSaveComplete()
			throws FederateHasNotBegunSave, FederateNotExecutionMember, RestoreInProgress, RTIinternalError;

	void federateSaveNotComplete()
			throws FederateHasNotBegunSave, FederateNotExecutionMember, RestoreInProgress, RTIinternalError;

	// 4.16
	void queryFederationSaveStatus() throws FederateNotExecutionMember, RestoreInProgress, RTIinternalError;

	// 4.18
	void requestFederationRestore(String label)
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 4.22
	void federateRestoreComplete()
			throws RestoreNotRequested, FederateNotExecutionMember, SaveInProgress, RTIinternalError;

	void federateRestoreNotComplete()
			throws RestoreNotRequested, FederateNotExecutionMember, SaveInProgress, RTIinternalError;

	// 4.24
	void queryFederationRestoreStatus() throws FederateNotExecutionMember, SaveInProgress, RTIinternalError;

/////////////////////////////////////
// Declaration Management Services //
/////////////////////////////////////

	// 5.2
	void publishObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 5.3
	void unpublishObjectClass(ObjectClassHandle theClass) throws ObjectClassNotDefined, OwnershipAcquisitionPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	void unpublishObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, OwnershipAcquisitionPending, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 5.4
	void publishInteractionClass(InteractionClassHandle theInteraction) throws InteractionClassNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 5.5
	void unpublishInteractionClass(InteractionClassHandle theInteraction) throws InteractionClassNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 5.6
	void subscribeObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	void subscribeObjectClassAttributesPassively(ObjectClassHandle theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 5.7
	void unsubscribeObjectClass(ObjectClassHandle theClass) throws ObjectClassNotDefined, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	void unsubscribeObjectClassAttributes(ObjectClassHandle theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 5.8
	void subscribeInteractionClass(InteractionClassHandle theClass)
			throws InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	void subscribeInteractionClassPassively(InteractionClassHandle theClass)
			throws InteractionClassNotDefined, FederateServiceInvocationsAreBeingReportedViaMOM,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 5.9
	void unsubscribeInteractionClass(InteractionClassHandle theClass) throws InteractionClassNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

////////////////////////////////
// Object Management Services //
////////////////////////////////

	// 6.2
	void reserveObjectInstanceName(String theObjectName)
			throws IllegalName, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.4
	ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass) throws ObjectClassNotDefined,
			ObjectClassNotPublished, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	ObjectInstanceHandle registerObjectInstance(ObjectClassHandle theClass, String theObjectName)
			throws ObjectClassNotDefined, ObjectClassNotPublished, ObjectInstanceNameNotReserved,
			ObjectInstanceNameInUse, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.6
	void updateAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag) throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	MessageRetractionReturn updateAttributeValues(ObjectInstanceHandle theObject, AttributeHandleValueMap theAttributes,
			byte[] userSuppliedTag, LogicalTime theTime)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidLogicalTime,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.8
	void sendInteraction(InteractionClassHandle theInteraction, ParameterHandleValueMap theParameters,
			byte[] userSuppliedTag)
			throws InteractionClassNotPublished, InteractionClassNotDefined, InteractionParameterNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	MessageRetractionReturn sendInteraction(InteractionClassHandle theInteraction,
			ParameterHandleValueMap theParameters, byte[] userSuppliedTag, LogicalTime theTime)
			throws InteractionClassNotPublished, InteractionClassNotDefined, InteractionParameterNotDefined,
			InvalidLogicalTime, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.10
	void deleteObjectInstance(ObjectInstanceHandle objectHandle, byte[] userSuppliedTag) throws DeletePrivilegeNotHeld,
			ObjectInstanceNotKnown, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	MessageRetractionReturn deleteObjectInstance(ObjectInstanceHandle objectHandle, byte[] userSuppliedTag,
			LogicalTime theTime) throws DeletePrivilegeNotHeld, ObjectInstanceNotKnown, InvalidLogicalTime,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.12
	void localDeleteObjectInstance(ObjectInstanceHandle objectHandle)
			throws ObjectInstanceNotKnown, FederateOwnsAttributes, OwnershipAcquisitionPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.13
	void changeAttributeTransportationType(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			TransportationType theType) throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 6.14
	void changeInteractionTransportationType(InteractionClassHandle theClass, TransportationType theType)
			throws InteractionClassNotDefined, InteractionClassNotPublished, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 6.17
	void requestAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag) throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	void requestAttributeValueUpdate(ObjectClassHandle theClass, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag) throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

///////////////////////////////////
// Ownership Management Services //
///////////////////////////////////

	// 7.2
	void unconditionalAttributeOwnershipDivestiture(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 7.3
	void negotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeAlreadyBeingDivested,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 7.6
	void confirmDivestiture(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeDivestitureWasNotRequested,
			NoAcquisitionPending, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 7.8
	void attributeOwnershipAcquisition(ObjectInstanceHandle theObject, AttributeHandleSet desiredAttributes,
			byte[] userSuppliedTag)
			throws ObjectInstanceNotKnown, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			FederateOwnsAttributes, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 7.9
	void attributeOwnershipAcquisitionIfAvailable(ObjectInstanceHandle theObject, AttributeHandleSet desiredAttributes)
			throws ObjectInstanceNotKnown, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			FederateOwnsAttributes, AttributeAlreadyBeingAcquired, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 7.12
	AttributeHandleSet attributeOwnershipDivestitureIfWanted(ObjectInstanceHandle theObject,
			AttributeHandleSet theAttributes) throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 7.13
	void cancelNegotiatedAttributeOwnershipDivestiture(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeDivestitureWasNotRequested,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 7.14
	void cancelAttributeOwnershipAcquisition(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeAlreadyOwned,
			AttributeAcquisitionWasNotRequested, FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError;

	// 7.16
	void queryAttributeOwnership(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 7.18
	boolean isAttributeOwnedByFederate(ObjectInstanceHandle theObject, AttributeHandle theAttribute)
			throws ObjectInstanceNotKnown, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

//////////////////////////////
// Time Management Services //
//////////////////////////////

	// 8.2
	void enableTimeRegulation(LogicalTimeInterval theLookahead) throws TimeRegulationAlreadyEnabled, InvalidLookahead,
			InTimeAdvancingState, RequestForTimeRegulationPending, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 8.4
	void disableTimeRegulation() throws TimeRegulationIsNotEnabled, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 8.5
	void enableTimeConstrained()
			throws TimeConstrainedAlreadyEnabled, InTimeAdvancingState, RequestForTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.7
	void disableTimeConstrained() throws TimeConstrainedIsNotEnabled, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 8.8
	void timeAdvanceRequest(LogicalTime theTime) throws InvalidLogicalTime, LogicalTimeAlreadyPassed,
			InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.9
	void timeAdvanceRequestAvailable(LogicalTime theTime) throws InvalidLogicalTime, LogicalTimeAlreadyPassed,
			InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.10
	void nextMessageRequest(LogicalTime theTime) throws InvalidLogicalTime, LogicalTimeAlreadyPassed,
			InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.11
	void nextMessageRequestAvailable(LogicalTime theTime) throws InvalidLogicalTime, LogicalTimeAlreadyPassed,
			InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.12
	void flushQueueRequest(LogicalTime theTime) throws InvalidLogicalTime, LogicalTimeAlreadyPassed,
			InTimeAdvancingState, RequestForTimeRegulationPending, RequestForTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.14
	void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.15
	void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.16
	TimeQueryReturn queryGALT() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.17
	LogicalTime queryLogicalTime()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.18
	TimeQueryReturn queryLITS() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.19
	void modifyLookahead(LogicalTimeInterval theLookahead) throws TimeRegulationIsNotEnabled, InvalidLookahead,
			InTimeAdvancingState, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.20
	LogicalTimeInterval queryLookahead() throws TimeRegulationIsNotEnabled, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 8.21
	void retract(MessageRetractionHandle theHandle)
			throws InvalidMessageRetractionHandle, TimeRegulationIsNotEnabled, MessageCanNoLongerBeRetracted,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.23
	void changeAttributeOrderType(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes, OrderType theType)
			throws ObjectInstanceNotKnown, AttributeNotDefined, AttributeNotOwned, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 8.24
	void changeInteractionOrderType(InteractionClassHandle theClass, OrderType theType)
			throws InteractionClassNotDefined, InteractionClassNotPublished, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

//////////////////////////////////
// Data Distribution Management //
//////////////////////////////////

	// 9.2
	RegionHandle createRegion(DimensionHandleSet dimensions) throws InvalidDimensionHandle, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.3
	void commitRegionModifications(RegionHandleSet regions) throws InvalidRegion, RegionNotCreatedByThisFederate,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.4
	void deleteRegion(RegionHandle theRegion)
			throws InvalidRegion, RegionNotCreatedByThisFederate, RegionInUseForUpdateOrSubscription,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.5
	ObjectInstanceHandle registerObjectInstanceWithRegions(ObjectClassHandle theClass,
			AttributeSetRegionSetPairList attributesAndRegions) throws ObjectClassNotDefined, ObjectClassNotPublished,
			AttributeNotDefined, AttributeNotPublished, InvalidRegion, RegionNotCreatedByThisFederate,
			InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	ObjectInstanceHandle registerObjectInstanceWithRegions(ObjectClassHandle theClass,
			AttributeSetRegionSetPairList attributesAndRegions, String theObject)
			throws ObjectClassNotDefined, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			InvalidRegion, RegionNotCreatedByThisFederate, InvalidRegionContext, ObjectInstanceNameNotReserved,
			ObjectInstanceNameInUse, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.6
	void associateRegionsForUpdates(ObjectInstanceHandle theObject, AttributeSetRegionSetPairList attributesAndRegions)
			throws ObjectInstanceNotKnown, AttributeNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.7
	void unassociateRegionsForUpdates(ObjectInstanceHandle theObject,
			AttributeSetRegionSetPairList attributesAndRegions)
			throws ObjectInstanceNotKnown, AttributeNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.8
	void subscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
			AttributeSetRegionSetPairList attributesAndRegions)
			throws ObjectClassNotDefined, AttributeNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	void subscribeObjectClassAttributesPassivelyWithRegions(ObjectClassHandle theClass,
			AttributeSetRegionSetPairList attributesAndRegions)
			throws ObjectClassNotDefined, AttributeNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.9
	void unsubscribeObjectClassAttributesWithRegions(ObjectClassHandle theClass,
			AttributeSetRegionSetPairList attributesAndRegions)
			throws ObjectClassNotDefined, AttributeNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.10
	void subscribeInteractionClassWithRegions(InteractionClassHandle theClass, RegionHandleSet regions)
			throws InteractionClassNotDefined, InvalidRegion, RegionNotCreatedByThisFederate, InvalidRegionContext,
			FederateServiceInvocationsAreBeingReportedViaMOM, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	void subscribeInteractionClassPassivelyWithRegions(InteractionClassHandle theClass, RegionHandleSet regions)
			throws InteractionClassNotDefined, InvalidRegion, RegionNotCreatedByThisFederate, InvalidRegionContext,
			FederateServiceInvocationsAreBeingReportedViaMOM, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 9.11
	void unsubscribeInteractionClassWithRegions(InteractionClassHandle theClass, RegionHandleSet regions)
			throws InteractionClassNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.12
	void sendInteractionWithRegions(InteractionClassHandle theInteraction, ParameterHandleValueMap theParameters,
			RegionHandleSet regions, byte[] userSuppliedTag) throws InteractionClassNotDefined,
			InteractionClassNotPublished, InteractionParameterNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	MessageRetractionReturn sendInteractionWithRegions(InteractionClassHandle theInteraction,
			ParameterHandleValueMap theParameters, RegionHandleSet regions, byte[] userSuppliedTag, LogicalTime theTime)
			throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined,
			InvalidRegion, RegionNotCreatedByThisFederate, InvalidRegionContext, InvalidLogicalTime,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 9.13
	void requestAttributeValueUpdateWithRegions(ObjectClassHandle theClass,
			AttributeSetRegionSetPairList attributesAndRegions, byte[] userSuppliedTag)
			throws ObjectClassNotDefined, AttributeNotDefined, InvalidRegion, RegionNotCreatedByThisFederate,
			InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

//////////////////////////
// RTI Support Services //
//////////////////////////

	// 10.2
	ObjectClassHandle getObjectClassHandle(String theName)
			throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.3
	String getObjectClassName(ObjectClassHandle theHandle)
			throws InvalidObjectClassHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.4
	AttributeHandle getAttributeHandle(ObjectClassHandle whichClass, String theName)
			throws InvalidObjectClassHandle, NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.5
	String getAttributeName(ObjectClassHandle whichClass, AttributeHandle theHandle) throws InvalidObjectClassHandle,
			InvalidAttributeHandle, AttributeNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.6
	InteractionClassHandle getInteractionClassHandle(String theName)
			throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.7
	String getInteractionClassName(InteractionClassHandle theHandle)
			throws InvalidInteractionClassHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.8
	ParameterHandle getParameterHandle(InteractionClassHandle whichClass, String theName)
			throws InvalidInteractionClassHandle, NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.9
	String getParameterName(InteractionClassHandle whichClass, ParameterHandle theHandle)
			throws InvalidInteractionClassHandle, InvalidParameterHandle, InteractionParameterNotDefined,
			FederateNotExecutionMember, RTIinternalError;

	// 10.10
	ObjectInstanceHandle getObjectInstanceHandle(String theName)
			throws ObjectInstanceNotKnown, FederateNotExecutionMember, RTIinternalError;

	// 10.11
	String getObjectInstanceName(ObjectInstanceHandle theHandle)
			throws ObjectInstanceNotKnown, FederateNotExecutionMember, RTIinternalError;

	// 10.12
	DimensionHandle getDimensionHandle(String theName)
			throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.13
	String getDimensionName(DimensionHandle theHandle)
			throws InvalidDimensionHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.14
	long getDimensionUpperBound(DimensionHandle theHandle)
			throws InvalidDimensionHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.15
	DimensionHandleSet getAvailableDimensionsForClassAttribute(ObjectClassHandle whichClass, AttributeHandle theHandle)
			throws InvalidObjectClassHandle, InvalidAttributeHandle, AttributeNotDefined, FederateNotExecutionMember,
			RTIinternalError;

	// 10.16
	ObjectClassHandle getKnownObjectClassHandle(ObjectInstanceHandle theObject)
			throws ObjectInstanceNotKnown, FederateNotExecutionMember, RTIinternalError;

	// 10.17
	DimensionHandleSet getAvailableDimensionsForInteractionClass(InteractionClassHandle theHandle)
			throws InvalidInteractionClassHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.18
	TransportationType getTransportationType(String theName)
			throws InvalidTransportationName, FederateNotExecutionMember, RTIinternalError;

	// 10.19
	String getTransportationName(TransportationType theType)
			throws InvalidTransportationType, FederateNotExecutionMember, RTIinternalError;

	// 10.20
	OrderType getOrderType(String theName) throws InvalidOrderName, FederateNotExecutionMember, RTIinternalError;

	// 10.21
	String getOrderName(OrderType theType) throws InvalidOrderType, FederateNotExecutionMember, RTIinternalError;

	// 10.22
	void enableObjectClassRelevanceAdvisorySwitch() throws FederateNotExecutionMember,
			ObjectClassRelevanceAdvisorySwitchIsOn, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.23
	void disableObjectClassRelevanceAdvisorySwitch() throws ObjectClassRelevanceAdvisorySwitchIsOff,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.24
	void enableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOn,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.25
	void disableAttributeRelevanceAdvisorySwitch() throws AttributeRelevanceAdvisorySwitchIsOff,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.26
	void enableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOn, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.27
	void disableAttributeScopeAdvisorySwitch() throws AttributeScopeAdvisorySwitchIsOff, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.28
	void enableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOn,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.29
	void disableInteractionRelevanceAdvisorySwitch() throws InteractionRelevanceAdvisorySwitchIsOff,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.30
	DimensionHandleSet getDimensionHandleSet(RegionHandle region)
			throws InvalidRegion, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.31
	RangeBounds getRangeBounds(RegionHandle region, DimensionHandle dimension)
			throws InvalidRegion, RegionDoesNotContainSpecifiedDimension, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError;

	// 10.32
	void setRangeBounds(RegionHandle region, DimensionHandle dimension, RangeBounds bounds)
			throws InvalidRegion, RegionNotCreatedByThisFederate, RegionDoesNotContainSpecifiedDimension,
			InvalidRangeBound, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.33
	long normalizeFederateHandle(FederateHandle federateHandle)
			throws InvalidFederateHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.34
	long normalizeServiceGroup(ServiceGroup group)
			throws InvalidServiceGroup, FederateNotExecutionMember, RTIinternalError;

	// 10.37
	boolean evokeCallback(double seconds) throws FederateNotExecutionMember, RTIinternalError;

	// 10.38
	boolean evokeMultipleCallbacks(double minimumTime, double maximumTime)
			throws FederateNotExecutionMember, RTIinternalError;

	// 10.39
	void enableCallbacks() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.40
	void disableCallbacks() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// API-specific services
	AttributeHandleFactory getAttributeHandleFactory() throws FederateNotExecutionMember;

	AttributeHandleSetFactory getAttributeHandleSetFactory() throws FederateNotExecutionMember;

	AttributeHandleValueMapFactory getAttributeHandleValueMapFactory() throws FederateNotExecutionMember;

	AttributeSetRegionSetPairListFactory getAttributeSetRegionSetPairListFactory() throws FederateNotExecutionMember;

	DimensionHandleFactory getDimensionHandleFactory() throws FederateNotExecutionMember;

	DimensionHandleSetFactory getDimensionHandleSetFactory() throws FederateNotExecutionMember;

	FederateHandleFactory getFederateHandleFactory() throws FederateNotExecutionMember;

	FederateHandleSetFactory getFederateHandleSetFactory() throws FederateNotExecutionMember;

	InteractionClassHandleFactory getInteractionClassHandleFactory() throws FederateNotExecutionMember;

	ObjectClassHandleFactory getObjectClassHandleFactory() throws FederateNotExecutionMember;

	ObjectInstanceHandleFactory getObjectInstanceHandleFactory() throws FederateNotExecutionMember;

	ParameterHandleFactory getParameterHandleFactory() throws FederateNotExecutionMember;

	ParameterHandleValueMapFactory getParameterHandleValueMapFactory() throws FederateNotExecutionMember;

	RegionHandleSetFactory getRegionHandleSetFactory() throws FederateNotExecutionMember;

	String getHLAversion();
}
//end RTIambassador

//File: RTIexception.java
