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
 * Implementation of an HLAASCIIchar
 * The value of the HLAASCIIchar is represented by a BasicHLAoctet
 */
public class HLAASCIIcharImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAASCIIchar {
    
    private BasicHLAoctetImpl value;
    
    /**
     * Constructor
     * Create a new HLAASCIIchar and set its value to a new BasicHLAoctet
     */
    public HLAASCIIcharImpl() {
        value = new BasicHLAoctetImpl();
    }
    
    /**
     * Constructor of HLAASCIIcharImpl
     * Set the value to a new BasicHLAoctet initialized with the value in parameter
     * @param value : value to set
     */
    public HLAASCIIcharImpl(byte value) {
        this.value = new BasicHLAoctetImpl(value);
    }
    
    /**
     * Returns the octet boundary of this element.
     * HLAunicodeChar octet boundary is the octet boundary of the atribute "value"
     * @return the octet boundary of this element
     */
    public int getOctetBoundary() {
        return value.getOctetBoundary();
    }

    /**
     * Encodes this element into the specified ByteWrapper.
     * Call the methode encode of the atribute "value"
     * @param byteWrapper destination for the encoded element
     *
     * @throws EncoderException if the element can not be encoded
     */
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        value.encode(byteWrapper);
    }
    
    /**
     * Returns the size in bytes of this element's encoding.
     * HLAunicodeChar size is defined to the encoded lenght of the atribute "value"
     * @return the size in bytes of this element's encoding
     */
    public int getEncodedLength() {
        return value.getEncodedLength();
    }

    /**
     * Decodes this element from the ByteWrapper.
     * Call the methode decode of the atribute "value"
     * @param byteWrapper source for the decoding of this element
     *
     * @throws DecoderException if the element can not be decoded
     */
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        value.decode(byteWrapper);
    }
    
    /**
     * Get the value in byte of the attribute value 
     * @return value in byte of the attribute value 
     */
    public byte getValue() {
        return value.getValue();
    }

    /**
     * Change the value of the attribute value
     * @param value : value to set
     */
    public void setValue(byte value) {
        this.value.setValue(value);
    }

}
