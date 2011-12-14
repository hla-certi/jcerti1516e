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

    
    public HLAASCIIchar createHLAASCIIchar() {
        return new HLAASCIIcharImpl();
    }

    
    public HLAASCIIchar createHLAASCIIchar(byte b) {
        return new HLAASCIIcharImpl(b);
    }

    
    public HLAASCIIstring createHLAASCIIstring() {
        return new HLAASCIIstringImpl();
    }

    
    public HLAASCIIstring createHLAASCIIstring(String s) {
        return new HLAASCIIstringImpl(s);
    }

    
    public HLAboolean createHLAboolean() {
        return new HLAbooleanImpl();
    }

    
    public HLAboolean createHLAboolean(boolean b) {
        return new HLAbooleanImpl(b);
    }

    
    public HLAbyte createHLAbyte() {
        return new BasicHLAbyteImpl();
    }

    
    public HLAbyte createHLAbyte(byte b) {
        return new BasicHLAbyteImpl(b);
    }

    
    public <T extends DataElement> HLAvariantRecord<T> createHLAvariantRecord(
            T discriminant) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public HLAfixedRecord createHLAfixedRecord() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray(
            DataElementFactory<T> factory, int size) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray(
            T... elements) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public HLAfloat32BE createHLAfloat32BE() {
        return new BasicHLAfloat32BEImpl();
    }

    
    public HLAfloat32BE createHLAfloat32BE(float f) {
        return new BasicHLAfloat32BEImpl(f);
    }

    
    public HLAfloat32LE createHLAfloat32LE() {
        return new BasicHLAfloat32LEImpl();
    }

    
    public HLAfloat32LE createHLAfloat32LE(float f) {
        return new BasicHLAfloat32LEImpl(f);
    }

    
    public HLAfloat64BE createHLAfloat64BE() {
        return new BasicHLAfloat64BEImpl();
    }

    
    public HLAfloat64BE createHLAfloat64BE(double d) {
        return new BasicHLAfloat64BEImpl(d);
    }

    
    public HLAfloat64LE createHLAfloat64LE() {
        return new BasicHLAfloat64LEImpl();
    }

    
    public HLAfloat64LE createHLAfloat64LE(double d) {
        return new BasicHLAfloat64LEImpl(d);
    }

    
    public HLAinteger16BE createHLAinteger16BE() {
        return new BasicHLAinteger16BEImpl();
    }

    
    public HLAinteger16BE createHLAinteger16BE(short s) {
        return new BasicHLAinteger16BEImpl(s);
    }

    
    public HLAinteger16LE createHLAinteger16LE() {
        return new BasicHLAinteger16LEImpl();
    }

    
    public HLAinteger16LE createHLAinteger16LE(short s) {
        return new BasicHLAinteger16LEImpl(s);
    }

    
    public HLAinteger32BE createHLAinteger32BE() {
        return new BasicHLAinteger32BEImpl();
    }

    
    public HLAinteger32BE createHLAinteger32BE(int i) {
        return new BasicHLAinteger32BEImpl(i);
    }

    
    public HLAinteger32LE createHLAinteger32LE() {
        return new BasicHLAinteger32LEImpl();
    }

    
    public HLAinteger32LE createHLAinteger32LE(int i) {
        return new BasicHLAinteger32LEImpl(i);
    }

    
    public HLAinteger64BE createHLAinteger64BE() {
        return new BasicHLAinteger64BEImpl();
    }

    
    public HLAinteger64BE createHLAinteger64BE(long l) {
        return new BasicHLAinteger64BEImpl(l);
    }

    
    public HLAinteger64LE createHLAinteger64LE() {
        return new BasicHLAinteger64LEImpl();
    }

    
    public HLAinteger64LE createHLAinteger64LE(long l) {
        return new BasicHLAinteger64LEImpl(l);
    }

    
    public HLAoctet createHLAoctet() {
        return new BasicHLAoctetImpl();
    }

    
    public HLAoctet createHLAoctet(byte b) {
        return new BasicHLAoctetImpl(b);
    }

    
    public HLAoctetPairBE createHLAoctetPairBE() {
       return new BasicHLAoctetPairBEImpl();
    }

    
    public HLAoctetPairBE createHLAoctetPairBE(short s) {
        return new BasicHLAoctetPairBEImpl(s);
    }

    
    public HLAoctetPairLE createHLAoctetPairLE() {
        return new BasicHLAoctetPairLEImpl();
    }

    
    public HLAoctetPairLE createHLAoctetPairLE(short s) {
        return new BasicHLAoctetPairLEImpl(s);
    }

    
    public HLAopaqueData createHLAopaqueData() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public HLAopaqueData createHLAopaqueData(byte[] b) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public HLAunicodeChar createHLAunicodeChar() {
        return new HLAunicodeCharImpl();
    }

    
    public HLAunicodeChar createHLAunicodeChar(short c) {
        return new HLAunicodeCharImpl(c);
    }

    
    public HLAunicodeString createHLAunicodeString() {
        // TODO Auto-generated method stub
        return null;
    }

    
    public HLAunicodeString createHLAunicodeString(String s) {
        // TODO Auto-generated method stub
        return null;
    }

    
    public <T extends DataElement> HLAvariableArray<T> createHLAvariableArray(
            DataElementFactory<T> factory, T... elements) {
        // TODO Auto-generated method stub
        return null;
    }

}
