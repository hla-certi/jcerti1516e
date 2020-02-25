/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

/**
 * An enumerated type (not a Java Enumeration!)
 * 
 * @see hla.rti1516.FederateAmbassador#federationNotResotred
 */

public final class RestoreFailureReason implements java.io.Serializable {
	private int _value; // each instance's value
	private static final int _lowestValue = 1;
	private static int _nextToAssign = _lowestValue; // begins at lowest

	/**
	 * This is the only public constructor. Each user-defined instance of a
	 * RestoreFailureReason must be initialized with one of the defined static
	 * values.
	 * 
	 * @param otherResignActionValue must be a defined static value or another
	 *                               instance.
	 */
	public RestoreFailureReason(RestoreFailureReason otherResignActionValue) {
		_value = otherResignActionValue._value;
	}

	/**
	 * Private to class
	 */
	private RestoreFailureReason() {
		_value = _nextToAssign++;
	}

	RestoreFailureReason(int value) throws RTIinternalError {
		_value = value;
		if (value < _lowestValue || value >= _nextToAssign)
			throw new RTIinternalError("RestoreFailureReason: illegal value " + value);
	}

	/**
	 * @return String with value "RestoreFailureReason(n)" where n is value
	 */
	@Override
	public String toString() {
		return "RestoreFailureReason(" + _value + ")";
	}

	/**
	 * Allows comparison with other instance of same type.
	 * 
	 * @return true if supplied object is of type RestoreFailureReason and has same
	 *         value; false otherwise
	 */
	@Override
	public boolean equals(Object otherResignActionValue) {
		if (otherResignActionValue instanceof RestoreFailureReason)
			return _value == ((RestoreFailureReason) otherResignActionValue)._value;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return _value;
	}

	static public final RestoreFailureReason RTI_UNABLE_TO_RESTORE = new RestoreFailureReason();
	static public final RestoreFailureReason FEDERATE_REPORTED_FAILURE = new RestoreFailureReason();
	static public final RestoreFailureReason FEDERATE_RESIGNED = new RestoreFailureReason();
	static public final RestoreFailureReason RTI_DETECTED_FAILURE = new RestoreFailureReason();
}
