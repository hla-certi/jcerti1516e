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
 * @author aVe
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
    public void decreaseBy(LogicalTimeInterval subtrahend) throws IllegalTimeArithmetic {
        if (getClass() != subtrahend.getClass()) {
            throw new IllegalTimeArithmetic("Different implementation of logical time supplied");
        }

        time -= ((CertiLogicalTimeInterval) subtrahend).getInterval();
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
     * @param addend
     * @throws IllegalTimeArithmetic
     */
    public void increaseBy(LogicalTimeInterval addend) throws IllegalTimeArithmetic {
        if (getClass() != addend.getClass()) {
            throw new IllegalTimeArithmetic("Different implementation of logical time supplied");
        }

        time += ((CertiLogicalTimeInterval) addend).getInterval();
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isEqualTo(LogicalTime value) {
        return this.equals(value);
    }

    /**
     *
     * @return
     */
    public boolean isFinal() {
        return this.equals(FINAL);
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isGreaterThan(LogicalTime value) {
        return compareTo((CertiLogicalTime) value) > 0;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isGreaterThanOrEqualTo(LogicalTime value) {
        return compareTo((CertiLogicalTime) value) >= 0;
    }

    /**
     *
     * @return
     */
    public boolean isInitial() {
        return this.equals(INITIAL);
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isLessThan(LogicalTime value) {
        return compareTo((CertiLogicalTime) value) < 0;
    }

    /**
     *
     * @param value
     * @return
     */
    public boolean isLessThanOrEqualTo(LogicalTime value) {
        return compareTo((CertiLogicalTime) value) <= 0;
    }

    /**
     *
     */
    public void setFinal() {
        setTo(FINAL);
    }

    /**
     *
     */
    public void setInitial() {
        setTo(INITIAL);
    }

    /**
     *
     * @param value
     */
    public void setTo(LogicalTime value) {
        if (getClass() != value.getClass()) {
            throw new IllegalArgumentException("Different implementation of logical time supplied");
        }

        this.time = ((CertiLogicalTime) value).getTime();
    }

    /**
     *
     * @param subtrahend
     * @return
     */
    public LogicalTimeInterval subtract(LogicalTime subtrahend) {
        if (getClass() != subtrahend.getClass()) {
            throw new IllegalArgumentException("Different implementation of logical time supplied");
        }

        return new CertiLogicalTimeInterval(time - ((CertiLogicalTime) subtrahend).getTime());
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
        if (this.time != other.time) {
            return false;
        }
        return true;
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

    public int compareTo(CertiLogicalTime o) {
        //double difference = time - o.getTime();
        //return difference > 0 ? 1 : difference < 0 ? -1 : 0;

        return Double.compare(time, o.time);
    }
}
