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

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aVe
 */
public class CertiHandleValuePairCollection implements SuppliedAttributes, SuppliedParameters {

    private List<HandleValuePair> pairs;

    /**
     *
     */
    public CertiHandleValuePairCollection() {
        pairs = new ArrayList<HandleValuePair>();
    }

    /**
     *
     * @param size
     */
    public CertiHandleValuePairCollection(int size) {
        pairs = new ArrayList<HandleValuePair>(size);
    }

    public void add(int handle, byte[] value) {
        pairs.add(new HandleValuePair(handle, value));
    }

    public void empty() {
        pairs.clear();
    }

    public int getHandle(int index) throws ArrayIndexOutOfBounds {
        return pairs.get(index).getHandle();
    }

    public byte[] getValue(int index) throws ArrayIndexOutOfBounds {
        return this.getValueReference(index).clone();
    }

    public int getValueLength(int index) throws ArrayIndexOutOfBounds {
        return this.getValueReference(index).length;
    }

    public byte[] getValueReference(int index) throws ArrayIndexOutOfBounds {        
        return pairs.get(index).getValue();
    }

    public void remove(int handle) throws ArrayIndexOutOfBounds {
        pairs.remove(new HandleValuePair(handle, null));
    }

    public void removeAt(int index) throws ArrayIndexOutOfBounds {
        pairs.remove(index);
    }

    /**
     *
     * @return
     */
    public List<Integer> getHandles() {
        List<Integer> list = new ArrayList<Integer>(this.size());

        for (HandleValuePair pair : pairs) {
            list.add(pair.getHandle());
        }

        return list;
    }

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
            if (this.handle != other.handle) {
                return false;
            }
            return true;
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
