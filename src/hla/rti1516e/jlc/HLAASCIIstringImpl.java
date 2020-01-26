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

import java.io.UnsupportedEncodingException;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * Implementation of an HLAASCIIstring
 * The value of the HLAASCIIstring is represented by a String
 */
public class HLAASCIIstringImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAASCIIstring {
    
    private String value;
    private static final String CHARACTER_SET = "ISO-8859-1";

    /**
     * Default constructor
     * Set the string value to ""
     */
    public HLAASCIIstringImpl() {
        value = "";
    }
    
    /**
     * Constructor to create a new HLAASCIIstringImpl with a string value s
     * @param s : value to set
     */
    public HLAASCIIstringImpl(String s) {
        value = (null!=s ? s: "");
    }
    
    /**
     * Returns the octet boundary of this element.
     * HLAASCIIstring octet boundary is defined to 4 in the HLA standard
     * @return the octet boundary of this element
     */
    public int getOctetBoundary() {
        return 4;
    }

    /**
     * Encodes this element into the specified ByteWrapper.
     * Convert the String value in byte array and put it in the byteWrapper's buffer
     * @param byteWrapper destination for the encoded element
     *
     * @throws EncoderException if the element can not be encoded
     */
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byte[] content;
        
        try {
            content = value.getBytes(CHARACTER_SET);
        } catch (UnsupportedEncodingException e) {
            throw new EncoderException("Character Set not handled",e);
        }
        byteWrapper.putInt(value.length());
        byteWrapper.put(content);
    }

    /**
     * Returns the size in bytes of this element's encoding.
     * HLAASCIIstring size is defined to 4 (to encode the size) + the lenght of the string 
     * @return the size in bytes of this element's encoding
     */
    public int getEncodedLength() {
        return 4  + value.length();
    }

    /**
     * Decodes this element from the ByteWrapper.
     * Get the bytes in the byteWrapperBuffer and convert them in a new String
     * @param byteWrapper source for the decoding of this element
     *
     * @throws DecoderException if the element can not be decoded
     */
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        int len = byteWrapper.getInt();
        byte content[] = new byte[len];
        byteWrapper.get(content);
        try {
            value = new String(content,CHARACTER_SET);
        } catch (UnsupportedEncodingException e) {
            throw new DecoderException("Character Set not Handled",e);
        }
    }

    /**
     * Get the string value of the HLAASCIIstring
     * @return  the string value of the HLAASCIIstring
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the string value of the HLAASCIIstring with the parameter value
     * @param value : string value to set to the HLAASCIIstring
     */
    public void setValue(String value) {
        this.value = value; 
    }

}
