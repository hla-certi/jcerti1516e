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

import java.util.ArrayList;
import java.util.Iterator;

public class HLAopaqueDataImpl extends DataElementBase implements
        hla.rti1516e.encoding.HLAopaqueData {

    private  byte[]   values;

    public HLAopaqueDataImpl() {
        values  = new byte[0];
    }
    
    public HLAopaqueDataImpl(byte[] bytes) {
        values = bytes;
    }
    
    public int getOctetBoundary() {
        return 4;
    }

    public void encode(ByteWrapper byteWrapper) throws EncoderException {
       byteWrapper.align(getOctetBoundary());
       byteWrapper.putInt(values.length);
       byteWrapper.put(values);
    }

    public int getEncodedLength() {
        return 4+values.length;
    }

    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        values = new byte[byteWrapper.getInt()];
        byteWrapper.get(values);
    }

    public int size() {
        return values.length;
    }

    public Iterator<Byte> iterator() {
        ArrayList<Byte> barray = new ArrayList<Byte>(values.length);
        for (int i =0; i<values.length;++i) {
            barray.add(values[i]);
        }
        return barray.iterator();
    }

    public byte[] getValue() {
        return values;
    }

    public void setValue(byte[] value) {
        this.values = value;
    }

    public byte get(int index) {
        return values[index];
    }

}