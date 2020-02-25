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

import java.util.HashSet;
import java.util.Iterator;

import hla.rti.AttributeHandleSet;
import hla.rti.AttributeNotDefined;
import hla.rti.HandleIterator;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiAttributeHandleSet extends HashSet<Integer> implements AttributeHandleSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3162809907138071826L;

	@Override
	public void add(int handle) throws AttributeNotDefined {
		super.add(handle);
	}

	@Override
	public void empty() {
		clear();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public HandleIterator handles() {
		return new AttributeHandleIterator();
	}

	@Override
	public boolean isMember(int handle) throws AttributeNotDefined {
		return contains(handle);
	}

	@Override
	public void remove(int handle) throws AttributeNotDefined {
		super.remove(handle);
	}

	/**
	 *
	 */
	public class AttributeHandleIterator implements HandleIterator {

		private Iterator iter = iterator();

		@Override
		public int first() {
			iter = iterator();
			return next();
		}

		@Override
		public boolean isValid() {
			return iter.hasNext();
		}

		@Override
		public int next() {
			return (Integer) iter.next();
		}
	}
}
