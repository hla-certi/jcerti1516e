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
import hla.rti1516e.encoding.HLAinteger32BE;

/**
 * Implementation of an HLAinteger on 32 bytes, encoded in Big Endian The value
 * is represented by a int
 */
public class BasicHLAinteger32BEImpl extends DataElementBase implements HLAinteger32BE {

	private int value;

	/**
	 * Empty constructor to create a new BasicHLAinteger32BE
	 */
	public BasicHLAinteger32BEImpl() {
		value = 0;
	}

	/**
	 * Constructor to create a HLAinteger32BE with a value i
	 * 
	 * @param value : value of the HLAinteger32BE, in integer
	 */
	public BasicHLAinteger32BEImpl(int i) {
		this.value = i;
	}

	/**
	 * Returns the octet boundary of this element. HLAinteger32BE octet boundary is
	 * defined to 4 in the HLA standard
	 * 
	 * @return the octet boundary of this element
	 */
	@Override
	public int getOctetBoundary() {
		return 4;
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
		byteWrapper.put(value >>> 24 & 0xFF);
		byteWrapper.put(value >>> 16 & 0xFF);
		byteWrapper.put(value >>> 8 & 0xFF);
		byteWrapper.put(value >>> 0 & 0xFF);
	}

	/**
	 * Returns the size in bytes of this element's encoding. float32BE size is
	 * defined to 4 in the HLA standard
	 * 
	 * @return the size in bytes of this element's encoding
	 */
	@Override
	public int getEncodedLength() {
		return 4;
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
		value += (byteWrapper.get() & 0xFF) << 24;
		value += (byteWrapper.get() & 0xFF) << 16;
		value += (byteWrapper.get() & 0xFF) << 8;
		value += (byteWrapper.get() & 0xFF) << 0;
	}

	/**
	 * Get the value in byte of the HLAinteger32BE
	 * 
	 * @return value in byte of the HLAinteger32BE
	 */
	@Override
	public int getValue() {
		return value;
	}

	/**
	 * Change the value of the HLAinteger32BE
	 * 
	 * @param value : value to set
	 */
	@Override
	public void setValue(int value) {
		this.value = value;
	}

}
