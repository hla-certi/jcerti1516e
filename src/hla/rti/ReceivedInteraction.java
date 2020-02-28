/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 * This packages the information supplied to the federate for
 * receiveInteraction. The parameters are conceptually an array with an initial
 * capacity and the ability to grow. You enumerate by stepping index from 0 to
 * size()-1.
 *
 */
public interface ReceivedInteraction {

	/**
	 * Return order type
	 * 
	 * @return int order type
	 */
	int getOrderType();

	/**
	 * Return parameter handle at index position.
	 * 
	 * @return int parameter handle
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	int getParameterHandle(int index) throws ArrayIndexOutOfBounds;

	/**
	 * Return Region out of which interaction was received.
	 */
	Region getRegion();

	/**
	 * Return transport type
	 * 
	 * @return int transport type
	 */
	int getTransportType();

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
	 * @return int Number of parameter handle-value pairs
	 */
	int size();
}
