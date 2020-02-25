/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 * Interface for a set of federate handles.
 *
 */
public interface FederateHandleSet {

	/**
	 * Add the handle to the set. Won't squawk if handle already member.
	 * 
	 * @param handle int
	 */
	void add(int handle);

	/**
	 * Classic clone
	 *
	 * @return java.lang.Object
	 */
	Object clone();

	/**
	 * Empties set of its members.
	 *
	 */
	void empty();

	/**
	 * Classic equals.
	 *
	 * @return boolean: true if set of same type and same members.
	 * @param obj java.lang.Object
	 */
	@Override
	boolean equals(Object obj);

	HandleIterator handles();

	/**
	 * Classic hashCode
	 *
	 * @return int: hash code
	 */
	@Override
	int hashCode();

	/**
	 *
	 * @return boolean: true if set empty.
	 */
	boolean isEmpty();

	/**
	 *
	 * @return boolean: true if handle is a meber
	 * @param handle int: an attribute handle
	 */
	boolean isMember(int handle);

	/**
	 * Remove the handle from the set. Won't squawk if handle not a member.
	 *
	 * @param handle int
	 */
	void remove(int handle);

	/**
	 *
	 * @return int: number of members
	 */
	int size();

	/**
	 *
	 * @return java.lang.String
	 */
	@Override
	String toString();
}
