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
import hla.rti1516e.encoding.HLAoctetPairLE;

/**
 * Implementation of an BasicHLAoctetPairLE
 * The value of the HLAbyte is represented by a short, in little endian
 */
public class BasicHLAoctetPairLEImpl extends DataElementBase implements
        HLAoctetPairLE {

    private short value;
    
    /**
     * Empty constructor to create a new BasicHLAoctetPairLEImpl
     * Set the value to 0
     */
    public BasicHLAoctetPairLEImpl() {
        value = 0;
    }

    /**
     * Constructor to create a BasicHLAoctetPairLE with a value 
     * @param value : value of the BasicHLAoctetPairLE, in short
     */
    public BasicHLAoctetPairLEImpl(short value) {
        this.value = value;
    }
    
    /**
     * Returns the octet boundary of this element.
     * BasicHLAoctetPairLE octet boundary is defined to 2 in the HLA standard
     * @return the octet boundary of this element
     */
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
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put((int)(value >>>  0) & 0xFF);
        byteWrapper.put((int)(value >>>  8) & 0xFF);
    }

    /**
     * Returns the size in bytes of this element's encoding.
     * BasicHLAoctetPairLE size is defined to 2 in the HLA standard
     * @return the size in bytes of this element's encoding
     */
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
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value  = 0;
        value += (short)((byteWrapper.get() & 0xFF) <<  0);
        value += (short)((byteWrapper.get() & 0xFF) <<  8);
    }

    /**
     * Get the value in byte of the BasicHLAoctetPairLE
     * @return value in byte of the BasicHLAoctetPairLE
     */
    public short getValue() {
        return value;
    }

    /**
     * Change the value of the BasicHLAoctetPairLE
     * @param value : value to set
     */
    public void setValue(short value) {
        this.value = value;
    }

}
