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

public class HLAunicodeStringImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAunicodeString {

    private String value;
    
    public HLAunicodeStringImpl() {
        value = "";
    }

    public HLAunicodeStringImpl(String s) {
        value = (null!=s ? s : "");
    }
    
    
    public int getOctetBoundary() {
        return 4;
    }

    
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

    
    public int getEncodedLength() {
        return 4+2*value.length();
    }

    
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
    }
    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }

}
