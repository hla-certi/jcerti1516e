
package hla.rti;

/**
 * Factory for SuppliedAttributes instances.
 */
public interface SuppliedAttributesFactory {

   /**
    * Creates a new SuppliedAttributes instance with specified initial capacity.
    * @return hla.rti.SuppliedAttributes
    * @param capacity int
    */
   public SuppliedAttributes create ( int capacity);
}
