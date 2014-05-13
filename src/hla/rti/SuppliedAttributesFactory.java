/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */
 

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
