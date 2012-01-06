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

public class BasicHLAoctetImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAoctet {
    private byte value;
    
    public BasicHLAoctetImpl(byte value) {
        this.value = value;
    }
    
    public BasicHLAoctetImpl() {
        value = 0;
    }
    
    
    public int getOctetBoundary() {
        return 1;
    }

    
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put(value);
    }

    
    public int getEncodedLength() {
        return 1;
    }

    
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value = (byte)byteWrapper.get();
    }

    
    public byte getValue() {
        return value;
    }

    
    public void setValue(byte value) {
        this.value = value;
    }
}
