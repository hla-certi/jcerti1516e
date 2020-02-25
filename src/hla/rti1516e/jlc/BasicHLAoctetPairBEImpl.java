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
import hla.rti1516e.encoding.HLAoctetPairBE;

/**
 * Implementation of an BasicHLAoctetPairBE The value of the HLAbyte is
 * represented by a short, in big endian
 */
public class BasicHLAoctetPairBEImpl extends DataElementBase implements HLAoctetPairBE {

	private short value;

	/**
	 * Empty constructor to create a new BasicHLAoctetPairBEImpl Set the value to 0
	 */
	public BasicHLAoctetPairBEImpl() {
		value = 0;
	}

	/**
	 * Constructor to create a BasicHLAoctetPairBE with a value
	 * 
	 * @param value : value of the BasicHLAoctetPairBE, in short
	 */
	public BasicHLAoctetPairBEImpl(short value) {
		this.value = value;
	}

	/**
	 * Returns the octet boundary of this element. BasicHLAoctetPairBE octet
	 * boundary is defined to 2 in the HLA standard
	 * 
	 * @return the octet boundary of this element
	 */
	@Override
	public int getOctetBoundary() {
		return 2;
	}

	/**
	 * Encodes this element into the specified ByteWrapper.
	 *
	 * @param byteWrapper destination for the encoded element
	 *
	 * @throws EncoderException if the element can not be encoded
	 */
	@Override
	public void encode(ByteWrapper byteWrapper) throws EncoderException {
		byteWrapper.align(getOctetBoundary());
		byteWrapper.put(value >>> 8 & 0xFF);
		byteWrapper.put(value >>> 0 & 0xFF);
	}

	/**
	 * Returns the size in bytes of this element's encoding. BasicHLAoctetPairBE
	 * size is defined to 2 in the HLA standard
	 * 
	 * @return the size in bytes of this element's encoding
	 */
	@Override
	public int getEncodedLength() {
		return 2;
	}

	/**
	 * Decodes this element from the ByteWrapper.
	 *
	 * @param byteWrapper source for the decoding of this element
	 *
	 * @throws DecoderException if the element can not be decoded
	 */
	@Override
	public void decode(ByteWrapper byteWrapper) throws DecoderException {
		byteWrapper.align(getOctetBoundary());
		value = 0;
		value += (short) ((byteWrapper.get() & 0xFF) << 8);
		value += (short) ((byteWrapper.get() & 0xFF) << 0);
	}

	/**
	 * Get the value in byte of the BasicHLAoctetPairBE
	 * 
	 * @return value in byte of the BasicHLAoctetPairBE
	 */
	@Override
	public short getValue() {
		return value;
	}

	/**
	 * Change the value of the BasicHLAoctetPairBE
	 * 
	 * @param value : value to set
	 */
	@Override
	public void setValue(short value) {
		this.value = value;
	}

}
