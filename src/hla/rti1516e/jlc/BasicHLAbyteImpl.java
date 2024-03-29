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
 * Implementation of an HLAbyte The value of the HLAbyte is represented by an
 * BasicHLAoctet
 */
public class BasicHLAbyteImpl extends DataElementBase implements hla.rti1516e.encoding.HLAbyte {

	private BasicHLAoctetImpl value;

	/**
	 * Empty constructor to create a new BasicHLAbyteImpl
	 */
	public BasicHLAbyteImpl() {
		value = new BasicHLAoctetImpl();
	}

	/**
	 * Construcor to create a BasicHLAbyteImpl with a value b
	 * 
	 * @param b : value of the BasicHLAbyteImpl, in byte
	 */
	public BasicHLAbyteImpl(byte b) {
		value = new BasicHLAoctetImpl(b);
	}

	/**
	 * Returns the octet boundary of this element.
	 *
	 * @return the octet boundary of this element
	 */
	@Override
	public int getOctetBoundary() {
		return value.getOctetBoundary();
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
		value.encode(byteWrapper);
	}

	/**
	 * Returns the size in bytes of this element's encoding.
	 *
	 * @return the size in bytes of this element's encoding
	 */
	@Override
	public int getEncodedLength() {
		return value.getEncodedLength();
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
		value.decode(byteWrapper);
	}

	/**
	 * Get the value in byte of the BasicHLAbyteImpl
	 * 
	 * @return value in byte of the BasicHLAbyteImpl
	 */
	@Override
	public byte getValue() {
		return value.getValue();
	}

	/**
	 * Change the value of the BasicHLAbyteImpl
	 * 
	 * @param value : value to set
	 */
	@Override
	public void setValue(byte value) {
		this.value.setValue(value);
	}

}
