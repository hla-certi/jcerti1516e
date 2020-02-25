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
 * @see hla.rti1516.FederateAmbassador#federationNotSaved
 */

public final class SaveFailureReason implements java.io.Serializable {
	private int _value; // each instance's value
	private static final int _lowestValue = 1;
	private static int _nextToAssign = _lowestValue; // begins at lowest

	/**
	 * This is the only public constructor. Each user-defined instance of a
	 * SaveFailureReason must be initialized with one of the defined static values.
	 * 
	 * @param otherSaveFailureReasonValue must be a defined static value or another
	 *                                    instance.
	 */
	public SaveFailureReason(SaveFailureReason otherSaveFailureReasonValue) {
		_value = otherSaveFailureReasonValue._value;
	}

	/**
	 * Private to class
	 */
	private SaveFailureReason() {
		_value = _nextToAssign++;
	}

	SaveFailureReason(int value) throws RTIinternalError {
		_value = value;
		if (value < _lowestValue || value >= _nextToAssign)
			throw new RTIinternalError("SaveFailureReason: illegal value " + value);
	}

	/**
	 * @return String with value "SaveFailureReason(n)" where n is value
	 */
	@Override
	public String toString() {
		return "SaveFailureReason(" + _value + ")";
	}

	/**
	 * Allows comparison with other instance of same type.
	 * 
	 * @return true if supplied object is of type SaveFailureReason and has same
	 *         value; false otherwise
	 */
	@Override
	public boolean equals(Object otherSaveFailureReasonValue) {
		if (otherSaveFailureReasonValue instanceof SaveFailureReason)
			return _value == ((SaveFailureReason) otherSaveFailureReasonValue)._value;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return _value;
	}

	static public final SaveFailureReason RTI_UNABLE_TO_SAVE = new SaveFailureReason();
	static public final SaveFailureReason FEDERATE_REPORTED_FAILURE = new SaveFailureReason();
	static public final SaveFailureReason FEDERATE_RESIGNED = new SaveFailureReason();
	static public final SaveFailureReason RTI_DETECTED_FAILURE = new SaveFailureReason();
	static public final SaveFailureReason SAVE_TIME_CANNOT_BE_HONORED = new SaveFailureReason();
}
