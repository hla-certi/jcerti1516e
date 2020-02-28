// ----------------------------------------------------------------------------
// CERTI - HLA RunTime Infrastructure
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
package certi.rti.jlc;

import certi.rti.impl.CertiAttributeHandleSet;
import certi.rti.impl.CertiFederateHandleSet;
import certi.rti.impl.CertiHandleValuePairCollection;
import certi.rti.impl.CertiRtiAmbassador;
import hla.rti.AttributeHandleSet;
import hla.rti.FederateHandleSet;
import hla.rti.RTIinternalError;
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;
import hla.rti.jlc.RTIambassadorEx;
import hla.rti.jlc.RtiFactory;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiRtiFactory implements RtiFactory {

	/**
	 *
	 * @return
	 * @throws RTIinternalError
	 */
	@Override
	public RTIambassadorEx createRtiAmbassador() throws RTIinternalError {
		return new CertiRtiAmbassador();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String RtiName() {
		return "Certi";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String RtiVersion() {
		return "3.4.0";
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getMinExtent() {
		return Long.MIN_VALUE;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public long getMaxExtent() {
		return Long.MAX_VALUE;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public AttributeHandleSet createAttributeHandleSet() {
		return new CertiAttributeHandleSet();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public FederateHandleSet createFederateHandleSet() {
		return new CertiFederateHandleSet();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public SuppliedAttributes createSuppliedAttributes() {
		return new CertiHandleValuePairCollection();
	}

	/**
	 *
	 * @return
	 */
	@Override
	public SuppliedParameters createSuppliedParameters() {
		return new CertiHandleValuePairCollection();
	}
}
