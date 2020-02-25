/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

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
	void createFederationExecution(String executionName, java.net.URL fed) throws FederationExecutionAlreadyExists,
			CouldNotOpenFED, ErrorReadingFED, RTIinternalError, ConcurrentAccessAttempted;

	// 4.3
	void destroyFederationExecution(String executionName) throws FederatesCurrentlyJoined,
			FederationExecutionDoesNotExist, RTIinternalError, ConcurrentAccessAttempted;

	// 4.4
	int joinFederationExecution(String federateType, String federationExecutionName,
			FederateAmbassador federateReference)
			throws FederateAlreadyExecutionMember, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.4
	int joinFederationExecution(String federateType, String federationExecutionName,
			FederateAmbassador federateReference, MobileFederateServices serviceReferences)
			throws FederateAlreadyExecutionMember, FederationExecutionDoesNotExist, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.5
	void resignFederationExecution(int resignAction) throws FederateOwnsAttributes, FederateNotExecutionMember,
			InvalidResignAction, RTIinternalError, ConcurrentAccessAttempted;

	// 4.6
	void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError,
			ConcurrentAccessAttempted;

	// 4.6
	void registerFederationSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag,
			FederateHandleSet synchronizationSet) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.9
	void synchronizationPointAchieved(String synchronizationPointLabel) throws SynchronizationLabelNotAnnounced,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 4.11
	void requestFederationSave(String label, LogicalTime theTime)
			throws FederationTimeAlreadyPassed, InvalidFederationTime, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 4.11
	void requestFederationSave(String label) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.13
	void federateSaveBegun() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress, RTIinternalError,
			ConcurrentAccessAttempted;

	// 4.14
	void federateSaveComplete() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.14
	void federateSaveNotComplete() throws SaveNotInitiated, FederateNotExecutionMember, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.16
	void requestFederationRestore(String label) throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.20
	void federateRestoreComplete() throws RestoreNotRequested, FederateNotExecutionMember, SaveInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 4.20
	void federateRestoreNotComplete() throws RestoreNotRequested, FederateNotExecutionMember, SaveInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

/////////////////////////////////////
// Declaration Management Services //
/////////////////////////////////////

	// 5.2
	void publishObjectClass(int theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, OwnershipAcquisitionPending, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.3
	void unpublishObjectClass(int theClass)
			throws ObjectClassNotDefined, ObjectClassNotPublished, OwnershipAcquisitionPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.4
	void publishInteractionClass(int theInteraction) throws InteractionClassNotDefined, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.5
	void unpublishInteractionClass(int theInteraction) throws InteractionClassNotDefined, InteractionClassNotPublished,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.6
	void subscribeObjectClassAttributes(int theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.6
	void subscribeObjectClassAttributesPassively(int theClass, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.7
	void unsubscribeObjectClass(int theClass) throws ObjectClassNotDefined, ObjectClassNotSubscribed,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.8
	void subscribeInteractionClass(int theClass) throws InteractionClassNotDefined, FederateNotExecutionMember,
			FederateLoggingServiceCalls, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.8
	void subscribeInteractionClassPassively(int theClass) throws InteractionClassNotDefined, FederateNotExecutionMember,
			FederateLoggingServiceCalls, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 5.9
	void unsubscribeInteractionClass(int theClass) throws InteractionClassNotDefined, InteractionClassNotSubscribed,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

////////////////////////////////
// Object Management Services //
////////////////////////////////

	// 6.2
	int registerObjectInstance(int theClass) throws ObjectClassNotDefined, ObjectClassNotPublished,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.2
	int registerObjectInstance(int theClass, String theObject)
			throws ObjectClassNotDefined, ObjectClassNotPublished, ObjectAlreadyRegistered, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.4
	void updateAttributeValues(int theObject, SuppliedAttributes theAttributes, byte[] userSuppliedTag)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.4
	EventRetractionHandle updateAttributeValues(int theObject, SuppliedAttributes theAttributes, byte[] userSuppliedTag,
			LogicalTime theTime) throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidFederationTime,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.6
	void sendInteraction(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag)
			throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.6
	EventRetractionHandle sendInteraction(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag,
			LogicalTime theTime) throws InteractionClassNotDefined, InteractionClassNotPublished,
			InteractionParameterNotDefined, InvalidFederationTime, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.8
	void deleteObjectInstance(int ObjectHandle, byte[] userSuppliedTag) throws ObjectNotKnown, DeletePrivilegeNotHeld,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.8
	EventRetractionHandle deleteObjectInstance(int ObjectHandle, byte[] userSuppliedTag, LogicalTime theTime)
			throws ObjectNotKnown, DeletePrivilegeNotHeld, InvalidFederationTime, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.10
	void localDeleteObjectInstance(int ObjectHandle) throws ObjectNotKnown, FederateOwnsAttributes,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.11
	void changeAttributeTransportationType(int theObject, AttributeHandleSet theAttributes, int theType)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidTransportationHandle,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.12
	void changeInteractionTransportationType(int theClass, int theType)
			throws InteractionClassNotDefined, InteractionClassNotPublished, InvalidTransportationHandle,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 6.15
	void requestObjectAttributeValueUpdate(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 6.15
	void requestClassAttributeValueUpdate(int theClass, AttributeHandleSet theAttributes)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

///////////////////////////////////
// Ownership Management Services //
///////////////////////////////////

	// 7.2
	void unconditionalAttributeOwnershipDivestiture(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.3
	void negotiatedAttributeOwnershipDivestiture(int theObject, AttributeHandleSet theAttributes,
			byte[] userSuppliedTag)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeAlreadyBeingDivested,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.7
	void attributeOwnershipAcquisition(int theObject, AttributeHandleSet desiredAttributes, byte[] userSuppliedTag)
			throws ObjectNotKnown, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			FederateOwnsAttributes, FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError,
			ConcurrentAccessAttempted;

	// 7.8
	void attributeOwnershipAcquisitionIfAvailable(int theObject, AttributeHandleSet desiredAttributes)
			throws ObjectNotKnown, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			FederateOwnsAttributes, AttributeAlreadyBeingAcquired, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.11
	AttributeHandleSet attributeOwnershipReleaseResponse(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, FederateWasNotAskedToReleaseAttribute,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.12
	void cancelNegotiatedAttributeOwnershipDivestiture(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, AttributeDivestitureWasNotRequested,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.13
	void cancelAttributeOwnershipAcquisition(int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotDefined, AttributeAlreadyOwned, AttributeAcquisitionWasNotRequested,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.15
	void queryAttributeOwnership(int theObject, int theAttribute) throws ObjectNotKnown, AttributeNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 7.17
	boolean isAttributeOwnedByFederate(int theObject, int theAttribute) throws ObjectNotKnown, AttributeNotDefined,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

//////////////////////////////
// Time Management Services //
//////////////////////////////

	// 8.2
	void enableTimeRegulation(LogicalTime theFederateTime, LogicalTimeInterval theLookahead)
			throws TimeRegulationAlreadyEnabled, EnableTimeRegulationPending, TimeAdvanceAlreadyInProgress,
			InvalidFederationTime, InvalidLookahead, FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 8.4
	void disableTimeRegulation() throws TimeRegulationWasNotEnabled, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.5
	void enableTimeConstrained()
			throws TimeConstrainedAlreadyEnabled, EnableTimeConstrainedPending, TimeAdvanceAlreadyInProgress,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.7
	void disableTimeConstrained() throws TimeConstrainedWasNotEnabled, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.8
	void timeAdvanceRequest(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed,
			TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.9
	void timeAdvanceRequestAvailable(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed,
			TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.10
	void nextEventRequest(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed,
			TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.11
	void nextEventRequestAvailable(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed,
			TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.12
	void flushQueueRequest(LogicalTime theTime) throws InvalidFederationTime, FederationTimeAlreadyPassed,
			TimeAdvanceAlreadyInProgress, EnableTimeRegulationPending, EnableTimeConstrainedPending,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.14
	void enableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyEnabled, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.15
	void disableAsynchronousDelivery() throws AsynchronousDeliveryAlreadyDisabled, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.16
	LogicalTime queryLBTS() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError,
			ConcurrentAccessAttempted;

	// 8.17
	LogicalTime queryFederateTime() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 8.18
	LogicalTime queryMinNextEventTime() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 8.19
	void modifyLookahead(LogicalTimeInterval theLookahead) throws InvalidLookahead, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.20
	LogicalTimeInterval queryLookahead() throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 8.21
	void retract(EventRetractionHandle theHandle) throws InvalidRetractionHandle, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.23
	void changeAttributeOrderType(int theObject, AttributeHandleSet theAttributes, int theType)
			throws ObjectNotKnown, AttributeNotDefined, AttributeNotOwned, InvalidOrderingHandle,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 8.24
	void changeInteractionOrderType(int theClass, int theType)
			throws InteractionClassNotDefined, InteractionClassNotPublished, InvalidOrderingHandle,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

//////////////////////////////////
// Data Distribution Management //
//////////////////////////////////

	// 9.2
	Region createRegion(int spaceHandle, int numberOfExtents) throws SpaceNotDefined, InvalidExtents,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.3
	void notifyOfRegionModification(Region modifiedRegionInstance) throws RegionNotKnown, InvalidExtents,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.4
	void deleteRegion(Region theRegion) throws RegionNotKnown, RegionInUse, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.5
	int registerObjectInstanceWithRegion(int theClass, int[] theAttributes, Region[] theRegions)
			throws ObjectClassNotDefined, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress, RestoreInProgress,
			RTIinternalError, ConcurrentAccessAttempted;

	// 9.5
	int registerObjectInstanceWithRegion(int theClass, String theObject, int[] theAttributes, Region[] theRegions)
			throws ObjectClassNotDefined, ObjectClassNotPublished, AttributeNotDefined, AttributeNotPublished,
			RegionNotKnown, InvalidRegionContext, ObjectAlreadyRegistered, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.6
	void associateRegionForUpdates(Region theRegion, int theObject, AttributeHandleSet theAttributes)
			throws ObjectNotKnown, AttributeNotDefined, InvalidRegionContext, RegionNotKnown,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.7
	void unassociateRegionForUpdates(Region theRegion, int theObject)
			throws ObjectNotKnown, InvalidRegionContext, RegionNotKnown, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.8
	void subscribeObjectClassAttributesWithRegion(int theClass, Region theRegion, AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, RegionNotKnown, InvalidRegionContext,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.8
	void subscribeObjectClassAttributesPassivelyWithRegion(int theClass, Region theRegion,
			AttributeHandleSet attributeList)
			throws ObjectClassNotDefined, AttributeNotDefined, RegionNotKnown, InvalidRegionContext,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.9
	void unsubscribeObjectClassWithRegion(int theClass, Region theRegion)
			throws ObjectClassNotDefined, RegionNotKnown, FederateNotSubscribed, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.10
	void subscribeInteractionClassWithRegion(int theClass, Region theRegion)
			throws InteractionClassNotDefined, RegionNotKnown, InvalidRegionContext, FederateLoggingServiceCalls,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.10
	void subscribeInteractionClassPassivelyWithRegion(int theClass, Region theRegion)
			throws InteractionClassNotDefined, RegionNotKnown, InvalidRegionContext, FederateLoggingServiceCalls,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.11
	void unsubscribeInteractionClassWithRegion(int theClass, Region theRegion)
			throws InteractionClassNotDefined, InteractionClassNotSubscribed, RegionNotKnown,
			FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.12
	void sendInteractionWithRegion(int theInteraction, SuppliedParameters theParameters, byte[] userSuppliedTag,
			Region theRegion) throws InteractionClassNotDefined, InteractionClassNotPublished,
			InteractionParameterNotDefined, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.12
	EventRetractionHandle sendInteractionWithRegion(int theInteraction, SuppliedParameters theParameters,
			byte[] userSuppliedTag, Region theRegion, LogicalTime theTime)
			throws InteractionClassNotDefined, InteractionClassNotPublished, InteractionParameterNotDefined,
			InvalidFederationTime, RegionNotKnown, InvalidRegionContext, FederateNotExecutionMember, SaveInProgress,
			RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

	// 9.13
	void requestClassAttributeValueUpdateWithRegion(int theClass, AttributeHandleSet theAttributes, Region theRegion)
			throws ObjectClassNotDefined, AttributeNotDefined, RegionNotKnown, FederateNotExecutionMember,
			SaveInProgress, RestoreInProgress, RTIinternalError, ConcurrentAccessAttempted;

//////////////////////////
// RTI Support Services //
//////////////////////////

	// 10.2
	int getObjectClassHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.3
	String getObjectClassName(int theHandle) throws ObjectClassNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.4
	int getAttributeHandle(String theName, int whichClass)
			throws ObjectClassNotDefined, NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.5
	String getAttributeName(int theHandle, int whichClass)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.6
	int getInteractionClassHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.7
	String getInteractionClassName(int theHandle)
			throws InteractionClassNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.8
	int getParameterHandle(String theName, int whichClass)
			throws InteractionClassNotDefined, NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.9
	String getParameterName(int theHandle, int whichClass) throws InteractionClassNotDefined,
			InteractionParameterNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.10
	int getObjectInstanceHandle(String theName) throws ObjectNotKnown, FederateNotExecutionMember, RTIinternalError;

	// 10.11
	String getObjectInstanceName(int theHandle) throws ObjectNotKnown, FederateNotExecutionMember, RTIinternalError;

	// 10.12
	int getRoutingSpaceHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.13
	String getRoutingSpaceName(int theHandle) throws SpaceNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.14
	int getDimensionHandle(String theName, int whichSpace)
			throws SpaceNotDefined, NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.15
	String getDimensionName(int theHandle, int whichClass)
			throws SpaceNotDefined, DimensionNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.16
	int getAttributeRoutingSpaceHandle(int theHandle, int whichClass)
			throws ObjectClassNotDefined, AttributeNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.17
	int getObjectClass(int theObject) throws ObjectNotKnown, FederateNotExecutionMember, RTIinternalError;

	// 10.18
	int getInteractionRoutingSpaceHandle(int theHandle)
			throws InteractionClassNotDefined, FederateNotExecutionMember, RTIinternalError;

	// 10.19
	int getTransportationHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.20
	String getTransportationName(int theHandle)
			throws InvalidTransportationHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.21
	int getOrderingHandle(String theName) throws NameNotFound, FederateNotExecutionMember, RTIinternalError;

	// 10.22
	String getOrderingName(int theHandle) throws InvalidOrderingHandle, FederateNotExecutionMember, RTIinternalError;

	// 10.23
	void enableClassRelevanceAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.24
	void disableClassRelevanceAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.25
	void enableAttributeRelevanceAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.26
	void disableAttributeRelevanceAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.27
	void enableAttributeScopeAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.28
	void disableAttributeScopeAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.29
	void enableInteractionRelevanceAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	// 10.30
	void disableInteractionRelevanceAdvisorySwitch()
			throws FederateNotExecutionMember, SaveInProgress, RestoreInProgress, RTIinternalError;

	Region getRegion(int regionToken)
			throws FederateNotExecutionMember, ConcurrentAccessAttempted, RegionNotKnown, RTIinternalError;

	int getRegionToken(Region region)
			throws FederateNotExecutionMember, ConcurrentAccessAttempted, RegionNotKnown, RTIinternalError;

	void tick() throws RTIinternalError, ConcurrentAccessAttempted;
}
