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
 * @see hla.rti1516.FederateAmbassador#synchronizationPointRegistrationFailed
 */

public final class SynchronizationPointFailureReason implements java.io.Serializable {
	private int _value; // each instance's value
	private static final int _lowestValue = 1;
	private static int _nextToAssign = _lowestValue; // begins at lowest

	/**
	 * This is the only public constructor. Each user-defined instance of a
	 * SynchronizationPointFailureReason must be initialized with one of the defined
	 * static values.
	 * 
	 * @param otherReason must be a defined static value or another instance.
	 */
	public SynchronizationPointFailureReason(SynchronizationPointFailureReason otherReason) {
		_value = otherReason._value;
	}

	/**
	 * Private to class
	 */
	private SynchronizationPointFailureReason() {
		_value = _nextToAssign++;
	}

	SynchronizationPointFailureReason(int value) throws RTIinternalError {
		_value = value;
		if (value < _lowestValue || value >= _nextToAssign)
			throw new RTIinternalError("SynchronizationPointFailureReason: illegal value " + value);
	}

	/**
	 * @return String with value "SynchronizationPointFailureReason(n)" where n is
	 *         value
	 */
	@Override
	public String toString() {
		return "SynchronizationPointFailureReason(" + _value + ")";
	}

	/**
	 * Allows comparison with other instance of same type.
	 * 
	 * @return true if supplied object is of type SynchronizationPointFailureReason
	 *         and has same value; false otherwise
	 */
	@Override
	public boolean equals(Object otherReason) {
		if (otherReason instanceof SynchronizationPointFailureReason)
			return _value == ((SynchronizationPointFailureReason) otherReason)._value;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return _value;
	}

	static public final SynchronizationPointFailureReason SYNCHRONIZATION_POINT_LABEL_NOT_UNIQUE = new SynchronizationPointFailureReason();
	static public final SynchronizationPointFailureReason SYNCHRONIZATION_SET_MEMBER_NOT_JOINED = new SynchronizationPointFailureReason();
}
