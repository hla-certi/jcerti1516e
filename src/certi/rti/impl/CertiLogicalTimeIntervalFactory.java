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

import hla.rti.CouldNotDecode;
import hla.rti.LogicalTimeInterval;
import hla.rti.LogicalTimeIntervalFactory;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiLogicalTimeIntervalFactory implements LogicalTimeIntervalFactory {

	/**
	 *
	 * @param buffer
	 * @param offset
	 * @return
	 * @throws CouldNotDecode
	 */
	@Override
	public LogicalTimeInterval decode(byte[] buffer, int offset) throws CouldNotDecode {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 *
	 * @return
	 */
	@Override
	public LogicalTimeInterval makeZero() {
		return new CertiLogicalTimeInterval(CertiLogicalTimeInterval.ZERO.getInterval());
	}

}
