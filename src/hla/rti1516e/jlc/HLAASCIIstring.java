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

public class HLAASCIIstring extends DataElementBase implements
        hla.rti1516e.encoding.HLAASCIIstring {

    public HLAASCIIstring() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getOctetBoundary() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getEncodedLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        // TODO Auto-generated method stub

    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        // TODO Auto-generated method stub

    }

    @Override
    public String getValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setValue(String value) {
        // TODO Auto-generated method stub

    }

}
