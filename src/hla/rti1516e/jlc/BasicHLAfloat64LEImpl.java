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
import hla.rti1516e.encoding.HLAfloat64LE;

/**
 *	Implementation of an HLAfloat on 64 bytes, encoded in Little Endian
 *	The value is represented by a double
 */
public class BasicHLAfloat64LEImpl extends DataElementBase implements HLAfloat64LE {

    private double value;
    
    /**
     * Empty constructor to create a new BasicHLAfloat64LE
     */
    public BasicHLAfloat64LEImpl() {
        value = 0.0;
    }
    
    /**
     * Construcor to create a BasicHLAfloat64LE with a value d
     * @param d : value of the BasicHLAfloat64LE, in double
     */
    public BasicHLAfloat64LEImpl(double d) {
        value = d;
    }
    
    /**
     * Returns the octet boundary of this element.
     * float64LE octet boundary is defined to 8 in the HLA standard
     * @return the octet boundary of this element
     */
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
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        long d_as_l = Double.doubleToLongBits(value);
        byte[] buffer = new byte[8];
        
        buffer[7] = (byte)(d_as_l >>> 56);
		buffer[6] = (byte)(d_as_l >>> 48);
		buffer[5] = (byte)(d_as_l >>> 40);
		buffer[4] = (byte)(d_as_l >>> 32);
		buffer[3] = (byte)(d_as_l >>> 24);
		buffer[2] = (byte)(d_as_l >>> 16);
		buffer[1] = (byte)(d_as_l >>>  8);
		buffer[0] = (byte)(d_as_l >>>  0);
		
		byteWrapper.put(buffer);

    }

    /**
     * Returns the size in bytes of this element's encoding.
     * float64LE size is defined to 8 in the HLA standard
     * @return the size in bytes of this element's encoding
     */
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
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        byte[] buffer = new byte[8];
		byteWrapper.get( buffer );
		
		long d_as_l = (((long)buffer[7] << 56) |
	             	  ((long)(buffer[6] & 255) << 48) |
	            	  ((long)(buffer[5] & 255) << 40) |
	            	  ((long)(buffer[4] & 255) << 32) |
	            	  ((long)(buffer[3] & 255) << 24) |
	            	  ((buffer[2] & 255) << 16) |
	            	  ((buffer[1] & 255) <<  8) |
	            	  ((buffer[0] & 255) <<  0));
		
        value = Double.longBitsToDouble(d_as_l);
    }

    /**
     * Get the value in byte of the BasicHLAfloat64LE
     * @return value in byte of the BasicHLAfloat64LE
     */
    public double getValue() {
        return value;
    }

    /**
     * Change the value of the BasicHLAfloat64LE
     * @param value : value to set
     */
    public void setValue(double value) {
        this.value = value;
    }

}
