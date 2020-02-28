/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti;

/**
 *
 * Represents a Region in federate's space. A federate creates a Region by
 * calling RTIambassador.createRegion. The federate mdifies the Region by
 * invoking Region methods on it. The federate modifies a Region by first
 * modifying its local instance, then supplying the modified instance to
 * RTIambassador.notifyOfRegionModification.
 *
 * The Region is conceptually an array, with the extents addressed by index
 * running from 0 to getNumberOfExtents()-1.
 */
public interface Region {

	/**
	 * @return long Number of extents in this Region
	 */
	long getNumberOfExtents();

	/**
	 * @return long Lower bound of extent along indicated dimension
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	long getRangeLowerBound(int extentIndex, int dimensionHandle) throws ArrayIndexOutOfBounds;

	/**
	 * @return long Upper bound of extent along indicated dimension
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	long getRangeUpperBound(int extentIndex, int dimensionHandle) throws ArrayIndexOutOfBounds;

	/**
	 * @return int Handle of routing space of which this Region is a subset
	 */
	int getSpaceHandle();

	/**
	 * Modify lower bound of extent along indicated dimension.
	 * 
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @param newLowerBound   long
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	void setRangeLowerBound(int extentIndex, int dimensionHandle, long newLowerBound) throws ArrayIndexOutOfBounds;

	/**
	 * Modify upper bound of extent along indicated dimension.
	 * 
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @param newUpperBound   long
	 * @exception hla.rti.ArrayIndexOutOfBounds The exception description.
	 */
	void setRangeUpperBound(int extentIndex, int dimensionHandle, long newUpperBound) throws ArrayIndexOutOfBounds;
}
