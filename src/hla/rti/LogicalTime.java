
package hla.rti;

public interface LogicalTime {

	public void decreaseBy(LogicalTimeInterval subtrahend)
	throws IllegalTimeArithmetic;
	public void encode(byte[] buffer, int offset);
	public int encodedLength();
	public void increaseBy(LogicalTimeInterval addend)
	throws IllegalTimeArithmetic;
	public boolean isEqualTo(LogicalTime value);
	public boolean isFinal();
	public boolean isGreaterThan(LogicalTime value);
	public boolean isGreaterThanOrEqualTo(LogicalTime value);
	public boolean isInitial();
	public boolean isLessThan(LogicalTime value);
	public boolean isLessThanOrEqualTo(LogicalTime value);
	public void setFinal();
	public void setInitial();
	public void setTo(LogicalTime value);
	public LogicalTimeInterval subtract(LogicalTime subtrahend);
}