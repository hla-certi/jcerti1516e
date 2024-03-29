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
 * Interface for the time part of the standardized time type HLAfloat64Time.
 */
public interface HLAfloat64Time extends LogicalTime<HLAfloat64Time, HLAfloat64Interval> {
	@Override
	boolean isInitial();

	@Override
	boolean isFinal();

	@Override
	HLAfloat64Time add(HLAfloat64Interval val) throws IllegalTimeArithmetic;

	@Override
	HLAfloat64Time subtract(HLAfloat64Interval val) throws IllegalTimeArithmetic;

	@Override
	HLAfloat64Interval distance(HLAfloat64Time val);

	@Override
	int compareTo(HLAfloat64Time other);

	@Override
	int encodedLength();

	@Override
	void encode(byte[] buffer, int offset) throws CouldNotEncode;

	double getValue();
}