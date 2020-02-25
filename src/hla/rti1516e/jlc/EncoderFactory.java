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

import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.HLAASCIIchar;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAboolean;
import hla.rti1516e.encoding.HLAbyte;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat32LE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAfloat64LE;
import hla.rti1516e.encoding.HLAinteger16BE;
import hla.rti1516e.encoding.HLAinteger16LE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger32LE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.encoding.HLAinteger64LE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAoctetPairBE;
import hla.rti1516e.encoding.HLAoctetPairLE;
import hla.rti1516e.encoding.HLAopaqueData;
import hla.rti1516e.encoding.HLAunicodeChar;
import hla.rti1516e.encoding.HLAunicodeString;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.encoding.HLAvariantRecord;

public class EncoderFactory implements hla.rti1516e.encoding.EncoderFactory {

	public static final EncoderFactory CERTI_ENCODER_FACTORY = new EncoderFactory();

	public static EncoderFactory getInstance() {
		return CERTI_ENCODER_FACTORY;
	}

	@Override
	public HLAASCIIchar createHLAASCIIchar() {
		return new HLAASCIIcharImpl();
	}

	@Override
	public HLAASCIIchar createHLAASCIIchar(byte b) {
		return new HLAASCIIcharImpl(b);
	}

	@Override
	public HLAASCIIstring createHLAASCIIstring() {
		return new HLAASCIIstringImpl();
	}

	@Override
	public HLAASCIIstring createHLAASCIIstring(String s) {
		return new HLAASCIIstringImpl(s);
	}

	@Override
	public HLAboolean createHLAboolean() {
		return new HLAbooleanImpl();
	}

	@Override
	public HLAboolean createHLAboolean(boolean b) {
		return new HLAbooleanImpl(b);
	}

	@Override
	public HLAbyte createHLAbyte() {
		return new BasicHLAbyteImpl();
	}

	@Override
	public HLAbyte createHLAbyte(byte b) {
		return new BasicHLAbyteImpl(b);
	}

	@Override
	public <T extends DataElement> HLAvariantRecord<T> createHLAvariantRecord(T discriminant) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HLAfixedRecord createHLAfixedRecord() {
		return new HLAfixedRecordImpl();
	}

	@Override
	public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray(DataElementFactory<T> factory, int size) {
		return new HLAfixedArrayImpl<>(factory, size);
	}

	@Override
	public <T extends DataElement> HLAfixedArray<T> createHLAfixedArray(T... elements) {
		// FIXME check: is this supposed to work with vaargs elements?
		// see: http://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html
		return new HLAfixedArrayImpl<>(elements);
	}

	@Override
	public HLAfloat32BE createHLAfloat32BE() {
		return new BasicHLAfloat32BEImpl();
	}

	@Override
	public HLAfloat32BE createHLAfloat32BE(float f) {
		return new BasicHLAfloat32BEImpl(f);
	}

	@Override
	public HLAfloat32LE createHLAfloat32LE() {
		return new BasicHLAfloat32LEImpl();
	}

	@Override
	public HLAfloat32LE createHLAfloat32LE(float f) {
		return new BasicHLAfloat32LEImpl(f);
	}

	@Override
	public HLAfloat64BE createHLAfloat64BE() {
		return new BasicHLAfloat64BEImpl();
	}

	@Override
	public HLAfloat64BE createHLAfloat64BE(double d) {
		return new BasicHLAfloat64BEImpl(d);
	}

	@Override
	public HLAfloat64LE createHLAfloat64LE() {
		return new BasicHLAfloat64LEImpl();
	}

	@Override
	public HLAfloat64LE createHLAfloat64LE(double d) {
		return new BasicHLAfloat64LEImpl(d);
	}

	@Override
	public HLAinteger16BE createHLAinteger16BE() {
		return new BasicHLAinteger16BEImpl();
	}

	@Override
	public HLAinteger16BE createHLAinteger16BE(short s) {
		return new BasicHLAinteger16BEImpl(s);
	}

	@Override
	public HLAinteger16LE createHLAinteger16LE() {
		return new BasicHLAinteger16LEImpl();
	}

	@Override
	public HLAinteger16LE createHLAinteger16LE(short s) {
		return new BasicHLAinteger16LEImpl(s);
	}

	@Override
	public HLAinteger32BE createHLAinteger32BE() {
		return new BasicHLAinteger32BEImpl();
	}

	@Override
	public HLAinteger32BE createHLAinteger32BE(int i) {
		return new BasicHLAinteger32BEImpl(i);
	}

	@Override
	public HLAinteger32LE createHLAinteger32LE() {
		return new BasicHLAinteger32LEImpl();
	}

	@Override
	public HLAinteger32LE createHLAinteger32LE(int i) {
		return new BasicHLAinteger32LEImpl(i);
	}

	@Override
	public HLAinteger64BE createHLAinteger64BE() {
		return new BasicHLAinteger64BEImpl();
	}

	@Override
	public HLAinteger64BE createHLAinteger64BE(long l) {
		return new BasicHLAinteger64BEImpl(l);
	}

	@Override
	public HLAinteger64LE createHLAinteger64LE() {
		return new BasicHLAinteger64LEImpl();
	}

	@Override
	public HLAinteger64LE createHLAinteger64LE(long l) {
		return new BasicHLAinteger64LEImpl(l);
	}

	@Override
	public HLAoctet createHLAoctet() {
		return new BasicHLAoctetImpl();
	}

	@Override
	public HLAoctet createHLAoctet(byte b) {
		return new BasicHLAoctetImpl(b);
	}

	@Override
	public HLAoctetPairBE createHLAoctetPairBE() {
		return new BasicHLAoctetPairBEImpl();
	}

	@Override
	public HLAoctetPairBE createHLAoctetPairBE(short s) {
		return new BasicHLAoctetPairBEImpl(s);
	}

	@Override
	public HLAoctetPairLE createHLAoctetPairLE() {
		return new BasicHLAoctetPairLEImpl();
	}

	@Override
	public HLAoctetPairLE createHLAoctetPairLE(short s) {
		return new BasicHLAoctetPairLEImpl(s);
	}

	@Override
	public HLAopaqueData createHLAopaqueData() {
		return new HLAopaqueDataImpl();
	}

	@Override
	public HLAopaqueData createHLAopaqueData(byte[] b) {
		return new HLAopaqueDataImpl(b);
	}

	@Override
	public HLAunicodeChar createHLAunicodeChar() {
		return new HLAunicodeCharImpl();
	}

	@Override
	public HLAunicodeChar createHLAunicodeChar(short c) {
		return new HLAunicodeCharImpl(c);
	}

	@Override
	public HLAunicodeString createHLAunicodeString() {
		return new HLAunicodeStringImpl();
	}

	@Override
	public HLAunicodeString createHLAunicodeString(String s) {
		return new HLAunicodeStringImpl(s);
	}

	@Override
	public <T extends DataElement> HLAvariableArray<T> createHLAvariableArray(DataElementFactory<T> factory,
			T... elements) {

		HLAvariableArray<T> va = new HLAvariableArrayImpl<>(factory, 10);
		// this is varargs + autoboxing
		// see: http://docs.oracle.com/javase/1.5.0/docs/guide/language/varargs.html
		for (T e : elements) {
			va.addElement(e);
		}
		return va;
	}

}
