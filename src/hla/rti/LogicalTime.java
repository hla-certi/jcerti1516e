/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

public interface LogicalTime {

	void decreaseBy(LogicalTimeInterval subtrahend) throws IllegalTimeArithmetic;

	void encode(byte[] buffer, int offset);

	int encodedLength();

	void increaseBy(LogicalTimeInterval addend) throws IllegalTimeArithmetic;

	boolean isEqualTo(LogicalTime value);

	boolean isFinal();

	boolean isGreaterThan(LogicalTime value);

	boolean isGreaterThanOrEqualTo(LogicalTime value);

	boolean isInitial();

	boolean isLessThan(LogicalTime value);

	boolean isLessThanOrEqualTo(LogicalTime value);

	void setFinal();

	void setInitial();

	void setTo(LogicalTime value);

	LogicalTimeInterval subtract(LogicalTime subtrahend);
}
