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

public class HLAASCIIstringImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAASCIIstring {
    
    private String value;
    private static final String CHARACTER_SET = "ISO-8859-1";

    public HLAASCIIstringImpl() {
        value = "";
    }
    
    public HLAASCIIstringImpl(String s) {
        value = (null!=s ? s: "");
    }
    
    public int getOctetBoundary() {
        return 4;
    }

    
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

    
    public int getEncodedLength() {
        return 4  + value.length();
    }

    
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        int len = byteWrapper.getInt();
        byte content[] = new byte[len];
        try {
            value = new String(content,CHARACTER_SET);
        } catch (UnsupportedEncodingException e) {
            throw new DecoderException("Character Set not Handled",e);
        }
    }

    
    public void decode(byte[] bytes) throws DecoderException {
        throw new DecoderException("NOT IMPLEMENTED");
    }

    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value; 
    }

}
