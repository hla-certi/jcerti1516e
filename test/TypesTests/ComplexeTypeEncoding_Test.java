package TypesTests;

import org.junit.Assert;
import org.junit.Test;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.HLAfixedArray;
import hla.rti1516e.encoding.HLAfixedRecord;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.encoding.HLAinteger32BE;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.encoding.HLAinteger64LE;
import hla.rti1516e.encoding.HLAoctet;
import hla.rti1516e.encoding.HLAvariableArray;
import hla.rti1516e.encoding.HLAvariantRecord;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.BasicHLAfloat64BEImpl;
import hla.rti1516e.jlc.BasicHLAinteger32BEImpl;
import hla.rti1516e.jlc.BasicHLAinteger64BEImpl;
import hla.rti1516e.jlc.BasicHLAinteger64LEImpl;
import hla.rti1516e.jlc.BasicHLAoctetImpl;
import hla.rti1516e.jlc.EncoderFactory;
import hla.rti1516e.jlc.HLAfixedArrayImpl;
import hla.rti1516e.jlc.HLAfixedRecordImpl;
import hla.rti1516e.jlc.HLAvariableArrayImpl;
import hla.rti1516e.jlc.HLAvariantRecordImpl;

public class ComplexeTypeEncoding_Test {

	private EncoderFactory encoderFactory;
	DataElementFactory<HLAinteger64BE> integer64BE_Factory;
	DataElementFactory<HLAfloat64BE> float64BE_Factory;
	DataElementFactory<HLAfloat32BE> float32BE_Factory;
	DataElementFactory<DataElement> dataElement_Factory;

	@Test
	public void FixedArrayTest_Integer64BE() {

		try {
			encoderFactory = EncoderFactory.getInstance();
			integer64BE_Factory = new DataElementFactory<HLAinteger64BE>() {
				@Override
				public HLAinteger64BE createElement(int index) {
					return encoderFactory.createHLAinteger64BE();
				}
			};

			HLAinteger64BE integer64BE_1_encode = new BasicHLAinteger64BEImpl(1);
			HLAinteger64BE integer64BE_2_encode = new BasicHLAinteger64BEImpl(2);

			HLAfixedArray<HLAinteger64BE> hlafixedArray_encode = new HLAfixedArrayImpl<>(
					integer64BE_Factory, 2);
			((HLAfixedArrayImpl<HLAinteger64BE>) hlafixedArray_encode).addElement(integer64BE_1_encode);
			((HLAfixedArrayImpl<HLAinteger64BE>) hlafixedArray_encode).addElement(integer64BE_2_encode);

			ByteWrapper byteWrapper = new ByteWrapper(hlafixedArray_encode.getEncodedLength());
			hlafixedArray_encode.encode(byteWrapper);

			byteWrapper.reset();
			HLAinteger64BE integer64BE_1_decode = new BasicHLAinteger64BEImpl();
			HLAinteger64BE integer64BE_2_decode = new BasicHLAinteger64BEImpl();
			HLAinteger64BE[] data = { integer64BE_1_decode, integer64BE_2_decode };
			HLAfixedArray<HLAinteger64BE> hlafixedArray_decode = new HLAfixedArrayImpl<>(data);

			hlafixedArray_decode.decode(byteWrapper);

			Assert.assertEquals(hlafixedArray_encode.get(0).getValue(), hlafixedArray_decode.get(0).getValue());
			Assert.assertEquals(hlafixedArray_decode.get(0).getValue(), 1);

			Assert.assertEquals(hlafixedArray_encode.get(1).getValue(), hlafixedArray_decode.get(1).getValue());
			Assert.assertEquals(hlafixedArray_decode.get(1).getValue(), 2);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

	}

	@Test
	public void FixedArrayTest_Float64BE() {

		try {
			encoderFactory = EncoderFactory.getInstance();
			float64BE_Factory = new DataElementFactory<HLAfloat64BE>() {
				@Override
				public HLAfloat64BE createElement(int index) {
					return encoderFactory.createHLAfloat64BE();
				}
			};

			HLAfloat64BE float64BE_1_encode = new BasicHLAfloat64BEImpl(-1);
			HLAfloat64BE float64BE_2_encode = new BasicHLAfloat64BEImpl((float) 3.6);

			HLAfixedArray<HLAfloat64BE> hlafixedArray_encode = new HLAfixedArrayImpl<>(float64BE_Factory,
					2);
			((HLAfixedArrayImpl<HLAfloat64BE>) hlafixedArray_encode).addElement(float64BE_1_encode);
			((HLAfixedArrayImpl<HLAfloat64BE>) hlafixedArray_encode).addElement(float64BE_2_encode);

			ByteWrapper byteWrapper = new ByteWrapper(hlafixedArray_encode.getEncodedLength());
			hlafixedArray_encode.encode(byteWrapper);

			byteWrapper.reset();
			HLAfloat64BE float64BE_1_decode = new BasicHLAfloat64BEImpl();
			HLAfloat64BE float64BE_2_decode = new BasicHLAfloat64BEImpl();
			HLAfloat64BE[] data = { float64BE_1_decode, float64BE_2_decode };
			HLAfixedArray<HLAfloat64BE> hlafixedArray_decode = new HLAfixedArrayImpl<>(data);

			hlafixedArray_decode.decode(byteWrapper);

			Assert.assertEquals(hlafixedArray_encode.get(0).getValue(), hlafixedArray_decode.get(0).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlafixedArray_encode.get(0).getValue(), hlafixedArray_decode.get(0).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlafixedArray_decode.get(0).getValue(), -1, Math.ulp(1.0));

			Assert.assertEquals(hlafixedArray_encode.get(1).getValue(), hlafixedArray_decode.get(1).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlafixedArray_decode.get(1).getValue(), (float) 3.6, Math.ulp(1.0));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}

	}

	@Test
	public void FixedArrayTest_Float32BE() {

		try {
			encoderFactory = EncoderFactory.getInstance();
			float32BE_Factory = new DataElementFactory<HLAfloat32BE>() {
				@Override
				public HLAfloat32BE createElement(int index) {
					return encoderFactory.createHLAfloat32BE();
				}
			};

			HLAfloat32BE float32BE_1_encode = new BasicHLAfloat32BEImpl(-1);
			HLAfloat32BE float32BE_2_encode = new BasicHLAfloat32BEImpl((float) 2.5);

			HLAfixedArray<HLAfloat32BE> hlafixedArray_encode = new HLAfixedArrayImpl<>(float32BE_Factory,
					2);
			((HLAfixedArrayImpl<HLAfloat32BE>) hlafixedArray_encode).addElement(float32BE_1_encode);
			((HLAfixedArrayImpl<HLAfloat32BE>) hlafixedArray_encode).addElement(float32BE_2_encode);

			ByteWrapper byteWrapper = new ByteWrapper(hlafixedArray_encode.getEncodedLength());
			hlafixedArray_encode.encode(byteWrapper);

			byteWrapper.reset();
			HLAfloat32BE float32BE_1_decode = new BasicHLAfloat32BEImpl();
			HLAfloat32BE float32BE_2_decode = new BasicHLAfloat32BEImpl();
			HLAfloat32BE[] data_decode = { float32BE_1_decode, float32BE_2_decode };
			HLAfixedArray<HLAfloat32BE> hlafixedArray_decode = new HLAfixedArrayImpl<>(data_decode);
			hlafixedArray_decode.decode(byteWrapper);

			Assert.assertEquals(hlafixedArray_encode.get(0).getValue(), hlafixedArray_decode.get(0).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlafixedArray_decode.get(0).getValue(), -1, Math.ulp(1.0));
			Assert.assertEquals(hlafixedArray_encode.get(1).getValue(), hlafixedArray_decode.get(1).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlafixedArray_decode.get(1).getValue(), (float) 2.5, Math.ulp(1.0));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void VariableArrayTest_Float32BE() {

		try {
			encoderFactory = EncoderFactory.getInstance();
			float32BE_Factory = new DataElementFactory<HLAfloat32BE>() {
				@Override
				public HLAfloat32BE createElement(int index) {
					return encoderFactory.createHLAfloat32BE();
				}
			};

			HLAfloat32BE float32BE_1_encode = new BasicHLAfloat32BEImpl(1);
			HLAfloat32BE float32BE_2_encode = new BasicHLAfloat32BEImpl(2);

			HLAvariableArray<HLAfloat32BE> hlavariableArray_encode = new HLAvariableArrayImpl<>(
					float32BE_Factory, 2);
			hlavariableArray_encode.addElement(float32BE_1_encode);
			hlavariableArray_encode.addElement(float32BE_2_encode);

			ByteWrapper byteWrapper = new ByteWrapper(hlavariableArray_encode.getEncodedLength());
			hlavariableArray_encode.encode(byteWrapper);

			byteWrapper.reset();
			HLAvariableArray<HLAfloat32BE> hlavariableArray_decode = new HLAvariableArrayImpl<>(
					float32BE_Factory, 2);
			hlavariableArray_decode.decode(byteWrapper);

			Assert.assertEquals(hlavariableArray_encode.get(0).getValue(), hlavariableArray_decode.get(0).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlavariableArray_encode.get(1).getValue(), hlavariableArray_decode.get(1).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(1, hlavariableArray_decode.get(0).getValue(), Math.ulp(1.0));
			Assert.assertEquals(2, hlavariableArray_decode.get(1).getValue(), Math.ulp(1.0));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void VariableArrayTest_Float64BE() {

		try {
			encoderFactory = EncoderFactory.getInstance();
			float64BE_Factory = new DataElementFactory<HLAfloat64BE>() {
				@Override
				public HLAfloat64BE createElement(int index) {
					return encoderFactory.createHLAfloat64BE();
				}
			};

			HLAfloat64BE float64BE_1_encode = new BasicHLAfloat64BEImpl(1);
			HLAfloat64BE float64BE_2_encode = new BasicHLAfloat64BEImpl(2);

			HLAvariableArray<HLAfloat64BE> hlavariableArray_encode = new HLAvariableArrayImpl<>(
					float64BE_Factory, 2);
			hlavariableArray_encode.addElement(float64BE_1_encode);
			hlavariableArray_encode.addElement(float64BE_2_encode);

			ByteWrapper byteWrapper = new ByteWrapper(hlavariableArray_encode.getEncodedLength());
			hlavariableArray_encode.encode(byteWrapper);

			byteWrapper.reset();
			HLAvariableArray<HLAfloat64BE> hlavariableArray_decode = new HLAvariableArrayImpl<>(
					float64BE_Factory, 2);
			hlavariableArray_decode.decode(byteWrapper);

			Assert.assertEquals(hlavariableArray_encode.get(0).getValue(), hlavariableArray_decode.get(0).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(hlavariableArray_encode.get(1).getValue(), hlavariableArray_decode.get(1).getValue(),
					Math.ulp(1.0));
			Assert.assertEquals(1, hlavariableArray_decode.get(0).getValue(), Math.ulp(1.0));
			Assert.assertEquals(2, hlavariableArray_decode.get(1).getValue(), Math.ulp(1.0));

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void VariableArrayTest_VariantRecord() {

		try {
			// Structure to encode
			HLAoctet discriminantEncoder_1 = new BasicHLAoctetImpl((byte) 1);
			HLAinteger32BE valueEncoder_1 = new BasicHLAinteger32BEImpl((byte) -5);
			HLAvariantRecord<HLAoctet> variantEncoder_1 = new HLAvariantRecordImpl<>();
			variantEncoder_1.setVariant(discriminantEncoder_1, valueEncoder_1);

			HLAoctet discriminantEncoder_2 = new BasicHLAoctetImpl((byte) 2);
			HLAinteger32BE valueEncoder_2 = new BasicHLAinteger32BEImpl((byte) 3);
			HLAvariantRecord<HLAoctet> variantEncoder_2 = new HLAvariantRecordImpl<>();
			variantEncoder_2.setVariant(discriminantEncoder_2, valueEncoder_2);

			HLAvariableArray<HLAvariantRecord<HLAoctet>> hlavariableArray_encode = new HLAvariableArrayImpl<>();
			hlavariableArray_encode.addElement(variantEncoder_1);
			hlavariableArray_encode.addElement(variantEncoder_2);

			ByteWrapper byteWrapper = new ByteWrapper(hlavariableArray_encode.getEncodedLength());
			hlavariableArray_encode.encode(byteWrapper);
			byteWrapper.reset();

			// Structure to decode
			HLAoctet discriminantDecoder_1 = new BasicHLAoctetImpl((byte) 1);
			HLAinteger32BE valueDecoder_1 = new BasicHLAinteger32BEImpl((byte) -5);
			HLAvariantRecord<HLAoctet> variantDecoder_1 = new HLAvariantRecordImpl<>();
			variantDecoder_1.setVariant(discriminantDecoder_1, valueDecoder_1);

			HLAoctet discriminantDecoder_2 = new BasicHLAoctetImpl((byte) 2);
			HLAinteger32BE valueDecoder_2 = new BasicHLAinteger32BEImpl((byte) 3);
			HLAvariantRecord<HLAoctet> variantDecoder_2 = new HLAvariantRecordImpl<>();
			variantDecoder_2.setVariant(discriminantDecoder_2, valueDecoder_2);

			HLAvariableArray<HLAvariantRecord<HLAoctet>> hlavariableArray_decode = new HLAvariableArrayImpl<>();
			hlavariableArray_decode.addElement(variantDecoder_1);
			hlavariableArray_decode.addElement(variantDecoder_2);
			hlavariableArray_decode.decode(byteWrapper);

			Assert.assertEquals(valueEncoder_1.getValue(), valueDecoder_1.getValue());
			Assert.assertEquals(valueEncoder_2.getValue(), valueDecoder_2.getValue());

			Assert.assertEquals(valueEncoder_1.getValue(), (byte) -5);
			Assert.assertEquals(valueEncoder_2.getValue(), (byte) 3);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

	@Test
	public void FixedRecordTest() {

		try {
			// Encode structure
			HLAfloat32BE float32BE_encode = new BasicHLAfloat32BEImpl((float) 1.5);
			HLAinteger32BE integer32BE_encode = new BasicHLAinteger32BEImpl(2);
			HLAinteger64LE integer64LE_encode = new BasicHLAinteger64LEImpl(10);
			// HLAfloat64LE float64LE_encode = new BasicHLAfloat64LEImpl((float) 1.2);

			HLAfixedRecord hlafixedRecord_encode = new HLAfixedRecordImpl();
			hlafixedRecord_encode.add(float32BE_encode);
			hlafixedRecord_encode.add(integer32BE_encode);
			hlafixedRecord_encode.add(integer64LE_encode);
			// hlafixedRecord_encode.add(float64LE_encode);

			ByteWrapper byteWrapper = new ByteWrapper(hlafixedRecord_encode.getEncodedLength());
			hlafixedRecord_encode.encode(byteWrapper);

			byteWrapper.reset();

			// Decode structure
			HLAfloat32BE float32BE_decode = new BasicHLAfloat32BEImpl();
			HLAinteger32BE integer32BE_decode = new BasicHLAinteger32BEImpl();
			HLAinteger64LE integer64LE_decode = new BasicHLAinteger64LEImpl();
			// HLAfloat64LE float64LE_decode = new BasicHLAfloat64LEImpl(10);

			HLAfixedRecord hlafixedRecord_decode = new HLAfixedRecordImpl();
			hlafixedRecord_decode.add(float32BE_decode);
			hlafixedRecord_decode.add(integer32BE_decode);
			hlafixedRecord_decode.add(integer64LE_decode);
			// hlafixedRecord_decode.add(float64LE_decode);

			hlafixedRecord_decode.decode(byteWrapper);

			Assert.assertEquals(((HLAfloat32BE) hlafixedRecord_encode.get(0)).getValue(),
					((HLAfloat32BE) hlafixedRecord_decode.get(0)).getValue(), Math.ulp(1.0));
			Assert.assertEquals(((HLAinteger32BE) hlafixedRecord_encode.get(1)).getValue(),
					((HLAinteger32BE) hlafixedRecord_decode.get(1)).getValue());
			Assert.assertEquals(((HLAinteger64LE) hlafixedRecord_encode.get(2)).getValue(),
					((HLAinteger64LE) hlafixedRecord_decode.get(2)).getValue());
			// Assert.assertEquals(((HLAinteger64LE)
			// hlafixedRecord_encode.get(3)).getValue(), ((HLAinteger64LE)
			// hlafixedRecord_decode.get(3)).getValue());

			Assert.assertEquals(((HLAfloat32BE) hlafixedRecord_decode.get(0)).getValue(), (float) 1.5, Math.ulp(1.0));
			Assert.assertEquals(((HLAinteger32BE) hlafixedRecord_decode.get(1)).getValue(), 2);
			Assert.assertEquals(((HLAinteger64LE) hlafixedRecord_decode.get(2)).getValue(), 10);
			// Assert.assertEquals(((HLAinteger64LE)
			// hlafixedRecord_decode.get(3)).getValue(), (float) 1.2);

		} catch (Exception e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test
	public void VariantRecordTest_1value() {
		try {
			HLAoctet discriminantEncoder_1 = new BasicHLAoctetImpl((byte) 1);
			HLAinteger32BE valueEncoder_1 = new BasicHLAinteger32BEImpl((byte) -5);

			HLAvariantRecord<HLAoctet> variantEncoder = new HLAvariantRecordImpl<>();
			variantEncoder.setVariant(discriminantEncoder_1, valueEncoder_1);

			// Encode discriminant2
			ByteWrapper byteWrapper_1 = new ByteWrapper(variantEncoder.getEncodedLength());
			variantEncoder.encode(byteWrapper_1);
			byteWrapper_1.reset();

			HLAoctet discriminantDecoder_1 = new BasicHLAoctetImpl();
			HLAinteger32BE valueDecoder_1 = new BasicHLAinteger32BEImpl();

			HLAvariantRecord<HLAoctet> variantDecoder = new HLAvariantRecordImpl<>();
			variantDecoder.setVariant(discriminantDecoder_1, valueDecoder_1);

			variantDecoder.decode(byteWrapper_1);

			Assert.assertEquals(valueEncoder_1.getValue(), valueDecoder_1.getValue());
			Assert.assertEquals(valueDecoder_1.getValue(), -5);

		} catch (Exception e) {
			Assert.assertTrue(false);
			e.printStackTrace();
		}
	}

	@Test
	public void VariantRecordTest_2values() {
		try {
			HLAoctet discriminantEncoder_1 = new BasicHLAoctetImpl((byte) 1);
			HLAinteger32BE valueEncoder_1 = new BasicHLAinteger32BEImpl((byte) -5);
			HLAoctet discriminantEncoder_2 = new BasicHLAoctetImpl((byte) 2);
			HLAinteger32BE valueEncoder_2 = new BasicHLAinteger32BEImpl((byte) 6);

			HLAvariantRecord<HLAoctet> variantEncoder = new HLAvariantRecordImpl<>();
			variantEncoder.setVariant(discriminantEncoder_1, valueEncoder_1);
			variantEncoder.setVariant(discriminantEncoder_2, valueEncoder_2);
			variantEncoder.setDiscriminant(discriminantEncoder_1);

			// Encode discriminant 1
			ByteWrapper byteWrapper_1 = new ByteWrapper(variantEncoder.getEncodedLength());
			variantEncoder.encode(byteWrapper_1);
			byteWrapper_1.reset();

			// Encode discriminant 2
			ByteWrapper byteWrapper_2 = new ByteWrapper(variantEncoder.getEncodedLength());
			variantEncoder.setDiscriminant(discriminantEncoder_2);
			variantEncoder.encode(byteWrapper_2);
			byteWrapper_2.reset();

			HLAoctet discriminantDecoder_1 = new BasicHLAoctetImpl();
			HLAinteger32BE valueDecoder_1 = new BasicHLAinteger32BEImpl();

			HLAoctet discriminantDecoder_2 = new BasicHLAoctetImpl();
			HLAinteger32BE valueDecoder_2 = new BasicHLAinteger32BEImpl();

			HLAvariantRecord<HLAoctet> variantDecoder = new HLAvariantRecordImpl<>();

			// Decode value 1
			variantDecoder.setVariant(discriminantDecoder_1, valueDecoder_1);
			variantDecoder.decode(byteWrapper_1);
			Assert.assertEquals(valueEncoder_1.getValue(), valueDecoder_1.getValue());
			Assert.assertEquals(valueDecoder_1.getValue(), -5);

			// decode value 2
			variantDecoder.setVariant(discriminantDecoder_2, valueDecoder_2);
			variantDecoder.decode(byteWrapper_2);
			Assert.assertEquals(valueEncoder_2.getValue(), valueDecoder_2.getValue());
			Assert.assertEquals(valueDecoder_2.getValue(), 6);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}

}
