
package hla.rti;

/**
 * 
 * 
 */
public interface AttributeHandleSet {


/**
 * Add the handle to the set. Won't squawk if handle already member.
 * 
 * @param handle int
 * @exception hla.rti.AttributeNotDefined if handle out of range
 */
public void add ( int handle) throws AttributeNotDefined;
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
 * @return boolean: true if handle is a member
 * @param handle int: an attribute handle
 * @exception hla.rti.AttributeNotDefined if handle out of range
 */
public boolean isMember(int handle) throws AttributeNotDefined;
/**
 * Remove the handle from the set. Won't squawk if handle not a member.
 * 
 * @param handle int
 * @exception hla.rti.AttributeNotDefined if handle out of range
 */
public void remove (int handle ) throws AttributeNotDefined;
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