// ----------------------------------------------------------------------------
// CERTI - HLA RunTime Infrastructure
// Copyright (C) 2011 Eric Noulard
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
package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * Implementation of an HLAboolean The value of the HLAboolean is represented by
 * a BasicHLAinteger32BE The value BasicHLAinteger32BE is set to 1 for true and
 * 0 for false
 */
public class HLAbooleanImpl extends DataElementBase implements hla.rti1516e.encoding.HLAboolean {

	private BasicHLAinteger32BEImpl value;

	/**
	 * Empty Constructor of a HLAbooleanImpl Set the value to a new
	 * BasicHLAinteger32BEImpl set to 0
	 */
	public HLAbooleanImpl() {
		value = new BasicHLAinteger32BEImpl(0);
	}

	/**
	 * Empty Constructor of a HLAbooleanImpl Set the value to a new
	 * BasicHLAinteger32BEImpl set to b
	 * 
	 * @param b : value to set
	 */
	public HLAbooleanImpl(boolean b) {
		value = new BasicHLAinteger32BEImpl(b ? 1 : 0);
	}

	/**
	 * Returns the octet boundary of this element. HLAboolean octet boundary is
	 * defined to the attribute value octet boundary
	 * 
	 * @return the octet boundary of this element
	 */
	@Override
	public int getOctetBoundary() {
		return value.getOctetBoundary();
	}

	/**
	 * Encodes this element into the specified ByteWrapper. Call the encode value of
	 * the attribute value
	 * 
	 * @param byteWrapper destination for the encoded element
	 *
	 * @throws EncoderException if the element can not be encoded
	 */
	@Override
	public void encode(ByteWrapper byteWrapper) throws EncoderException {
		value.encode(byteWrapper);
	}

	/**
	 * Returns the size in bytes of this element's encoding. HLAboolean size is
	 * defined to the encoded lenght of the atribute "value"
	 * 
	 * @return the size in bytes of this element's encoding
	 */
	@Override
	public int getEncodedLength() {
		return value.getEncodedLength();
	}

	/**
	 * Decodes this element from the ByteWrapper. Call the methode decode of the
	 * atribute "value"
	 * 
	 * @param byteWrapper source for the decoding of this element
	 *
	 * @throws DecoderException if the element can not be decoded
	 */
	@Override
	public void decode(ByteWrapper byteWrapper) throws DecoderException {
		value.decode(byteWrapper);
	}

	/**
	 * Get the value in boolean of the attribute value
	 * 
	 * @return value in boolean of the attribute value
	 */
	@Override
	public boolean getValue() {
		return (value.getValue() == 1);
	}

	/**
	 * Change the value of the attribute value
	 * 
	 * @param value : value to set
	 */
	@Override
	public void setValue(boolean value) {
		this.value.setValue(value ? 1 : 0);
	}

}
