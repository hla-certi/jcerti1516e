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
 * @see hla.rti1516.RTIambassador#resignFederationExecution
 */

public final class ResignAction implements java.io.Serializable {
	private int _value; // each instance's value
	private static final int _lowestValue = 1;
	private static int _nextToAssign = _lowestValue; // begins at lowest

	/**
	 * This is the only public constructor. Each user-defined instance of a
	 * ResignAction must be initialized with one of the defined static values.
	 * 
	 * @param otherResignActionValue must be a defined static value or another
	 *                               instance.
	 */
	public ResignAction(ResignAction otherResignActionValue) {
		_value = otherResignActionValue._value;
	}

	/**
	 * Private to class
	 */
	private ResignAction() {
		_value = _nextToAssign++;
	}

	ResignAction(int value) throws RTIinternalError {
		_value = value;
		if (value < _lowestValue || value >= _nextToAssign)
			throw new RTIinternalError("ResignAction: illegal value " + value);
	}

	/**
	 * @return String with value "ResignAction(n)" where n is value
	 */
	@Override
	public String toString() {
		return "ResignAction(" + _value + ")";
	}

	/**
	 * Allows comparison with other instance of same type.
	 * 
	 * @return true if supplied object is of type ResignAction and has same value;
	 *         false otherwise
	 */
	@Override
	public boolean equals(Object otherResignActionValue) {
		if (otherResignActionValue instanceof ResignAction)
			return _value == ((ResignAction) otherResignActionValue)._value;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return _value;
	}

	static public final ResignAction UNCONDITIONALLY_DIVEST_ATTRIBUTES = new ResignAction();
	static public final ResignAction DELETE_OBJECTS = new ResignAction();
	static public final ResignAction CANCEL_PENDING_OWNERSHIP_ACQUISITIONS = new ResignAction();
	static public final ResignAction DELETE_OBJECTS_THEN_DIVEST = new ResignAction();
	static public final ResignAction CANCEL_THEN_DELETE_THEN_DIVEST = new ResignAction();
	static public final ResignAction NO_ACTION = new ResignAction();
}
