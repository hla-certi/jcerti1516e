/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */
 

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
