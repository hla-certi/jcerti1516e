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

import java.util.ArrayList;
import java.util.List;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.Region;

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
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 * @version 3.3.3
 */
public class CertiRegion implements Region {

	private int spaceHandle;
	private int handle;
	private List<CertiExtent> extents;

	/**
	 *
	 * @param handle
	 * @param space
	 * @param numberOfExtends
	 */
	public CertiRegion(int handle, int space, int numberOfExtends) {
		this.handle = handle;
		this.spaceHandle = space;
		this.extents = new ArrayList<>(numberOfExtends);

		for (int i = 0; i < numberOfExtends; i++) {
			extents.add(new CertiExtent());
		}
	}

	/**
	 * @return long Number of extents in this Region
	 */
	@Override
	public long getNumberOfExtents() {
		return extents.size();
	}

	/**
	 * @return long Lower bound of extent along indicated dimension
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public long getRangeLowerBound(int extentIndex, int dimensionHandle) throws ArrayIndexOutOfBounds {
		if (extentIndex < extents.size()) {
			return extents.get(extentIndex).getRangeLowerBound(dimensionHandle);
		} else {
			throw new ArrayIndexOutOfBounds("Extent index above limit");
		}
	}

	/**
	 * @return long Upper bound of extent along indicated dimension
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public long getRangeUpperBound(int extentIndex, int dimensionHandle) throws ArrayIndexOutOfBounds {
		if (extentIndex < extents.size()) {
			return extents.get(extentIndex).getRangeUpperBound(dimensionHandle);
		} else {
			throw new ArrayIndexOutOfBounds("Extent index above limit");
		}
	}

	/**
	 * @return int Handle of routing space of which this Region is a subset
	 */
	@Override
	public int getSpaceHandle() {
		return spaceHandle;
	}

	/**
	 * Modify lower bound of extent along indicated dimension.
	 * 
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @param newLowerBound   long
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public void setRangeLowerBound(int extentIndex, int dimensionHandle, long newLowerBound)
			throws ArrayIndexOutOfBounds {
		if (extentIndex < extents.size()) {
			extents.get(extentIndex).setRangeLowerBound(dimensionHandle, newLowerBound);
		} else {
			throw new ArrayIndexOutOfBounds("Extent index above limit");
		}
	}

	/**
	 * Modify upper bound of extent along indicated dimension.
	 * 
	 * @param extentIndex     int
	 * @param dimensionHandle int
	 * @param newUpperBound   long
	 * @exception hla.rti.ArrayIndexOutOfBounds The exception description.
	 */
	@Override
	public void setRangeUpperBound(int extentIndex, int dimensionHandle, long newUpperBound)
			throws ArrayIndexOutOfBounds {
		if (extentIndex < extents.size()) {
			extents.get(extentIndex).setRangeUpperBound(dimensionHandle, newUpperBound);
		} else {
			throw new ArrayIndexOutOfBounds("Extent index above limit");
		}
	}

	/**
	 *
	 * @return
	 */
	public int getHandle() {
		return handle;
	}

	/**
	 *
	 * @param handle
	 */
	public void setHandle(int handle) {
		this.handle = handle;
	}

	/**
	 *
	 * @return
	 */
	public List<CertiExtent> getExtents() {
		return extents;
	}

	/**
	 *
	 * @param extents
	 */
	public void setExtents(List<CertiExtent> extents) {
		this.extents = extents;
	}
}
