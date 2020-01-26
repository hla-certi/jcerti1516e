package TypesTests;

import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.exceptions.InvalidLogicalTimeInterval;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;
import hla.rti1516e.time.implementations.HLAfloat64TimeFactoryImpl;
import org.junit.Assert;
import org.junit.Test;

public class TimeTypeFloat_Test {
	
	/**************************************************************************************
	 * Create HLAfloat64Time with a HLAfloat64TimeFactory
	 **************************************************************************************/
	@Test
	public void CreateHLAfloat64Time_Initial_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time_initial = float64TimeFactory.makeInitial();

		Assert.assertEquals(float64Time_initial.getValue(), 0, Math.ulp(1.0));
		Assert.assertTrue(float64Time_initial.isInitial());

	}
	
	@Test
	public void CreateHLAfloat64Time_Final_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time_final = float64TimeFactory.makeFinal();

		Assert.assertEquals(float64Time_final.getValue(), Double.MAX_VALUE, Math.ulp(1.0));
		Assert.assertTrue(float64Time_final.isFinal());

	}
	
	@Test
	public void CreateHLAfloat64Time_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time = float64TimeFactory.makeTime(15);

		Assert.assertEquals(float64Time.getValue(), 15, Math.ulp(1.0));
		
	}
	
	/**************************************************************************************
	 * Create HLAfloat64Interval with a HLAfloat64TimeFactory
	 **************************************************************************************/
	@Test
	public void CreateHLAfloat64Interval_Initial_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64Interval_initial = float64TimeFactory.makeZero();

		Assert.assertEquals(float64Interval_initial.getValue(), 0, Math.ulp(1.0));
		Assert.assertTrue(float64Interval_initial.isZero());

	}
	@Test
	public void CreateHLAfloat64Interval_Epsilon_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64Interval_epsilon = float64TimeFactory.makeEpsilon();

		Assert.assertEquals(float64Interval_epsilon.getValue(), Math.ulp(1.0), Math.ulp(1.0));
		Assert.assertTrue(float64Interval_epsilon.isEpsilon());

	}
	@Test
	public void CreateHLAfloat64Interval_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64Interval = float64TimeFactory.makeInterval(15);

		Assert.assertEquals(float64Interval.getValue(), 15, Math.ulp(1.0));
	}
	
	/**************************************************************************************
	 * Add/Substract HLAfloat64Interval to a HLAfloat64Time, check distance aznd compare functions
	 **************************************************************************************/
	@Test
	public void AddTimeandInterval_Test() throws IllegalTimeArithmetic {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time = float64TimeFactory.makeTime(10);
		HLAfloat64Interval float64Interval = float64TimeFactory.makeInterval(5);

		HLAfloat64Time newfloat64Time = float64Time.add(float64Interval);

		Assert.assertEquals(newfloat64Time.getValue(), 15, Math.ulp(1.0));
	}
	
	@Test
	public void SubtractTimeandInterval_Test() throws IllegalTimeArithmetic {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time = float64TimeFactory.makeTime(10);
		HLAfloat64Interval float64Interval = float64TimeFactory.makeInterval(5);

		HLAfloat64Time newfloat64Time = float64Time.subtract(float64Interval);

		Assert.assertEquals(newfloat64Time.getValue(), 5, Math.ulp(1.0));
	}
	
	@Test
	public void Add2Intervals_Test() throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64Interval_1 = float64TimeFactory.makeInterval(5);
		HLAfloat64Interval float64Interval_2 = float64TimeFactory.makeInterval(5);

		HLAfloat64Interval sumItervals = float64Interval_1.add(float64Interval_2);

		Assert.assertEquals(sumItervals.getValue(), 10, Math.ulp(1.0));
	}
	
	@Test
	public void Subtract2Intervals_Test() throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64Interval_1 = float64TimeFactory.makeInterval(10);
		HLAfloat64Interval float64Interval_2 = float64TimeFactory.makeInterval(5);

		HLAfloat64Interval diferenceItervals = float64Interval_1.subtract(float64Interval_2);

		Assert.assertEquals(diferenceItervals.getValue(), 5, Math.ulp(1.0));
	}
	
	@Test
	public void CheckDistanceBetween2Times_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time_1 = float64TimeFactory.makeTime(5);
		HLAfloat64Time float64Time_2 = float64TimeFactory.makeTime(15);

		Assert.assertEquals(float64Time_1.distance(float64Time_2).getValue(), 10, Math.ulp(1.0));
	}
	
	@Test
	public void Compare2Times_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time_1 = float64TimeFactory.makeTime(5);
		HLAfloat64Time float64Time_2 = float64TimeFactory.makeTime(15);

		Assert.assertEquals(float64Time_1.compareTo(float64Time_2), -1);
	}
	
	@Test
	public void Compare2Intervals_Test() {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64Time_1 = float64TimeFactory.makeInterval(5);
		HLAfloat64Interval float64Time_2 = float64TimeFactory.makeInterval(15);

		Assert.assertEquals(float64Time_1.compareTo(float64Time_2), -1);
	}
	
	/**************************************************************************************
	 * Encode and decode
	 * @throws CouldNotEncode 
	 * @throws CouldNotDecode 
	 **************************************************************************************/
	@Test
	public void EncodeAndDecodeTime_Test() throws IllegalTimeArithmetic, CouldNotEncode, CouldNotDecode {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Time float64Time_encode = float64TimeFactory.makeTime(10);
		byte[] buffer = new byte[float64Time_encode.encodedLength()];
		float64Time_encode.encode(buffer, 0);
		//decode
		HLAfloat64Time float64Time_decode = float64TimeFactory.decodeTime(buffer, 0);
		Assert.assertEquals(float64Time_encode.getValue(), float64Time_decode.getValue(), Math.ulp(1.0));
		Assert.assertEquals(float64Time_decode.getValue(), 10, Math.ulp(1.0));
	}
	
	@Test
	public void EncodeAndDecodeInterval_Test() throws IllegalTimeArithmetic, CouldNotEncode, CouldNotDecode {
		HLAfloat64TimeFactory float64TimeFactory= new HLAfloat64TimeFactoryImpl();
		HLAfloat64Interval float64interval_encode = float64TimeFactory.makeInterval(10);
		byte[] buffer = new byte[float64interval_encode.encodedLength()];
		float64interval_encode.encode(buffer, 0);
		//decode
		HLAfloat64Interval float64Interval_decode = float64TimeFactory.decodeInterval(buffer, 0);
		Assert.assertEquals(float64interval_encode.getValue(), float64Interval_decode.getValue(), Math.ulp(1.0));
		Assert.assertEquals(float64Interval_decode.getValue(), 10, Math.ulp(1.0));
	}
}
