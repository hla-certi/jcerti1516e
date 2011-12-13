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
import hla.rti1516e.encoding.HLAfloat64BE;

public class BasicHLAfloat64BE extends DataElementBase implements HLAfloat64BE {

    private double value;
    
    public BasicHLAfloat64BE() {
        value = 0.0;
    }
    
    public BasicHLAfloat64BE(double d) {
        value = d;
    }
    
    @Override
    public int getOctetBoundary() {
        return 4;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        long d_as_l = Double.doubleToLongBits(value);
        byteWrapper.put((int)(d_as_l >>> 56) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 48) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 40) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 32) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 24) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 16) & 0xFF);
        byteWrapper.put((int)(d_as_l >>>  8) & 0xFF);
        byteWrapper.put((int)(d_as_l >>>  0) & 0xFF);
    }

    @Override
    public int getEncodedLength() {
        return 8;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        long d_as_l;
        d_as_l  = 0L;
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 56);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 48);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 40);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 32);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 24);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 16);
        d_as_l += (long)((byteWrapper.get() & 0xFF) <<  8);
        d_as_l += (long)((byteWrapper.get() & 0xFF) <<  0);
        value = Double.longBitsToDouble(d_as_l);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        
        long d_as_l;
        d_as_l  = 0L;
        d_as_l += (long)((bytes[0] & 0xFF) << 56);
        d_as_l += (long)((bytes[1] & 0xFF) << 48);
        d_as_l += (long)((bytes[2] & 0xFF) << 40);
        d_as_l += (long)((bytes[3] & 0xFF) << 32);
        d_as_l += (long)((bytes[4] & 0xFF) << 24);
        d_as_l += (long)((bytes[5] & 0xFF) << 16);
        d_as_l += (long)((bytes[6] & 0xFF) <<  8);
        d_as_l += (long)((bytes[7] & 0xFF) <<  0);
        value = Double.longBitsToDouble(d_as_l);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

}
