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

import hla.rti1516e.encoding.*;

public class EncoderFactory implements hla.rti1516e.encoding.EncoderFactory {
    
    public static final EncoderFactory CERTI_ENCODER_FACTORY = new EncoderFactory();
    
    public static EncoderFactory getInstance()
    {
       return CERTI_ENCODER_FACTORY;
    }

    @Override
    public HLAASCIIchar createHLAASCIIchar() {
        return new HLAASCIIchar();
    }

    @Override
    public HLAASCIIchar createHLAASCIIchar(byte b) {
        return new HLAASCIIchar(b);
    }

    @Override
    public HLAASCIIstring createHLAASCIIstring() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAASCIIstring createHLAASCIIstring(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAboolean createHLAboolean() {
        return new HLAboolean();
    }

    @Override
    public HLAboolean createHLAboolean(boolean b) {
        return new HLAboolean(b);
    }

    @Override
    public HLAbyte createHLAbyte() {
        return new BasicHLAbyte();
    }

    @Override
    public HLAbyte createHLAbyte(byte b) {
        return new BasicHLAbyte(b);
    }

    @Override
    public <T extends DataElement> HLAvariantRecord<T> createHLAvariantRecord(
            T discriminant) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAfixedRecord createHLAfixedRecord() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray(
            DataElementFactory<T> factory, int size) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray(
            T... elements) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAfloat32BE createHLAfloat32BE() {
        return new BasicHLAfloat32BE();
    }

    @Override
    public HLAfloat32BE createHLAfloat32BE(float f) {
        return new BasicHLAfloat32BE(f);
    }

    @Override
    public HLAfloat32LE createHLAfloat32LE() {
        return new BasicHLAfloat32LE();
    }

    @Override
    public HLAfloat32LE createHLAfloat32LE(float f) {
        return new BasicHLAfloat32LE(f);
    }

    @Override
    public HLAfloat64BE createHLAfloat64BE() {
        return new BasicHLAfloat64BE();
    }

    @Override
    public HLAfloat64BE createHLAfloat64BE(double d) {
        return new BasicHLAfloat64BE(d);
    }

    @Override
    public HLAfloat64LE createHLAfloat64LE() {
        return new BasicHLAfloat64LE();
    }

    @Override
    public HLAfloat64LE createHLAfloat64LE(double d) {
        return new BasicHLAfloat64LE(d);
    }

    @Override
    public HLAinteger16BE createHLAinteger16BE() {
        return new BasicHLAinteger16BE();
    }

    @Override
    public HLAinteger16BE createHLAinteger16BE(short s) {
        return new BasicHLAinteger16BE(s);
    }

    @Override
    public HLAinteger16LE createHLAinteger16LE() {
        return new BasicHLAinteger16LE();
    }

    @Override
    public HLAinteger16LE createHLAinteger16LE(short s) {
        return new BasicHLAinteger16LE(s);
    }

    @Override
    public HLAinteger32BE createHLAinteger32BE() {
        return new BasicHLAinteger32BE();
    }

    @Override
    public HLAinteger32BE createHLAinteger32BE(int i) {
        return new BasicHLAinteger32BE(i);
    }

    @Override
    public HLAinteger32LE createHLAinteger32LE() {
        return new BasicHLAinteger32LE();
    }

    @Override
    public HLAinteger32LE createHLAinteger32LE(int i) {
        return new BasicHLAinteger32LE(i);
    }

    @Override
    public HLAinteger64BE createHLAinteger64BE() {
        return new BasicHLAinteger64BE();
    }

    @Override
    public HLAinteger64BE createHLAinteger64BE(long l) {
        return new BasicHLAinteger64BE(l);
    }

    @Override
    public HLAinteger64LE createHLAinteger64LE() {
        return new BasicHLAinteger64LE();
    }

    @Override
    public HLAinteger64LE createHLAinteger64LE(long l) {
        return new BasicHLAinteger64LE(l);
    }

    @Override
    public HLAoctet createHLAoctet() {
        return new BasicHLAoctet();
    }

    @Override
    public HLAoctet createHLAoctet(byte b) {
        return new BasicHLAoctet(b);
    }

    @Override
    public HLAoctetPairBE createHLAoctetPairBE() {
       return new BasicHLAoctetPairBE();
    }

    @Override
    public HLAoctetPairBE createHLAoctetPairBE(short s) {
        return new BasicHLAoctetPairBE(s);
    }

    @Override
    public HLAoctetPairLE createHLAoctetPairLE() {
        return new BasicHLAoctetPairLE();
    }

    @Override
    public HLAoctetPairLE createHLAoctetPairLE(short s) {
        return new BasicHLAoctetPairLE(s);
    }

    @Override
    public HLAopaqueData createHLAopaqueData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAopaqueData createHLAopaqueData(byte[] b) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAunicodeChar createHLAunicodeChar() {
        return new HLAunicodeChar();
    }

    @Override
    public HLAunicodeChar createHLAunicodeChar(short c) {
        return new HLAunicodeChar(c);
    }

    @Override
    public HLAunicodeString createHLAunicodeString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HLAunicodeString createHLAunicodeString(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends DataElement> HLAvariableArray<T> createHLAvariableArray(
            DataElementFactory<T> factory, T... elements) {
        // TODO Auto-generated method stub
        return null;
    }

}
