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

import java.util.HashMap;
import java.util.Map;

import hla.rti.ArrayIndexOutOfBounds;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiExtent {

	private Map<Integer, Bound> dimensions = new HashMap<>();

	/**
	 *
	 * @param dimensionHandle
	 * @return
	 * @throws ArrayIndexOutOfBounds
	 */
	public long getRangeLowerBound(int dimensionHandle) throws ArrayIndexOutOfBounds {
		if (dimensions.containsKey(dimensionHandle)) {
			return dimensions.get(dimensionHandle).getLower();
		} else {
			throw new ArrayIndexOutOfBounds("Dimension index above limit");
		}
	}

	/**
	 *
	 * @param dimensionHandle
	 * @return
	 * @throws ArrayIndexOutOfBounds
	 */
	public long getRangeUpperBound(int dimensionHandle) throws ArrayIndexOutOfBounds {
		if (dimensions.containsKey(dimensionHandle)) {
			return dimensions.get(dimensionHandle).getUpper();
		} else {
			throw new ArrayIndexOutOfBounds("Dimension index above limit");
		}
	}

	/**
	 *
	 * @param dimensionHandle
	 * @param newLowerBound
	 */
	public void setRangeLowerBound(int dimensionHandle, long newLowerBound) {
		if (!dimensions.containsKey(dimensionHandle)) {
			dimensions.put(dimensionHandle, new Bound());
		}
		dimensions.get(dimensionHandle).setLower(newLowerBound);
	}

	/**
	 *
	 * @param dimensionHandle
	 * @param newUpperBound
	 */
	public void setRangeUpperBound(int dimensionHandle, long newUpperBound) {
		if (!dimensions.containsKey(dimensionHandle)) {
			dimensions.put(dimensionHandle, new Bound());
		}
		dimensions.get(dimensionHandle).setUpper(newUpperBound);
	}

	/**
	 *
	 * @return
	 */
	public int getNumberOfDimensions() {
		return dimensions.size();
	}

	private class Bound {

		private long lower;
		private long upper;

		public long getLower() {
			return lower;
		}

		public void setLower(long lower) {
			this.lower = lower;
		}

		public long getUpper() {
			return upper;
		}

		public void setUpper(long upper) {
			this.upper = upper;
		}
	}
}
