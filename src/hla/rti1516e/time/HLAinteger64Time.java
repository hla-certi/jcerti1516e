/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

package hla.rti1516e.time;

import hla.rti1516e.LogicalTime;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;

/**
 * Interface for the time part of the standardized time type HLAinteger64Time.
 */
public interface HLAinteger64Time extends LogicalTime<HLAinteger64Time, HLAinteger64Interval> {
	@Override
	boolean isInitial();

	@Override
	boolean isFinal();

	@Override
	HLAinteger64Time add(HLAinteger64Interval val) throws IllegalTimeArithmetic;

	@Override
	HLAinteger64Time subtract(HLAinteger64Interval val) throws IllegalTimeArithmetic;

	@Override
	HLAinteger64Interval distance(HLAinteger64Time val);

	@Override
	int compareTo(HLAinteger64Time other);

	@Override
	int encodedLength();

	@Override
	void encode(byte[] buffer, int offset) throws CouldNotEncode;

	long getValue();
}
