package certi.rti1516e.impl;

import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.exceptions.InvalidLogicalTimeInterval;

public class CertiLogicalTimeInterval1516E implements LogicalTimeInterval {

	private double interval;
	public static final CertiLogicalTimeInterval1516E POSITIVE_INFINITY = new CertiLogicalTimeInterval1516E(
			Double.MAX_VALUE);
	public static final CertiLogicalTimeInterval1516E EPSILON = new CertiLogicalTimeInterval1516E(Math.ulp(1.0));
	public static final CertiLogicalTimeInterval1516E ZERO = new CertiLogicalTimeInterval1516E(0);

	public CertiLogicalTimeInterval1516E(double interval) {
		this.interval = interval;
	}

	public boolean equals(CertiLogicalTimeInterval1516E logicalInterval) {
		return this.interval == logicalInterval.getInterval();
	}

	/**
	 * Getter
	 * 
	 * @return time value in double
	 */
	public double getInterval() {
		return this.interval;
	}

	/**
	 * Setter
	 * 
	 * @param interval : time value to set, in double
	 */
	public void setInterval(double interval) {
		this.interval = interval;
	}

	@Override
	public boolean isZero() {
		return this.equals(ZERO);
	}

	@Override
	public boolean isEpsilon() {
		return this.equals(EPSILON);
	}

	@Override
	public LogicalTimeInterval add(LogicalTimeInterval addend)
			throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		if (addend instanceof CertiLogicalTimeInterval1516E) {
			double i = ((CertiLogicalTimeInterval1516E) addend).getInterval();
			return new CertiLogicalTimeInterval1516E(this.interval + i);
		} else {
			throw new InvalidLogicalTimeInterval("The parameter value is not a CertiLogicalTimeInterval1516E");
		}
	}

	@Override
	public LogicalTimeInterval subtract(LogicalTimeInterval subtrahend)
			throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		if (subtrahend instanceof CertiLogicalTimeInterval1516E) {
			double i = ((CertiLogicalTimeInterval1516E) subtrahend).getInterval();
			return new CertiLogicalTimeInterval1516E(this.interval - i);
		} else {
			throw new InvalidLogicalTimeInterval("The parameter value is not a CertiLogicalTimeInterval1516E");
		}
	}

	@Override
	public int compareTo(LogicalTimeInterval other) {
		return Double.compare(this.interval, ((CertiLogicalTimeInterval1516E) other).getInterval());
	}

	@Override
	public int compareTo(Object other) {
		return Double.compare(this.interval, ((CertiLogicalTimeInterval1516E) other).getInterval());
	}

	@Override
	public int encodedLength() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void encode(byte[] buffer, int offset) throws CouldNotEncode {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
