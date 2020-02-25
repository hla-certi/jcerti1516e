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

import java.util.ArrayList;
import java.util.Iterator;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * Implementation of an HLAopaqueData The value of the HLAopaqueData is
 * represented by a, array of bytes
 */
public class HLAopaqueDataImpl extends DataElementBase implements hla.rti1516e.encoding.HLAopaqueData {

	private byte[] values;

	/**
	 * Empty constructor Create a new array of byte
	 */
	public HLAopaqueDataImpl() {
		values = new byte[0];
	}

	/**
	 * Create a new HLAopaqueData and set it array with the array in parameter
	 * 
	 * @param bytes : array of byte to set to the HLAopaqueData
	 */
	public HLAopaqueDataImpl(byte[] bytes) {
		values = bytes;
	}

	/**
	 * Returns the octet boundary of this element. HLAopaqueData octet boundary is
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
		byteWrapper.putInt(values.length);
		byteWrapper.put(values);
	}

	/**
	 * Returns the size in bytes of this element's encoding. BasicHLAoctet size is
	 * defined to 4 (to encode the size) + the size of the array
	 * 
	 * @return the size in bytes of this element's encoding
	 */
	@Override
	public int getEncodedLength() {
		return 4 + values.length;
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
		values = new byte[byteWrapper.getInt()];
		byteWrapper.get(values);
	}

	/**
	 * Size of the array
	 * 
	 * @return : the size of the array
	 */
	@Override
	public int size() {
		return values.length;
	}

	/**
	 * Create an iterator on the array
	 * 
	 * @return iterator on the array
	 */
	@Override
	public Iterator<Byte> iterator() {
		ArrayList<Byte> barray = new ArrayList<>(values.length);
		for (int i = 0; i < values.length; ++i) {
			barray.add(values[i]);
		}
		return barray.iterator();
	}

	/**
	 * Get the array witch contains all the values
	 * 
	 * @return the array attributes
	 */
	@Override
	public byte[] getValue() {
		return values;
	}

	/**
	 * Set the array attribute with a array value
	 * 
	 * @param : array value to set
	 */
	@Override
	public void setValue(byte[] value) {
		this.values = value;
	}

	/**
	 * Get the value at a specific position in the array
	 * 
	 * @return the value at a specific position in the array
	 */
	@Override
	public byte get(int index) {
		return values[index];
	}

}