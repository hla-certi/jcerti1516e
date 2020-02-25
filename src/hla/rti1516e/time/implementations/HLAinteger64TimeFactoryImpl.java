package hla.rti1516e.time.implementations;

import hla.rti1516e.exceptions.CouldNotDecode;
import hla.rti1516e.time.HLAinteger64Interval;
import hla.rti1516e.time.HLAinteger64Time;
import hla.rti1516e.time.HLAinteger64TimeFactory;

/**
 * Implementation of HLAinteger64TimeFactory Used to create HLAinteger64Time and
 * HLAinteger64Interval
 */
public class HLAinteger64TimeFactoryImpl implements HLAinteger64TimeFactory {

	private static final long serialVersionUID = 909206047485485047L;

	/**
	 * Decode an HLAinteger64Time object and create one with decoded informations
	 * 
	 * @param buffer : buffer witch contains encoded informations
	 * @param offset : offset to start to decode at
	 * @return a new HLAinteger64Time object with decoded informations
	 */
	@Override
	public HLAinteger64Time decodeTime(byte[] buffer, int offset) throws CouldNotDecode {
		HLAinteger64Time integer64Time = HLAinteger64TimeImpl.decode(buffer, offset);
		return integer64Time;
	}

	/**
	 * Decode an HLAinteger64Interval object and create one with decoded
	 * informations
	 * 
	 * @param buffer : buffer witch contains encoded informations
	 * @param offset : offset to start to decode at
	 * @return a new HLAinteger64Interval object with decoded informations
	 */
	@Override
	public HLAinteger64Interval decodeInterval(byte[] buffer, int offset) throws CouldNotDecode {
		HLAinteger64Interval integer64Interval = HLAinteger64IntervalImpl.decode(buffer, offset);
		return integer64Interval;
	}

	/**
	 * Create a new HLAinteger64Time with value 0
	 * 
	 * @return a new HLAinteger64Time with value 0
	 */
	@Override
	public HLAinteger64Time makeInitial() {
		return new HLAinteger64TimeImpl(0);
	}

	/**
	 * Create a new HLAinteger64Time with value max
	 * 
	 * @return a new HLAinteger64Time with value max
	 */
	@Override
	public HLAinteger64Time makeFinal() {
		return new HLAinteger64TimeImpl(Long.MAX_VALUE);
	}

	/**
	 * Create a new HLAinteger64Time with a specific value
	 * 
	 * @param value to set to the HLAinteger64Time
	 * @return a new HLAinteger64Time with a specific value
	 */
	@Override
	public HLAinteger64Time makeTime(long value) {
		return new HLAinteger64TimeImpl(value);
	}

	/**
	 * Create a new HLAinteger64Interval with value 0
	 * 
	 * @return a new HLAinteger64Interval with value 0
	 */
	@Override
	public HLAinteger64Interval makeZero() {
		return new HLAinteger64IntervalImpl(0);
	}

	/**
	 * Create a new HLAinteger64Interval with value epsilon (resolution of an
	 * integer, =1)
	 * 
	 * @return a new HLAinteger64Interval with value epsilon
	 */
	@Override
	public HLAinteger64Interval makeEpsilon() {
		return new HLAinteger64IntervalImpl(1);
	}

	/**
	 * Create a new HLAinteger64Interval with a specific value
	 * 
	 * @param value to set to the HLAinteger64Interval
	 * @return a new HLAinteger64Interval with a specific value
	 */
	@Override
	public HLAinteger64Interval makeInterval(long value) {
		return new HLAinteger64IntervalImpl(value);
	}

	/**
	 * Get the name of the class
	 * 
	 * @return a string with the name of the class
	 */
	@Override
	public String getName() {
		return "HLAinteger64TimeFactory";
	}

}
