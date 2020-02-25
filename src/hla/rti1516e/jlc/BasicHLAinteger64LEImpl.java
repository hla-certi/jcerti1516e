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
import hla.rti1516e.encoding.HLAinteger64LE;

/**
 * Implementation of an HLAinteger on 64 bytes, encoded in Little Endian The
 * value is represented by a long
 */
public class BasicHLAinteger64LEImpl extends DataElementBase implements HLAinteger64LE {

	private long value;

	/**
	 * Empty constructor to create a new BasicHLAinteger64LE
	 */
	public BasicHLAinteger64LEImpl() {
		value = 0L;
	}

	/**
	 * Constructor to create a HLAinteger64LE with a value l
	 * 
	 * @param value : value of the HLAintege642LE, in long
	 */
	public BasicHLAinteger64LEImpl(long value) {
		this.value = value;
	}

	/**
	 * Returns the octet boundary of this element. HLAinteger64LE octet boundary is
	 * defined to 8 in the HLA standard
	 * 
	 * @return the octet boundary of this element
	 */
	@Override
	public int getOctetBoundary() {
		return 8;
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
		byteWrapper.put((int) (value >>> 0) & 0xFF);
		byteWrapper.put((int) (value >>> 8) & 0xFF);
		byteWrapper.put((int) (value >>> 16) & 0xFF);
		byteWrapper.put((int) (value >>> 24) & 0xFF);
		byteWrapper.put((int) (value >>> 32) & 0xFF);
		byteWrapper.put((int) (value >>> 40) & 0xFF);
		byteWrapper.put((int) (value >>> 48) & 0xFF);
		byteWrapper.put((int) (value >>> 56) & 0xFF);
	}

	/**
	 * Returns the size of this element's encoding. integer64LE size is defined to 8
	 * in the HLA standard
	 * 
	 * @return the size in bytes of this element's encoding
	 */
	@Override
	public int getEncodedLength() {
		return 8;
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
		value = 0L;
		value += (byteWrapper.get() & 0xFF) << 0;
		value += (byteWrapper.get() & 0xFF) << 8;
		value += (byteWrapper.get() & 0xFF) << 16;
		value += (byteWrapper.get() & 0xFF) << 24;
		value += (byteWrapper.get() & 0xFF) << 32;
		value += (byteWrapper.get() & 0xFF) << 40;
		value += (byteWrapper.get() & 0xFF) << 48;
		value += (byteWrapper.get() & 0xFF) << 56;
	}

	/**
	 * Get the value in byte of the HLAinteger64LE
	 * 
	 * @return value in byte of the HLAinteger64LE
	 */
	@Override
	public long getValue() {
		return value;
	}

	/**
	 * Change the value of the HLAinteger64LE
	 * 
	 * @param value : value to set
	 */
	@Override
	public void setValue(long value) {
		this.value = value;
	}

}
