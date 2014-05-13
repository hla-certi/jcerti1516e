/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */
 

package hla.rti;

/**
 *
 *
 */
public interface FederateHandleSetFactory {

/**
 *
 * @return hla.rti.FederateHandleSet newly created
 * @exception hla.rti.FederationExecutionDoesNotExist if called before FED read
 */
public FederateHandleSet create()
throws FederationExecutionDoesNotExist;
}
