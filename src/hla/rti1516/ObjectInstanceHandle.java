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
 * Type-safe handle for an object instance. Generally these are created by the
 * RTI and passed to the user.
 */

public interface ObjectInstanceHandle extends java.io.Serializable {

	/**
	 * @return true if this refers to the same instance as other handle
	 */
	@Override
	boolean equals(Object otherObjectInstanceHandle);

	/**
	 * @return int. All instances that refer to the same instance should return the
	 *         same hascode.
	 */
	@Override
	int hashCode();

	int encodedLength();

	void encode(byte[] buffer, int offset);

	@Override
	String toString();

}
//end ObjectInstanceHandle

//File: ObjectInstanceHandleFactory.java
