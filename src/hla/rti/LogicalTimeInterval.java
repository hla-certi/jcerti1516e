
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