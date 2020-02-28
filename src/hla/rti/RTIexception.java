/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 * Superclass of all exceptions thrown by the RTI. All RTI exceptions must be
 * caught or specified.
 */
public class RTIexception extends Exception {
	protected int _serial;

	/**
	 * @param reason String to be carried with exception; equivalent of 'message'
	 *               parameter in .java.lang.Exception
	 */
	public RTIexception(String reason) {
		super(reason);
		_serial = 0;
	}

	/**
	 * @param serial serial number also printed with the exception: programmer can
	 *               assign these to tell where the exception was thrown
	 */
	public RTIexception(String reason, int serial) {
		super(reason);
		_serial = serial;
	}

	public int getSerial() {
		return _serial;
	}

	@Override
	public String toString() {
		String rep = super.toString();
		return rep += " serial:" + _serial;
	}
}
