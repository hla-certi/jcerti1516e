
package hla.rti;

public interface LogicalTimeIntervalFactory {

	public LogicalTimeInterval decode(byte[] buffer, int offset)
	throws CouldNotDecode;
	public LogicalTimeInterval makeZero();
}