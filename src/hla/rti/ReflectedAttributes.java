
package hla.rti;

/**
 * This packages the attributes supplied to the federate for
 * reflectAttributeValues. This is conceptually an array
 * with an initial capacity and the ability to grow.
 * You enumerate by stepping index from 0 to size()-1.
 * 
 */
public interface ReflectedAttributes {

/**
 * Return attribute handle at index position.
 * @return int attribute handle
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public int getAttributeHandle ( int index) throws ArrayIndexOutOfBounds;
/**
 * Return order handle at index position.
 * @return int order type
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public int getOrderType ( int index) throws ArrayIndexOutOfBounds;
/**
 * Return Region handle at index position.
 * @return int region handle
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public Region getRegion ( int index) throws ArrayIndexOutOfBounds;
/**
 * Return transport handle at index position.
 * @return int transport type
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public int getTransportType ( int index) throws ArrayIndexOutOfBounds;
/**
 * Return copy of value at index position.
 * @return byte[] copy (clone) of value
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public byte[] getValue ( int index) throws ArrayIndexOutOfBounds;
/**
 * Return length of value at index position.
 * @return int value length
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds 
 */
public int getValueLength ( int index) throws ArrayIndexOutOfBounds;
/**
 * Get the reference of the value at position index (not a clone)
 * @return byte[] the reference
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public byte[] getValueReference ( int index) throws ArrayIndexOutOfBounds;
/**
 * @return int Number of attribute handle-value pairs
 */
public int size ( );
}