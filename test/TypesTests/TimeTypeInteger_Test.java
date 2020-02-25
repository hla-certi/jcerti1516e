package TypesTests;

import org.junit.Assert;
import org.junit.Test;

import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.exceptions.InvalidLogicalTimeInterval;
import hla.rti1516e.time.HLAinteger64Interval;
import hla.rti1516e.time.HLAinteger64Time;
import hla.rti1516e.time.HLAinteger64TimeFactory;
import hla.rti1516e.time.implementations.HLAinteger64TimeFactoryImpl;

public class TimeTypeInteger_Test {

	/**************************************************************************************
	 * Create HLAinteger64Time with a HLAinteger64TimeFactory
	 **************************************************************************************/
	@Test
	public void CreateHLAInteger64Time_Initial_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time_initial = integer64TimeFactory.makeInitial();

		Assert.assertEquals(integer64Time_initial.getValue(), 0);
		Assert.assertTrue(integer64Time_initial.isInitial());

	}

	@Test
	public void CreateHLAInteger64Time_Final_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time_final = integer64TimeFactory.makeFinal();

		Assert.assertEquals(integer64Time_final.getValue(), Long.MAX_VALUE);
		Assert.assertTrue(integer64Time_final.isFinal());

	}

	@Test
	public void CreateHLAInteger64Time_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time = integer64TimeFactory.makeTime(15);

		Assert.assertEquals(integer64Time.getValue(), 15);

	}

	/**************************************************************************************
	 * Create HLAinteger64Interval with a HLAinteger64TimeFactory
	 **************************************************************************************/
	@Test
	public void CreateHLAInteger64Interval_Initial_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64Interval_initial = integer64TimeFactory.makeZero();

		Assert.assertEquals(integer64Interval_initial.getValue(), 0);
		Assert.assertTrue(integer64Interval_initial.isZero());

	}

	@Test
	public void CreateHLAInteger64Interval_Epsilon_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64Interval_epsilon = integer64TimeFactory.makeEpsilon();

		Assert.assertEquals(integer64Interval_epsilon.getValue(), 1);
		Assert.assertTrue(integer64Interval_epsilon.isEpsilon());

	}

	@Test
	public void CreateHLAInteger64Interval_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64Interval = integer64TimeFactory.makeInterval(15);

		Assert.assertEquals(integer64Interval.getValue(), 15);
	}

	/**************************************************************************************
	 * Add/Substract HLAinteger64Interval to a HLAinteger64Time, check distance aznd
	 * compare functions
	 **************************************************************************************/
	@Test
	public void AddTimeandInterval_Test() throws IllegalTimeArithmetic {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time = integer64TimeFactory.makeTime(10);
		HLAinteger64Interval integer64Interval = integer64TimeFactory.makeInterval(5);

		HLAinteger64Time newInteger64Time = integer64Time.add(integer64Interval);

		Assert.assertEquals(newInteger64Time.getValue(), 15);
	}

	@Test
	public void SubtractTimeandInterval_Test() throws IllegalTimeArithmetic {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time = integer64TimeFactory.makeTime(10);
		HLAinteger64Interval integer64Interval = integer64TimeFactory.makeInterval(5);

		HLAinteger64Time newInteger64Time = integer64Time.subtract(integer64Interval);

		Assert.assertEquals(newInteger64Time.getValue(), 5);
	}

	@Test
	public void Add2Intervals_Test() throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64Interval_1 = integer64TimeFactory.makeInterval(5);
		HLAinteger64Interval integer64Interval_2 = integer64TimeFactory.makeInterval(5);

		HLAinteger64Interval sumItervals = integer64Interval_1.add(integer64Interval_2);

		Assert.assertEquals(sumItervals.getValue(), 10);
	}

	@Test
	public void Subtract2Intervals_Test() throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64Interval_1 = integer64TimeFactory.makeInterval(10);
		HLAinteger64Interval integer64Interval_2 = integer64TimeFactory.makeInterval(5);

		HLAinteger64Interval diferenceItervals = integer64Interval_1.subtract(integer64Interval_2);

		Assert.assertEquals(diferenceItervals.getValue(), 5);
	}

	@Test
	public void CheckDistanceBetween2Times_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time_1 = integer64TimeFactory.makeTime(5);
		HLAinteger64Time integer64Time_2 = integer64TimeFactory.makeTime(15);

		Assert.assertEquals(integer64Time_1.distance(integer64Time_2).getValue(), 10);
	}

	@Test
	public void Compare2Times_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time_1 = integer64TimeFactory.makeTime(5);
		HLAinteger64Time integer64Time_2 = integer64TimeFactory.makeTime(15);

		Assert.assertEquals(integer64Time_1.compareTo(integer64Time_2), -1);
	}

	@Test
	public void Compare2Intervals_Test() {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64Time_1 = integer64TimeFactory.makeInterval(5);
		HLAinteger64Interval integer64Time_2 = integer64TimeFactory.makeInterval(15);

		Assert.assertEquals(integer64Time_1.compareTo(integer64Time_2), -1);
	}

	/**************************************************************************************
	 * Encode and decode
	 * 
	 * @throws CouldNotEncode
	 * @throws CouldNotDecode
	 **************************************************************************************/
	@Test
	public void EncodeAndDecodeTime_Test() throws IllegalTimeArithmetic, CouldNotEncode, CouldNotDecode {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Time integer64Time_encode = integer64TimeFactory.makeTime(10);
		byte[] buffer = new byte[integer64Time_encode.encodedLength()];
		integer64Time_encode.encode(buffer, 0);
		// decode
		HLAinteger64Time integer64Time_decode = integer64TimeFactory.decodeTime(buffer, 0);
		Assert.assertEquals(integer64Time_encode.getValue(), integer64Time_decode.getValue());
		Assert.assertEquals(integer64Time_decode.getValue(), 10);
	}

	@Test
	public void EncodeAndDecodeInterval_Test() throws IllegalTimeArithmetic, CouldNotEncode, CouldNotDecode {
		HLAinteger64TimeFactory integer64TimeFactory = new HLAinteger64TimeFactoryImpl();
		HLAinteger64Interval integer64interval_encode = integer64TimeFactory.makeInterval(10);
		byte[] buffer = new byte[integer64interval_encode.encodedLength()];
		integer64interval_encode.encode(buffer, 0);
		// decode
		HLAinteger64Interval integer64Interval_decode = integer64TimeFactory.decodeInterval(buffer, 0);
		Assert.assertEquals(integer64interval_encode.getValue(), integer64Interval_decode.getValue());
		Assert.assertEquals(integer64Interval_decode.getValue(), 10);
	}
}
