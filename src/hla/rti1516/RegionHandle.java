/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

public interface RegionHandle extends java.io.Serializable {

	/**
	 * @return true if this refers to the same Region as other handle
	 */
	@Override
	boolean equals(Object otherRegionHandle);

	/**
	 * @return int. All instances that refer to the same Region should return the
	 *         same hashcode.
	 */
	@Override
	int hashCode();

	@Override
	String toString();

}
//end RegionHandle

//File: RegionHandleSet.java
