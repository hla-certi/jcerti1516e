/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

package hla.rti1516e.encoding;

/**
 * Interface for the HLA data type HLAinteger64BE.
 */
public interface HLAinteger64BE extends DataElement {

	/**
	 * Returns the long value of this element.
	 *
	 * @return long value
	 */
	long getValue();

	/**
	 * Sets the long value of this element.
	 *
	 * @param value new value
	 */
	void setValue(long value);
}
