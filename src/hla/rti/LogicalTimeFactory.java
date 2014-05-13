/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */
 

package hla.rti;

public interface LogicalTimeFactory {

	public LogicalTime decode(byte[] buffer, int offset)
	throws CouldNotDecode;
	public LogicalTime makeInitial();
}
