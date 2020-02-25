/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 * This packages the attributes supplied to the federate for
 * reflectAttributeValues. This is conceptually an array with an initial
 * capacity and the ability to grow. You enumerate by stepping index from 0 to
 * size()-1.
 *
 */
public interface ReflectedAttributes {

	/**
	 * Return attribute handle at index position.
	 * 
	 * @return int attribute handle
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	int getAttributeHandle(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Return order handle at index position.
	 * 
	 * @return int order type
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	int getOrderType(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Return Region handle at index position.
	 * 
	 * @return int region handle
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	Region getRegion(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Return transport handle at index position.
	 * 
	 * @return int transport type
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	int getTransportType(int index) throws ArrayIndexOutOfBounds;

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
	 * @return int Number of attribute handle-value pairs
	 */
	int size();
}
