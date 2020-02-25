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
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;

/**
 * Collection similar to standard array that holds handle-value pairs and has
 * ability to dynamically grow.
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 * @version 3.3.3
 */
public class CertiHandleValuePairCollection implements SuppliedAttributes, SuppliedParameters {

	private List<HandleValuePair> pairs;

	/**
	 * Constructs the handle-value pair collection
	 */
	public CertiHandleValuePairCollection() {
		pairs = new ArrayList<>();
	}

	/**
	 * Constructs the handle-value pair collection with specified initial capacity.
	 * This is prefered to default constructor because of better performance.
	 * 
	 * @param size starting initial capacity of collection
	 */
	public CertiHandleValuePairCollection(int size) {
		pairs = new ArrayList<>(size);
	}

	/**
	 * Add pair beyond last index.
	 *
	 * @param handle int
	 * @param value  byte[]
	 */
	@Override
	public void add(int handle, byte[] value) {
		pairs.add(new HandleValuePair(handle, value));
	}

	/**
	 * Removes all handles & values.
	 */
	@Override
	public void empty() {
		pairs.clear();
	}

	/**
	 * Return handle at index position.
	 *
	 * @return int attribute handle
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public int getHandle(int index) throws ArrayIndexOutOfBounds {
		return pairs.get(index).getHandle();
	}

	/**
	 * Return copy of value at index position.
	 *
	 * @return byte[] copy (clone) of value
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public byte[] getValue(int index) throws ArrayIndexOutOfBounds {
		return this.getValueReference(index).clone();
	}

	/**
	 * Return length of value at index position.
	 *
	 * @return int value length
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public int getValueLength(int index) throws ArrayIndexOutOfBounds {
		return this.getValueReference(index).length;
	}

	/**
	 * Get the reference of the value at position index (not a clone)
	 *
	 * @return byte[] the reference
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public byte[] getValueReference(int index) throws ArrayIndexOutOfBounds {
		return pairs.get(index).getValue();
	}

	/**
	 * Remove handle & value corresponding to handle. All other elements shifted
	 * down. Not safe during iteration.
	 *
	 * @param handle int
	 * @exception hla.rti.ArrayIndexOutOfBounds if handle not in set
	 */
	@Override
	public void remove(int handle) throws ArrayIndexOutOfBounds {
		pairs.remove(new HandleValuePair(handle, null));
	}

	/**
	 * Remove handle & value at index position. All other elements shifted down. Not
	 * safe during iteration.
	 *
	 * @param index int
	 * @exception hla.rti.ArrayIndexOutOfBounds
	 */
	@Override
	public void removeAt(int index) throws ArrayIndexOutOfBounds {
		pairs.remove(index);
	}

	/**
	 * @return int Number of elements
	 */
	@Override
	public int size() {
		return pairs.size();
	}

	@Override
	public String toString() {
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("{");
		for (HandleValuePair pair : pairs) {
			stringBuffer.append(pair.toString());
		}
		stringBuffer.append("}");
		return stringBuffer.toString();
	}

	class HandleValuePair {

		private int handle;
		private byte[] value;

		protected HandleValuePair(int handle, byte[] value) {
			this.handle = handle;
			this.value = value;
		}

		public int getHandle() {
			return handle;
		}

		public void setHandle(int handle) {
			this.handle = handle;
		}

		public byte[] getValue() {
			return value;
		}

		public void setValue(byte[] value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final HandleValuePair other = (HandleValuePair) obj;
			return this.handle == other.handle;
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 31 * hash + this.handle;
			return hash;
		}

		@Override
		public String toString() {
			return "(" + handle + ":" + value + ")";
		}
	}
}
