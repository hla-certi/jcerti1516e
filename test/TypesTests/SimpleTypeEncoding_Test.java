package TypesTests;

import hla.rti1516e.encoding.*;
import hla.rti1516e.jlc.*;

import org.junit.Assert;
import org.junit.Test;

public class SimpleTypeEncoding_Test {
	
	@Test
	public void TestBasicHLAbyte() {
		byte b = (byte)12;
		HLAbyte hlaByte_encode = new BasicHLAbyteImpl(b);
		try {
			int size = hlaByte_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaByte_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAbyte hlaByte_decode = new BasicHLAbyteImpl();
			hlaByte_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaByte_encode.getValue(), hlaByte_decode.getValue());
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAfloat32BE() {
		float f = (float) 12.0;
		HLAfloat32BE hlaFloat_encode = new BasicHLAfloat32BEImpl(f);
		try {
			int size = hlaFloat_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaFloat_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAfloat32BE hlaFloat_decode = new BasicHLAfloat32BEImpl();
			hlaFloat_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaFloat_encode.getValue(), hlaFloat_decode.getValue(), Math.ulp(1.0));
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAfloat32LE() {
		float f = (float) 12.0;
		HLAfloat32LE hlaFloat_encode = new BasicHLAfloat32LEImpl(f);
		try {
			int size = hlaFloat_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaFloat_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAfloat32LE hlaFloat_decode = new BasicHLAfloat32LEImpl();
			hlaFloat_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaFloat_encode.getValue(), hlaFloat_decode.getValue(), Math.ulp(1.0));
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAfloat64BE() {
			
		double d = 1;
		HLAfloat64BE hlaFloat_encode = new BasicHLAfloat64BEImpl(d);
		try {
			int size = hlaFloat_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaFloat_encode.encode(byteWrapper);
		
			byteWrapper.reset();
			HLAfloat64BE hlaFloat_decode = new BasicHLAfloat64BEImpl();
			hlaFloat_decode.decode(byteWrapper);
			
			
			
			Assert.assertEquals(hlaFloat_encode.getValue(), hlaFloat_decode.getValue(), Math.ulp(1.0));

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void TestBasicHLAfloat64LE() {
		double d =  12.0;
		HLAfloat64LE hlaFloat_encode = new BasicHLAfloat64LEImpl(d);
		try {
			int size = hlaFloat_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaFloat_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAfloat64LE hlaFloat_decode = new BasicHLAfloat64LEImpl();
			hlaFloat_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaFloat_encode.getValue(), hlaFloat_decode.getValue(), Math.ulp(1.0));
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void TestBasicHLAinteger16BE() {
		short s = 2;
		HLAinteger16BE hlaInt_encode = new BasicHLAinteger16BEImpl(s);
		try {
			int size = hlaInt_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaInt_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAinteger16BE hlaInt_decode = new BasicHLAinteger16BEImpl();
			hlaInt_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaInt_encode.getValue(), hlaInt_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void TestBasicHLAinteger16LE() {
		short s = 2;
		HLAinteger16LE hlaInt_encode = new BasicHLAinteger16LEImpl(s);
		try {
			int size = hlaInt_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaInt_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAinteger16LE hlaInt_decode = new BasicHLAinteger16LEImpl();
			hlaInt_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaInt_encode.getValue(), hlaInt_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAinteger32BE() {
		int i = 2;
		HLAinteger32BE hlaInt_encode = new BasicHLAinteger32BEImpl(i);
		try {
			int size = hlaInt_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaInt_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAinteger32BE hlaInt_decode = new BasicHLAinteger32BEImpl();
			hlaInt_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaInt_encode.getValue(), hlaInt_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAinteger32LE() {
		int i = 2;
		HLAinteger32LE hlaInt_encode = new BasicHLAinteger32LEImpl(i);
		try {
			int size = hlaInt_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaInt_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAinteger32LE hlaInt_decode = new BasicHLAinteger32LEImpl();
			hlaInt_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaInt_encode.getValue(), hlaInt_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAinteger64BE() {
		long l = 2;
		HLAinteger64BE hlaInt_encode = new BasicHLAinteger64BEImpl(l);
		try {
			int size = hlaInt_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaInt_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAinteger64BE hlaInt_decode = new BasicHLAinteger64BEImpl();
			hlaInt_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaInt_encode.getValue(), hlaInt_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAinteger64LE() {
		long l = 2;
		HLAinteger64LE hlaInt_encode = new BasicHLAinteger64LEImpl(l);
		try {
			int size = hlaInt_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaInt_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAinteger64LE hlaInt_decode = new BasicHLAinteger64LEImpl();
			hlaInt_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaInt_encode.getValue(), hlaInt_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAoctet() {
		byte b = (byte)12;
		HLAoctet hlaoctet_encode = new BasicHLAoctetImpl(b);
		try {
			int size = hlaoctet_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaoctet_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAoctet hlaoctet_decode = new BasicHLAoctetImpl();
			hlaoctet_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaoctet_encode.getValue(), hlaoctet_decode.getValue());
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAoctetPairBE() {
		short s = 12;
		HLAoctetPairBE hlaoctet_encode = new BasicHLAoctetPairBEImpl(s);
		try {
			int size = hlaoctet_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaoctet_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAoctetPairBE hlaoctet_decode = new BasicHLAoctetPairBEImpl();
			hlaoctet_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaoctet_encode.getValue(), hlaoctet_decode.getValue());
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestBasicHLAoctetPairLE() {
		short s = 12;
		HLAoctetPairLE hlaoctet_encode = new BasicHLAoctetPairLEImpl(s);
		try {
			int size = hlaoctet_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaoctet_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAoctetPairLE hlaoctet_decode = new BasicHLAoctetPairLEImpl();
			hlaoctet_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaoctet_encode.getValue(), hlaoctet_decode.getValue());
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void TestHLAASCIIchar() {
		byte b = (byte) 2;
		HLAASCIIchar hlaasciichar_encode = new HLAASCIIcharImpl(b);
		try {
			int size = hlaasciichar_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size*10);
			hlaasciichar_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAoctetPairLE hlaasciichar_decode = new BasicHLAoctetPairLEImpl();
			hlaasciichar_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaasciichar_encode.getValue(), hlaasciichar_decode.getValue());
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void TestHLAASCIIstring() {
		String s = "test";
		HLAASCIIstring hlaasciistring_encode = new HLAASCIIstringImpl(s);
		try {
			int size = hlaasciistring_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaasciistring_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAASCIIstring hlaasciistring_decode = new HLAASCIIstringImpl();
			hlaasciistring_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaasciistring_encode.getValue(), hlaasciistring_decode.getValue());
			Assert.assertEquals(hlaasciistring_decode.getValue(), s);

			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.assertTrue(false);
		}
	}
	
	
	@Test
	public void TestHLAAboolean() {
		boolean b = true;
		HLAboolean hlaaboolean_encode = new HLAbooleanImpl(b);
		try {
			int size = hlaaboolean_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaaboolean_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAboolean hlaasciiboolean_decode = new HLAbooleanImpl();
			hlaasciiboolean_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaaboolean_encode.getValue(), hlaasciiboolean_decode.getValue());
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestHLAunicodeChar() {
		short s = 2;
		HLAunicodeChar hlaUnicodeChar_encode = new HLAunicodeCharImpl(s);
		try {
			int size = hlaUnicodeChar_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaUnicodeChar_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAunicodeChar hlaUnicodeChar_decode = new HLAunicodeCharImpl();
			hlaUnicodeChar_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaUnicodeChar_encode.getValue(), hlaUnicodeChar_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestHLAunicodeString() {
		String s = "test";
		HLAunicodeString hlaUnicodeString_encode = new HLAunicodeStringImpl(s);
		try {
			int size = hlaUnicodeString_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaUnicodeString_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAunicodeString hlaUnicodeString_decode = new HLAunicodeStringImpl();
			hlaUnicodeString_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaUnicodeString_encode.getValue(), hlaUnicodeString_decode.getValue());

			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void TestHLAopaqueData() {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) 2;
		bytes[1] = (byte) 5;
		
		HLAopaqueData hlaOpaqueData_encode = new HLAopaqueDataImpl(bytes);
		try {
			int size = hlaOpaqueData_encode.getEncodedLength();
			ByteWrapper byteWrapper = new ByteWrapper(size);
			hlaOpaqueData_encode.encode(byteWrapper);
			byteWrapper.reset();
			HLAopaqueData hlaOpaqueData_decode = new HLAopaqueDataImpl();
			hlaOpaqueData_decode.decode(byteWrapper);
			
			Assert.assertEquals(hlaOpaqueData_encode.getValue()[0], hlaOpaqueData_decode.getValue()[0]);
			Assert.assertEquals(hlaOpaqueData_encode.getValue()[1], hlaOpaqueData_decode.getValue()[1]);
			
		} catch (EncoderException encoderException) {
			// TODO Auto-generated catch block
			encoderException.printStackTrace();
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
