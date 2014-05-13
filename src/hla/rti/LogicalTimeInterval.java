/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */
 

package hla.rti;

public interface LogicalTimeInterval {

	public void encode(byte[] buffer, int offset);
	public int encodedLength();
	public boolean isEpsilon();
	public boolean isEqualTo(LogicalTimeInterval value);
	public boolean isGreaterThan(LogicalTimeInterval value);
	public boolean isGreaterThanOrEqualTo(LogicalTimeInterval value);
	public boolean isLessThan(LogicalTimeInterval value);
	public boolean isLessThanOrEqualTo(LogicalTimeInterval value);
	public boolean isZero();
	public void setEpsilon();
	public void setTo(LogicalTimeInterval value);
	public void setZero();
}
