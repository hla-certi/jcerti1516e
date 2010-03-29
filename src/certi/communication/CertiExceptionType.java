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
 * <p><code>CertiExceptionType</code> represents the type of exception carried by the messages which is exchanged between the federate and the RTIA.</p>
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 * @author <a href = "mailto:yannick.bisiaux@supaero.fr">Yannick Bisiaux</a>
 * @author <a href = "mailto:ronan.bossard@supaero.fr">Ronan Bossard</a>
 * @author <a href = "mailto:samuel.reese@supaero.fr">Samuel Reese</a>
 * @version 3.3.3
 */
public enum CertiExceptionType {

    NO_EXCEPTION,
    ARRAY_INDEX_OUT_OF_BOUNDS,
    ASYNCHRONOUS_DELIVERY_ALREADY_ENABLED,
    ASYNCHRONOUS_DELIVERY_ALREADY_DISABLED,
    ATTRIBUTE_ALREADY_OWNED,
    ATTRIBUTE_ALREADY_BEING_ACQUIRED,
    ATTRIBUTE_ALREADY_BEING_DIVESTED,
    ATTRIBUTE_ACQUISITION_WAS_NOT_REQUESTED,
    ATTRIBUTE_DIVESTITURE_WAS_NOT_REQUESTED,
    ATTRIBUTE_NOT_DEFINED,
    ATTRIBUTE_NOT_KNOWN,
    ATTRIBUTE_NOT_OWNED,
    ATTRIBUTE_NOT_PUBLISHED,
    ATTRIBUTE_NOT_SUBSCRIBED,
    CONCURRENT_ACCESS_ATTEMPTED,
    COULD_NOT_DISCOVER,
    COULD_NOT_OPEN_RID,
    COULD_NOT_OPEN_FED,
    COULD_NOT_RESTORE,
    DELETE_PRIVILEGE_NOT_HELD,
    ERROR_READING_RID,
    ERROR_READING_FED,
    EVENT_NOT_KNOWN,
    FEDERATE_ALREADY_PAUSED,
    FEDERATE_ALREADY_EXECUTION_MEMBER,
    FEDERATE_DOES_NOT_EXIST,
    FEDERATE_INTERNAL_ERROR,
    FEDERATE_NAME_ALREADY_IN_USE,
    FEDERATE_NOT_EXECUTION_MEMBER,
    FEDERATE_NOT_PAUSED,
    FEDERATE_NOT_PUBLISHING,
    FEDERATE_NOT_SUBSCRIBING,
    FEDERATE_OWNS_ATTRIBUTES,
    FEDERATES_CURRENTLY_JOINED,
    FEDERATE_WAS_NOT_ASKED_TO_RELEASE_ATTRIBUTE,
    FEDERATION_ALREADY_PAUSED,
    FEDERATION_EXECUTION_ALREADY_EXISTS,
    FEDERATION_EXECUTION_DOES_NOT_EXIST,
    FEDERATION_NOT_PAUSED,
    FEDERATION_TIME_ALREADY_PASSED,
    REGION_NOT_KNOWN,
    ID_SUPPLY_EXHAUSTED,
    INTERACTION_CLASS_NOT_DEFINED,
    INTERACTION_CLASS_NOT_KNOWN,
    INTERACTION_CLASS_NOT_PUBLISHED,
    INTERACTION_PARAMETER_NOT_DEFINED,
    INTERACTION_PARAMETER_NOT_KNOWN,
    INVALID_DIVESTITURE_CONDITION,
    INVALID_EXTENTS,
    INVALID_FEDERATION_TIME,
    INVALID_FEDERATION_TIME_DELTA,
    INVALID_OBJECT_HANDLE,
    INVALID_RESIGN_ACTION,
    INVALID_RETRACTION_HANDLE,
    INVALID_ROUTING_SPACE,
    MEMORY_EXHAUSTED,
    NAME_NOT_FOUND,
    NO_PAUSE_REQUESTED,
    NO_RESUME_REQUESTED,
    OBJECT_CLASS_NOT_DEFINED,
    OBJECT_CLASS_NOT_KNOWN,
    OBJECT_CLASS_NOT_PUBLISHED,
    OBJECT_CLASS_NOT_SUBSCRIBED,
    OBJECT_NOT_KNOWN,
    OBJECT_ALREADY_REGISTERED,
    RESTORE_IN_PROGRESS,
    RESTORE_NOT_REQUESTED,
    RTI_CANNOT_RESTORE,
    RTI_INTERNAL_ERROR,
    SPACE_NOT_DEFINED,
    SAVE_IN_PROGRESS,
    SAVE_NOT_INITIATED,
    SECURITY_ERROR,
    SPECIFIED_SAVE_LABEL_DOES_NOT_EXIST,
    TIME_ADVANCE_ALREADY_IN_PROGRESS,
    TIME_ADVANCE_WAS_NOT_IN_PROGRESS,
    TOO_MANY_IDS_REQUESTED,
    UNABLE_TO_PERFORM_SAVE,
    UNIMPLEMENTED_SERVICE,
    UNKNOWN_LABEL,
    VALUE_COUNT_EXCEEDED,
    VALUE_LENGTH_EXCEEDED,
    ATTRIBUTE_ACQUISITION_WAS_NOT_CANCELED,
    DIMENSION_NOT_DEFINED,
    ENABLE_TIME_CONSTRAINED_PENDING,
    ENABLE_TIME_CONSTRAINED_WAS_NOT_PENDING,
    ENABLE_TIME_REGULATION_PENDING,
    ENABLE_TIME_REGULATION_WAS_NOT_PENDING,
    FEDERATE_LOGGING_SERVICE_CALLS,
    HANDLE_VALUE_PAIR_MAXIMUM_EXCEEDED,
    INTERACTION_CLASS_NOT_SUBSCRIBED,
    INVALID_HANDLE_VALUE_PAIR_SET_CONTEXT,
    INVALID_LOOKAHEAD,
    INVALID_ORDERING_HANDLE,
    INVALID_REGION_CONTEXT,
    INVALID_TRANSPORTATION_HANDLE,
    OWNERSHIP_ACQUISITION_PENDING,
    REGION_IN_USE,
    SYNCHRONIZATION_POINT_LABEL_WAS_NOT_ANNOUNCED,
    TIME_CONSTRAINED_ALREADY_ENABLED,
    TIME_CONSTRAINED_WAS_NOT_ENABLED,
    TIME_REGULATION_ALREADY_ENABLED,
    TIME_REGULATION_WAS_NOT_ENABLED,
    NETWORK_ERROR,
    NETWORK_SIGNAL;
    /**<p>A HashMap between the <code>int</code> which corresponds to the <code>CertiExceptionType</code> and the <code>CertiExceptionType</code> itself.</p>
     * <p>For instance, <code>CertiExceptionType.reverse.get(0)</code> will return the <code>CertiExceptionType</code> NO_EXCEPTION.</p>
     */
    public static HashMap<Integer, CertiExceptionType> reverse = new HashMap<Integer, CertiExceptionType>();

    static {
        for (CertiExceptionType m : CertiExceptionType.values()) {
            reverse.put(m.ordinal(), m);
        }
    }

    /**Returns the <code>int</code> which corresponds to the <code>CertiExceptionType</code>.
     * @return the <code>int</code>
     */
    public int getException() {
        return this.ordinal();
    }
}
