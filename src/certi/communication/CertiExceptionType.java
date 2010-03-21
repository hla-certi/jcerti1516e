// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2007-2010 Andrej Pancik, Yannick Bisiaux, Ronan Bossard, Samuel Reese
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

import java.util.HashMap;

/**
 * <p><code>CertiExceptionType</code> represents the exception carried by the messages which is exchanged between the federate and the RTIA.</p>
 *
 * @author apancik@gmail.com
 * @author <a href = "mailto:yannick.bisiaux@supaero.fr">Yannick Bisiaux</a>, <a href = "mailto:ronan.bossard@supaero.fr">Ronan Bossard</a>, <a href = "mailto:samuel.reese@supaero.fr">Samuel Reese</a>
 * @version 1.0
 */
public enum CertiExceptionType {

    NoException,
    ArrayIndexOutOfBounds,
    AsynchronousDeliveryAlreadyEnabled,
    AsynchronousDeliveryAlreadyDisabled,
    AttributeAlreadyOwned,
    AttributeAlreadyBeingAcquired,
    AttributeAlreadyBeingDivested,
    AttributeAcquisitionWasNotRequested,
    AttributeDivestitureWasNotRequested,
    AttributeNotDefined,
    AttributeNotKnown,
    AttributeNotOwned,
    AttributeNotPublished,
    AttributeNotSubscribed,
    ConcurrentAccessAttempted,
    CouldNotDiscover,
    CouldNotOpenRID,
    CouldNotOpenFED,
    CouldNotRestore,
    DeletePrivilegeNotHeld,
    ErrorReadingRID,
    ErrorReadingFED,
    EventNotKnown,
    FederateAlreadyPaused,
    FederateAlreadyExecutionMember,
    FederateDoesNotExist,
    FederateInternalError,
    FederateNameAlreadyInUse,
    FederateNotExecutionMember,
    FederateNotPaused,
    FederateNotPublishing,
    FederateNotSubscribing,
    FederateOwnsAttributes,
    FederatesCurrentlyJoined,
    FederateWasNotAskedToReleaseAttribute,
    FederationAlreadyPaused,
    FederationExecutionAlreadyExists,
    FederationExecutionDoesNotExist,
    FederationNotPaused,
    FederationTimeAlreadyPassed,
    RegionNotKnown,
    IDsupplyExhausted,
    InteractionClassNotDefined,
    InteractionClassNotKnown,
    InteractionClassNotPublished,
    InteractionParameterNotDefined,
    InteractionParameterNotKnown,
    InvalidDivestitureCondition,
    InvalidExtents,
    InvalidFederationTime,
    InvalidFederationTimeDelta,
    InvalidObjectHandle,
    InvalidResignAction,
    InvalidRetractionHandle,
    InvalidRoutingSpace,
    MemoryExhausted,
    NameNotFound,
    NoPauseRequested,
    NoResumeRequested,
    ObjectClassNotDefined,
    ObjectClassNotKnown,
    ObjectClassNotPublished,
    ObjectClassNotSubscribed,
    ObjectNotKnown,
    ObjectAlreadyRegistered,
    RestoreInProgress,
    RestoreNotRequested,
    RTICannotRestore,
    RTIinternalError,
    SpaceNotDefined,
    SaveInProgress,
    SaveNotInitiated,
    SecurityError,
    SpecifiedSaveLabelDoesNotExist,
    TimeAdvanceAlreadyInProgress,
    TimeAdvanceWasNotInProgress,
    TooManyIDsRequested,
    UnableToPerformSave,
    UnimplementedService,
    UnknownLabel,
    ValueCountExceeded,
    ValueLengthExceeded,
    AttributeAcquisitionWasNotCanceled,
    DimensionNotDefined,
    EnableTimeConstrainedPending,
    EnableTimeConstrainedWasNotPending,
    EnableTimeRegulationPending,
    EnableTimeRegulationWasNotPending,
    FederateLoggingServiceCalls,
    HandleValuePairMaximumExceeded,
    InteractionClassNotSubscribed,
    InvalidHandleValuePairSetContext,
    InvalidLookahead,
    InvalidOrderingHandle,
    InvalidRegionContext,
    InvalidTransportationHandle,
    OwnershipAcquisitionPending,
    RegionInUse,
    SynchronizationPointLabelWasNotAnnounced,
    TimeConstrainedAlreadyEnabled,
    TimeConstrainedWasNotEnabled,
    TimeRegulationAlreadyEnabled,
    TimeRegulationWasNotEnabled,
    NetworkError,
    NetworkSignal;

    /**<p>Makes a HashMap between the <code>int</code> which corresponds with the <code>CertiExceptionType</code> and the <code>CertiExceptionType</code> itself.</p>
     * <p>For instance, <code>CertiExceptionType.reverse.get(0)</code> will return the <code>CertiExceptionType</code> NoException.</p>
     */
    public static HashMap<Integer, CertiExceptionType> reverse = new HashMap<Integer, CertiExceptionType>();

    static {
        for (CertiExceptionType m : CertiExceptionType.values()) {
            reverse.put(m.ordinal(), m);
        }
    }

    /**Returns the <code>int</code> which corresponds with the <code>CertiExceptionType</code>.
     * @return the <code>int</code>
     */
    public int getException() {
        return this.ordinal();
    }
}
