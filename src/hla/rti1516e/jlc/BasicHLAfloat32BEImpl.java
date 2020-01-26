package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAfloat32BE;

//----------------------------------------------------------------------------
//CERTI - HLA RunTime Infrastructure
//Copyright (C) 2011 Eric Noulard
//
//This program is free software ; you can redistribute it and/or
//modify it under the terms of the GNU Lesser General Public License
//as published by the Free Software Foundation ; either version 2 of
//the License, or (at your option) any later version.
//
//This program is distributed in the hope that it will be useful, but
//WITHOUT ANY WARRANTY ; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
//Lesser General Public License for more details.
//
//You should have received a copy of the GNU Lesser General Public
//License along with this program ; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
//----------------------------------------------------------------------------

/**
 *	Implementation of an HLAfloat on 32 bytes, encoded in Big Endian
 *	The value is represented by a float
 */
public class BasicHLAfloat32BEImpl extends DataElementBase implements HLAfloat32BE {

    private float value;
    
    /**
     * Empty constructor to create a new BasicHLAfloat32BE
     */
    public BasicHLAfloat32BEImpl() {
        value = 0.0f;
    }
    
    /**
     * Constructor to create a BasicHLAfloat32BE with a value f
     * @param f : value of the BasicHLAfloat32BE, in float
     */
    public BasicHLAfloat32BEImpl(float f) {
        value = f;
    }
    
    /**
     * Returns the octet boundary of this element.
     * float32BEI octet boundary is defined to 4 in the HLA standard
     * @return the octet boundary of this element
     */
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
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        int f_as_i = Float.floatToIntBits(value);
        byteWrapper.put((int)(f_as_i >>> 24) & 0xFF);
        byteWrapper.put((int)(f_as_i >>> 16) & 0xFF);
        byteWrapper.put((int)(f_as_i >>>  8) & 0xFF);
        byteWrapper.put((int)(f_as_i >>>  0) & 0xFF);
    }

    /**
     * Returns the size in bytes of this element's encoding.
     * float32BEI size is defined to 4 in the HLA standard
     * @return the size in bytes of this element's encoding
     */
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
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        int f_as_i;
        f_as_i  = 0;
        f_as_i += (int)((byteWrapper.get() & 0xFF) << 24);
        f_as_i += (int)((byteWrapper.get() & 0xFF) << 16);
        f_as_i += (int)((byteWrapper.get() & 0xFF) <<  8);
        f_as_i += (int)((byteWrapper.get() & 0xFF) <<  0);
        value = Float.intBitsToFloat(f_as_i);
    }

    /**
     * Get the value in byte of the BasicHLAfloat32BE
     * @return value in byte of the BasicHLAfloat32BE
     */
    public float getValue() {
        return value;
    }

    /**
     * Change the value of the BasicHLAfloat32BE
     * @param value : value to set
     */
    public void setValue(float value) {
        this.value = value;
    }

}
