
package hla.rti;

public interface LogicalTimeFactory {

	public LogicalTime decode(byte[] buffer, int offset)
	throws CouldNotDecode;
	public LogicalTime makeInitial();
}