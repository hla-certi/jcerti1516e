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
import hla.rti1516e.encoding.HLAinteger32LE;

public class BasicHLAinteger32LEImpl extends DataElementBase implements
        HLAinteger32LE {

    private int value;
    
    public BasicHLAinteger32LEImpl() {
        value = 0;
    }
    
    public BasicHLAinteger32LEImpl(int value) {
        this.value = value;
    }
    
    
    public int getOctetBoundary() {
        return 4;
    }

    
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put((int)(value >>>  0) & 0xFF);
        byteWrapper.put((int)(value >>>  8) & 0xFF);
        byteWrapper.put((int)(value >>> 16) & 0xFF);
        byteWrapper.put((int)(value >>> 24) & 0xFF);
    }

    
    public int getEncodedLength() {
        return 4;
    }

    
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value  = 0;
        value += (int)((byteWrapper.get() & 0xFF) <<  0);
        value += (int)((byteWrapper.get() & 0xFF) <<  8);
        value += (int)((byteWrapper.get() & 0xFF) << 16);
        value += (int)((byteWrapper.get() & 0xFF) << 24);
    }

    
    public int getValue() {
        return value;
    }

    
    public void setValue(int value) {
        this.value = value;
    }

}
