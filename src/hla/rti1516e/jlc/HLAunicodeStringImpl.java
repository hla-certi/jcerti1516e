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
 * Implementation of an HLAunicodeString
 * The value of the HLAunicodeString is represented by a String
 */
public class HLAunicodeStringImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAunicodeString {

    private String value;
    
    /**
     * Default constructor
     * Set the string value to ""
     */
    public HLAunicodeStringImpl() {
        value = "";
    }

    /**
     * Constructor to create a new HLAunicodeStringImpl with a string value s
     * @param s : value to set
     */
    public HLAunicodeStringImpl(String s) {
        value = (null!=s ? s : "");
    }
    
    /**
     * Returns the octet boundary of this element.
     * HLAunicodeString octet boundary is defined to 4 in the HLA standard
     * @return the octet boundary of this element
     */
    public int getOctetBoundary() {
        return 4;
    }

    /**
     * Encodes this element into the specified ByteWrapper.
     * @param byteWrapper destination for the encoded element
     *
     * @throws EncoderException if the element can not be encoded
     */
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
       byteWrapper.align(getOctetBoundary());
       int ls = value.length();
       // put size of the string first
       byteWrapper.putInt(ls);
       int c;
       for (int i=0; i<ls; ++i) {
           c = (int) value.charAt(i);
           byteWrapper.put((c >>> 8) & 0xFF);
           byteWrapper.put((c >>> 0) & 0xFF);
       }
    }

    /**
     * Returns the size in bytes of this element's encoding.
     * HLAunicodeString size is defined to 4 (to encode the size) + 2 * the lenght of the string 
     * @return the size in bytes of this element's encoding
     */
    public int getEncodedLength() {
        return 4+2*value.length();
    }

    /**
     * Decodes this element from the ByteWrapper.
     * @param byteWrapper source for the decoding of this element
     *
     * @throws DecoderException if the element can not be decoded
     */
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        int ls = byteWrapper.getInt();
        char[] s = new char[ls];
        int upper;
        int lower;
        for (int i=0;i<ls;++i) {
            upper = byteWrapper.get();
            lower = byteWrapper.get();
            s[i] = (char)((upper << 8) + (lower << 0));
        }
        value = new String(s);
    }
    
    /**
     * Get the string value of the HLAunicodeString
     * @return  the string value of the HLAunicodeString
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the string value of the HLAunicodeString with the parameter value
     * @param value : string value to set to the HLAunicodeString
     */
    public void setValue(String value) {
        this.value = value;
    }

}
