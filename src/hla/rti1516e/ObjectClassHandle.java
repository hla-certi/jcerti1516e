/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

//File: ObjectClassHandle.java

package hla.rti1516e;

import java.io.Serializable;

/**
 * Type-safe handle for an object class. Generally these are created by the RTI
 * and passed to the user.
 */

public interface ObjectClassHandle extends Serializable {

	/**
	 * @return true if this refers to the same object class as other handle
	 */
	@Override
	boolean equals(Object otherObjectClassHandle);

	/**
	 * @return int. All instances that refer to the same object class should return
	 *         the same hashcode.
	 */
	@Override
	int hashCode();

	int encodedLength();

	void encode(byte[] buffer, int offset);

	@Override
	String toString();

}

//end ObjectClassHandle
