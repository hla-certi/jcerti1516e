
package hla.rti;

/**
 * 
 * Represents a Region in federate's space.
 * A federate creates a Region by calling RTIambassador.createRegion.
 * The federate mdifies the Region by invoking Region methods
 * on it. The federate modifies a Region by first modifying
 * its local instance, then supplying the modified instance
 * to RTIambassador.notifyOfRegionModification.
 * 
 * The Region is conceptually an array, with the extents addressed
 * by index running from 0 to getNumberOfExtents()-1.
 */
public interface Region {

/**
 * @return long Number of extents in this Region
 */
public long getNumberOfExtents ( );
/**
 * @return long Lower bound of extent along indicated dimension
 * @param extentIndex int
 * @param dimensionHandle int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public long getRangeLowerBound (int extentIndex, int dimensionHandle) throws ArrayIndexOutOfBounds;
/**
 * @return long Upper bound of extent along indicated dimension
 * @param extentIndex int
 * @param dimensionHandle int
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public long getRangeUpperBound (int extentIndex, int dimensionHandle) throws ArrayIndexOutOfBounds;
/**
 * @return int Handle of routing space of which this Region is a subset
 */
public int getSpaceHandle ( );
/**
 * Modify lower bound of extent along indicated dimension.
 * @param extentIndex int
 * @param dimensionHandle int
 * @param newLowerBound long
 * @exception hla.rti.ArrayIndexOutOfBounds
 */
public void setRangeLowerBound ( int extentIndex, int dimensionHandle, long newLowerBound) throws ArrayIndexOutOfBounds;
/**
 * Modify upper bound of extent along indicated dimension.
 * @param extentIndex int
 * @param dimensionHandle int
 * @param newUpperBound long
 * @exception hla.rti.ArrayIndexOutOfBounds The exception description.
 */
public void setRangeUpperBound (int extentIndex, int dimensionHandle, long newUpperBound) throws ArrayIndexOutOfBounds;
}