
package hla.rti;

/**
 * This packages the parameters supplied to the RTI for
 * sendInteraction. This is conceptually an array
 * with an initial capacity and the ability to grow.
 * You enumerate by stepping index from 0 to size()-1.
 * 
 */
public interface SuppliedParameters {

/**
 * Add pair beyond last index.
 * @param handle int
 * @param value byte[]
 */
public void add (int handle, byte[] value );
/**
 * Removes all handles & values.
 */
public void empty ( );
/**
 * Return handle at index position.
 * @return int parameter handle
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public int getHandle ( int index) throws ArrayIndexOutOfBounds;
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
 * Remove handle & value corresponding to handle. All other elements shifted down.
 * Not safe during iteration.
 * @param handle int
 * @exception hla.rti.ArrayIndexOutOfBounds if handle not in set
 */
public void remove ( int handle) throws ArrayIndexOutOfBounds;
/**
 * Remove handle & value at index position. All other elements shifted down.
 * Not safe during iteration.
 * @param index int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public void removeAt ( int index) throws ArrayIndexOutOfBounds;
/**
 * @return int Number of elements
 */
public int size ( );
}