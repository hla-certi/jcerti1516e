
package hla.rti;

/**
 * Factory for SuppliedParameters instances.
 */
public interface SuppliedParametersFactory {

/**
 * Creates a new SuppliedParameters instance with specified initial capacity.
 * @return hla.rti.SuppliedParameters
 * @param capacity int
 */
public SuppliedParameters create ( int capacity);
}