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

import hla.rti.FederateHandleSet;
import hla.rti.HandleIterator;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author aVe
 */
public class CertiFederateHandleSet extends HashSet<Integer> implements FederateHandleSet {

    public void add(int handle) {
        add(handle);
    }

    public void empty() {
        clear();
    }

    /**
     *
     * @return
     */
    public HandleIterator handles() {
        return new AttributeHandleIterator();
    }

    public boolean isMember(int handle) {
        return contains(handle);
    }

    public void remove(int handle) {
        remove(handle);
    }

    /**
     *
     */
    public class AttributeHandleIterator implements HandleIterator {

        private Iterator iter = iterator();

        public int first() {
            iter = iterator();
            return next();
        }

        public boolean isValid() {
            return iter.hasNext();
        }

        public int next() {
            return (Integer) iter.next();
        }
    }
}
