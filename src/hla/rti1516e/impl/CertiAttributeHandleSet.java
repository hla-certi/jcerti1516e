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
package hla.rti1516e.impl;

import java.util.HashSet;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.exceptions.AttributeNotDefined;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiAttributeHandleSet extends HashSet<AttributeHandle> implements AttributeHandleSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3162809907138071826L;

	@Override
	public boolean add(AttributeHandle handle) {
		return super.add(handle);
	}

	public void empty() {
		clear();
	}

	@Override
	public AttributeHandleSet clone() {
		return (AttributeHandleSet) super.clone();
	}

	public boolean isMember(AttributeHandle handle) throws AttributeNotDefined {
		return contains(handle);
	}

	public void remove(AttributeHandle handle) throws AttributeNotDefined {
		super.remove(handle);
	}
}
