/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

public final class OrderType implements java.io.Serializable {
	private int _value; // each instance's value
	private static final int _lowestValue = 1;
	private static int _nextToAssign = _lowestValue; // begins at lowest

	/**
	 * This is the only public constructor. Each user-defined instance of a
	 * OrderType must be initialized with one of the defined static values.
	 * 
	 * @param otherOrderTypeValue must be a defined static value or another
	 *                            instance.
	 */
	public OrderType(OrderType otherOrderTypeValue) {
		_value = otherOrderTypeValue._value;
	}

	/**
	 * Private to class
	 */
	private OrderType() {
		_value = _nextToAssign++;
	}

	OrderType(int value) throws RTIinternalError {
		_value = value;
		if (value < _lowestValue || value >= _nextToAssign)
			throw new RTIinternalError("OrderType: illegal value " + value);
	}

	/**
	 * @return String with value "OrderType(n)" where n is value
	 */
	@Override
	public String toString() {
		return "OrderType(" + _value + ")";
	}

	/**
	 * Allows comparison with other instance of same type.
	 * 
	 * @return true if supplied object is of type OrderType and has same value;
	 *         false otherwise
	 */
	@Override
	public boolean equals(Object otherOrderTypeValue) {
		if (otherOrderTypeValue instanceof OrderType)
			return _value == ((OrderType) otherOrderTypeValue)._value;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return _value;
	}

	public int encodedLength() {
		return 1;
	}

	public void encode(byte[] buffer, int offset) {
		buffer[offset] = (byte) _value;
	}

	public static OrderType decode(byte[] buffer, int offset) throws CouldNotDecode {
		int val = buffer[offset];
		OrderType neo;
		try {
			neo = new OrderType(val);
		} catch (RTIinternalError e) {
			throw new CouldNotDecode(e.getMessage());
		}
		return neo;
	}

	static public final OrderType RECEIVE = new OrderType();
	static public final OrderType TIMESTAMP = new OrderType();
}
