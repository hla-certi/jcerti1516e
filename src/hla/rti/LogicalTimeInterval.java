/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

public interface LogicalTimeInterval {

	void encode(byte[] buffer, int offset);

	int encodedLength();

	boolean isEpsilon();

	boolean isEqualTo(LogicalTimeInterval value);

	boolean isGreaterThan(LogicalTimeInterval value);

	boolean isGreaterThanOrEqualTo(LogicalTimeInterval value);

	boolean isLessThan(LogicalTimeInterval value);

	boolean isLessThanOrEqualTo(LogicalTimeInterval value);

	boolean isZero();

	void setEpsilon();

	void setTo(LogicalTimeInterval value);

	void setZero();
}
