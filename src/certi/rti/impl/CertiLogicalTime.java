// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi.rti.impl;

import hla.rti.IllegalTimeArithmetic;
import hla.rti.LogicalTime;
import hla.rti.LogicalTimeInterval;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiLogicalTime implements LogicalTime, Comparable<CertiLogicalTime> {

	private double time;
	/**
	 *
	 */
	public static final CertiLogicalTime INITIAL = new CertiLogicalTime(0);
	/**
	 *
	 */
	public static final CertiLogicalTime FINAL = new CertiLogicalTime(Double.MAX_VALUE);

	/**
	 *
	 * @param time
	 */
	public CertiLogicalTime(double time) {
		this.time = time;
	}

	/**
	 *
	 * @param subtrahend
	 * @throws IllegalTimeArithmetic
	 */
	@Override
	public void decreaseBy(LogicalTimeInterval subtrahend) throws IllegalTimeArithmetic {
		if (subtrahend instanceof CertiLogicalTimeInterval) {
			time -= ((CertiLogicalTimeInterval) subtrahend).getInterval();
		} else {
			throw new IllegalTimeArithmetic("Different implementation of logical time supplied");
		}
	}

	/**
	 *
	 * @param buffer
	 * @param offset
	 */
	@Override
	public void encode(byte[] buffer, int offset) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 *
	 * @return
	 */
	@Override
	public int encodedLength() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 *
	 * @param addend
	 * @throws IllegalTimeArithmetic
	 */
	@Override
	public void increaseBy(LogicalTimeInterval addend) throws IllegalTimeArithmetic {
		if (addend instanceof CertiLogicalTimeInterval) {
			time += ((CertiLogicalTimeInterval) addend).getInterval();
		} else {
			throw new IllegalTimeArithmetic("Different implementation of logical time supplied");
		}
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	@Override
	public boolean isEqualTo(LogicalTime value) {
		return this.equals(value);
	}

	/**
	 *
	 * @return
	 */
	@Override
	public boolean isFinal() {
		return this.equals(FINAL);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	@Override
	public boolean isGreaterThan(LogicalTime value) {
		return compareTo((CertiLogicalTime) value) > 0;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	@Override
	public boolean isGreaterThanOrEqualTo(LogicalTime value) {
		return compareTo((CertiLogicalTime) value) >= 0;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public boolean isInitial() {
		return this.equals(INITIAL);
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	@Override
	public boolean isLessThan(LogicalTime value) {
		return compareTo((CertiLogicalTime) value) < 0;
	}

	/**
	 *
	 * @param value
	 * @return
	 */
	@Override
	public boolean isLessThanOrEqualTo(LogicalTime value) {
		return compareTo((CertiLogicalTime) value) <= 0;
	}

	/**
	 *
	 */
	@Override
	public void setFinal() {
		setTo(FINAL);
	}

	/**
	 *
	 */
	@Override
	public void setInitial() {
		setTo(INITIAL);
	}

	/**
	 *
	 * @param value
	 */
	@Override
	public void setTo(LogicalTime value) {
		if (value instanceof CertiLogicalTime) {
			this.time = ((CertiLogicalTime) value).getTime();
		} else {
			throw new IllegalArgumentException("Different implementation of logical time supplied");
		}
	}

	/**
	 *
	 * @param subtrahend
	 * @return
	 */
	@Override
	public LogicalTimeInterval subtract(LogicalTime subtrahend) {
		if (subtrahend instanceof CertiLogicalTime) {
			return new CertiLogicalTimeInterval(time - ((CertiLogicalTime) subtrahend).getTime());
		} else {
			throw new IllegalArgumentException("Different implementation of logical time supplied");
		}
	}

	/**
	 *
	 * @return
	 */
	public double getTime() {
		return time;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		final CertiLogicalTime other = (CertiLogicalTime) obj;
		return this.time == other.time;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + (int) (Double.doubleToLongBits(this.time) ^ (Double.doubleToLongBits(this.time) >>> 32));
		return hash;
	}

	@Override
	public String toString() {
		return Double.toString(time);
	}

	@Override
	public int compareTo(CertiLogicalTime o) {
		// double difference = time - o.getTime();
		// return difference > 0 ? 1 : difference < 0 ? -1 : 0;

		return Double.compare(time, o.time);
	}
}
