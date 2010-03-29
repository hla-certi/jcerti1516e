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

import hla.rti.LogicalTimeInterval;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiLogicalTimeInterval implements LogicalTimeInterval, Comparable<CertiLogicalTimeInterval> {

    /**
     *
     */
    public static final CertiLogicalTimeInterval POSITIVE_INFINITY = new CertiLogicalTimeInterval(Double.MAX_VALUE);
    /**
     *
     */
    public static final CertiLogicalTimeInterval EPSILON = new CertiLogicalTimeInterval(0.00001);
    /**
     *
     */
    public static final CertiLogicalTimeInterval ZERO = new CertiLogicalTimeInterval(0);
    private double interval;

    /**
     *
     * @param interval
     */
    public CertiLogicalTimeInterval(double interval) {
        this.interval = interval;
    }

    /**
     *
     * @param buffer
     * @param offset
     */
    public void encode(byte[] buffer, int offset) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     */
    public int encodedLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     */
    public boolean isEpsilon() {
        return equals(EPSILON);
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isEqualTo(LogicalTimeInterval value) {
        return equals(value);
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isGreaterThan(LogicalTimeInterval value) {
        return compareTo((CertiLogicalTimeInterval) value) > 0;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isGreaterThanOrEqualTo(LogicalTimeInterval value) {
        return compareTo((CertiLogicalTimeInterval) value) >= 0;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isLessThan(LogicalTimeInterval value) {
        return compareTo((CertiLogicalTimeInterval) value) < 0;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isLessThanOrEqualTo(LogicalTimeInterval value) {
        return compareTo((CertiLogicalTimeInterval) value) <= 0;
    }

    /**
     *
     * @return
     */
    public boolean isZero() {
        return equals(ZERO);
    }

    /**
     *
     */
    public void setEpsilon() {
        setTo(EPSILON);
    }

    /**
     *
     * @param value
     */
    public void setTo(LogicalTimeInterval value) {
        if (getClass() != value.getClass()) {
            throw new IllegalArgumentException("Different LogicalTimeInterval implementation supported");
        }

        this.interval = ((CertiLogicalTimeInterval) value).getInterval();
    }

    /**
     *
     */
    public void setZero() {
        setTo(ZERO);
    }

    /**
     *
     * @return
     */
    public double getInterval() {
        return interval;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CertiLogicalTimeInterval other = (CertiLogicalTimeInterval) obj;
        if (this.interval != other.interval) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.interval) ^ (Double.doubleToLongBits(this.interval) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return Double.toString(interval);
    }

    public int compareTo(CertiLogicalTimeInterval o) {
        //double difference = interval - o.interval;
        //return difference > 0 ? 1 : difference < 0 ? -1 : 0;

        return Double.compare(interval, o.interval);
    }
}
