
package hla.rti;

/**
 * Interface for a set of federate handles.
 * 
 */
public interface FederateHandleSet {

/**
 * Add the handle to the set. Won't squawk if handle already member.
 * @param handle int
 */
public void add ( int handle);
/**
 * Classic clone
 * 
 * @return java.lang.Object
 */
public Object clone();
/**
 * Empties set of its members.
 * 
 */
public void empty ( );
/**
 * Classic equals.
 * 
 * @return boolean: true if set of same type and same members.
 * @param obj java.lang.Object
 */
public boolean equals(Object obj);
public HandleIterator handles ( );
/**
 * Classic hashCode
 * 
 * @return int: hash code
 */
public int hashCode();
/**
 * 
 * @return boolean: true if set empty.
 */
public boolean isEmpty();
/**
 * 
 * @return boolean: true if handle is a meber
 * @param handle int: an attribute handle
 */
public boolean isMember(int handle);
/**
 * Remove the handle from the set. Won't squawk if handle not a member.
 * 
 * @param handle int
 */
public void remove (int handle );
/**
 * 
 * @return int: number of members
 */
public int size();
/**
 * 
 * @return java.lang.String
 */
public String toString();
}