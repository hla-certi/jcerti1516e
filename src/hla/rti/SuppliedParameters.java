/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 * This packages the parameters supplied to the RTI for sendInteraction. This is
 * conceptually an array with an initial capacity and the ability to grow. You
 * enumerate by stepping index from 0 to size()-1.
 *
 */
public interface SuppliedParameters {

	/**
	 * Add pair beyond last index.
	 * 
	 * @param handle int
	 * @param value  byte[]
	 */
	void add(int handle, byte[] value);

	/**
	 * Removes all handles & values.
	 */
	void empty();

	/**
	 * Return handle at index position.
	 * 
	 * @return int parameter handle
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	int getHandle(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Return copy of value at index position.
	 * 
	 * @return byte[] copy (clone) of value
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	byte[] getValue(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Return length of value at index position.
	 * 
	 * @return int value length
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	int getValueLength(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Get the reference of the value at position index (not a clone)
	 * 
	 * @return byte[] the reference
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	byte[] getValueReference(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Remove handle & value corresponding to handle. All other elements shifted
	 * down. Not safe during iteration.
	 * 
	 * @param handle int
	 * @exception hla.rti.ArrayIndexOutOfBounds if handle not in set
	 */
	void remove(int handle) throws ArrayIndexOutOfBounds;

	/**
	 * Remove handle & value at index position. All other elements shifted down. Not
	 * safe during iteration.
	 * 
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	void removeAt(int index) throws ArrayIndexOutOfBounds;

	/**
	 * @return int Number of elements
	 */
	int size();
}
