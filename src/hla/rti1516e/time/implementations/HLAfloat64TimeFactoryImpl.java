package hla.rti1516e.time.implementations;

import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;
import hla.rti1516e.time.HLAfloat64TimeFactory;

/**
 * Implementation of HLAfloat64TimeFactory Used to create HLAfloat64Time and
 * HLAfloat64Interval
 */
public class HLAfloat64TimeFactoryImpl implements HLAfloat64TimeFactory {

	private static final long serialVersionUID = 6796806851371656159L;

	/**
	 * Decode an HLAfloat64Time object and create one with decoded informations
	 * 
	 * @param buffer : buffer witch contains encoded informations
	 * @param offset : offset to start to decode at
	 * @return a new HLAfloat64Time object with decoded informations
	 */
	@Override
	public HLAfloat64Time decodeTime(byte[] buffer, int offset) throws CouldNotDecode {
		HLAfloat64Time float64Time = HLAfloat64TimeImpl.decode(buffer, offset);
		return float64Time;
	}

	/**
	 * Decode an HLAfloat64Interval object and create one with decoded informations
	 * 
	 * @param buffer : buffer witch contains encoded informations
	 * @param offset : offset to start to decode at
	 * @return a new HLAfloat64Interval object with decoded informations
	 */
	@Override
	public HLAfloat64Interval decodeInterval(byte[] buffer, int offset) throws CouldNotDecode {
		HLAfloat64Interval float64Interval = HLAfloat64IntervalImpl.decode(buffer, offset);
		return float64Interval;
	}

	/**
	 * Create a new HLAfloat64Time with value 0
	 * 
	 * @return a new HLAfloat64Time with value 0
	 */
	@Override
	public HLAfloat64Time makeInitial() {
		return new HLAfloat64TimeImpl(0);
	}

	/**
	 * Create a new HLAfloat64Time with value max
	 * 
	 * @return a new HLAfloat64Time with value max
	 */
	@Override
	public HLAfloat64Time makeFinal() {
		return new HLAfloat64TimeImpl(Double.MAX_VALUE);
	}

	/**
	 * Create a new HLAfloat64Time with a specific value
	 * 
	 * @param value to set to the HLAfloat64Time
	 * @return a new HLAfloat64Time with a specific value
	 */
	@Override
	public HLAfloat64Time makeTime(double value) {
		return new HLAfloat64TimeImpl(value);
	}

	/**
	 * Create a new HLAfloat64Interval with value 0
	 * 
	 * @return a new HLAfloat64Interval with value 0
	 */
	@Override
	public HLAfloat64Interval makeZero() {
		return new HLAfloat64IntervalImpl(0);
	}

	/**
	 * Create a new HLAfloat64Interval with value epsilon (resolution of an float,
	 * =1)
	 * 
	 * @return a new HLAfloat64Interval with value epsilon
	 */
	@Override
	public HLAfloat64Interval makeEpsilon() {
		return new HLAfloat64IntervalImpl(Math.ulp(1.0));
	}

	/**
	 * Create a new HLAfloat64Interval with a specific value
	 * 
	 * @param value to set to the HLAfloat64Interval
	 * @return a new HLAfloat64Interval with a specific value
	 */
	@Override
	public HLAfloat64Interval makeInterval(double value) {
		return new HLAfloat64IntervalImpl(value);
	}

	/**
	 * Get the name of the class
	 * 
	 * @return a string with the name of the class
	 */
	@Override
	public String getName() {
		return "HLAfloat64TimeFactory";
	}

}
