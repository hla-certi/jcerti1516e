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
 * Implementation of an BasicHLAoctet
 * The value of the HLAoctet is represented by a byte
 */
public class BasicHLAoctetImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAoctet {
    private byte value;
    
    /**
     * Constructor to create a BasicHLAoctet with a value b
     * @param b : value of the BasicHLAoctet, in byte
     */
    public BasicHLAoctetImpl(byte b) {
        this.value = b;
    }
    
    /**
     * Empty constructor to create a new BasicHLAoctetImpl
     * Set the value to 0
     */
    public BasicHLAoctetImpl() {
        value = 0;
    }
    
    /**
     * Returns the octet boundary of this element.
     * BasicHLAoctet octet boundary is defined to 1 in the HLA standard
     * @return the octet boundary of this element
     */
    public int getOctetBoundary() {
        return 1;
    }

    /**
     * Encodes this element into the specified ByteWrapper.
     *
     * @param byteWrapper destination for the encoded element
     *
     * @throws EncoderException if the element can not be encoded
     */
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put(value);
    }

    /**
     * Returns the size in bytes of this element's encoding.
     * BasicHLAoctet size is defined to 1 in the HLA standard
     * @return the size in bytes of this element's encoding
     */
    public int getEncodedLength() {
        return 1;
    }

    /**
     * Decodes this element from the ByteWrapper.
     *
     * @param byteWrapper source for the decoding of this element
     *
     * @throws DecoderException if the element can not be decoded
     */
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value = (byte)byteWrapper.get();
    }

    /**
     * Get the value in byte of the BasicHLAoctet
     * @return value in byte of the BasicHLAoctet
     */
    public byte getValue() {
        return value;
    }

    /**
     * Change the value of the BasicHLAoctet
     * @param value : value to set
     */
    public void setValue(byte value) {
        this.value = value;
    }
}
