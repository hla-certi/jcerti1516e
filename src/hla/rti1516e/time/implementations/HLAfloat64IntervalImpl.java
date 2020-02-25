package hla.rti1516e.time.implementations;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.exceptions.InvalidLogicalTimeInterval;
import hla.rti1516e.jlc.BasicHLAfloat64BEImpl;
import hla.rti1516e.time.HLAfloat64Interval;

/**
 * Implementation of HLAfloat64Interval The time value is represented on a
 * HLAfloat64BE value
 */
public class HLAfloat64IntervalImpl implements HLAfloat64Interval {

	private static final long serialVersionUID = -4383667837037765869L;
	private HLAfloat64BE time;

	/**
	 * Default constructor Set time to 0
	 */
	public HLAfloat64IntervalImpl() {
		time = new BasicHLAfloat64BEImpl(0);
	}

	/**
	 * Create a new HLAfloat64Interval initialized to a value l
	 * 
	 * @param l : value to iniialized the new HLAfloat64Interval
	 */
	public HLAfloat64IntervalImpl(double l) {
		time = new BasicHLAfloat64BEImpl(l);
	}

	/**
	 * Compare the value of the HLAfloat64Interval to 0
	 * 
	 * @return true if the time is equal to 0, false if not
	 */
	@Override
	public boolean isZero() {
		return this.time.getValue() == 0;
	}

	/**
	 * Compare the value of the HLAfloat64Interval to epsilon (resoluation de
	 * l'float)
	 * 
	 * @return true if the time is equal to epsilon, false if not
	 */
	@Override
	public boolean isEpsilon() {
		return this.time.getValue() == Math.ulp(1.0);
	}

	/**
	 * Create a new HLAfloat64Interval witch value is the sum of this and addend
	 * 
	 * @param addend : HLAfloat64Interval to add
	 * @returna new HLAfloat64Interval witch value is the sum of this and addend
	 */
	@Override
	public HLAfloat64Interval add(HLAfloat64Interval addend) throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAfloat64Interval float64Interval = new HLAfloat64IntervalImpl(this.time.getValue() + addend.getValue());
		return float64Interval;
	}

	/**
	 * Create a new HLAfloat64Interval witch value is the difference between this
	 * and addend
	 * 
	 * @param subtrahend : HLAfloat64Interval to addsubstract
	 * @returna new HLAfloat64Interval witch value is the sum of this and addend
	 */
	@Override
	public HLAfloat64Interval subtract(HLAfloat64Interval subtrahend)
			throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAfloat64Interval float64Interval = new HLAfloat64IntervalImpl(this.time.getValue() - subtrahend.getValue());
		return float64Interval;
	}

	/**
	 * Compare the value of this HLAfloat64Interval the value of the
	 * HLAfloat64Interval other
	 * 
	 * @return 0 if this = other, 1 if this > other, -1 if this < other
	 */
	@Override
	public int compareTo(HLAfloat64Interval other) {
		if (this.time.getValue() == other.getValue())
			return 0;
		if (this.time.getValue() > other.getValue())
			return 1;
		return -1;
	}

	/**
	 * Calcul the size necesary to encode the object
	 * 
	 * @return the size necesary to encode the object
	 */
	@Override
	public int encodedLength() {
		return this.time.getEncodedLength();
	}

	/**
	 * Encode this object value in a buffer, at the offset in parameter
	 * 
	 * @param buffer : buffer to put values in
	 * @param offset : position to start to put values in the buffer
	 */
	@Override
	public void encode(byte[] buffer, int offset) throws CouldNotEncode {
		ByteWrapper byteWrapper = new ByteWrapper(buffer, offset);
		this.time.encode(byteWrapper);
	}

	/**
	 * Get the time value of the object
	 * 
	 * @return the time value of the object
	 */
	@Override
	public double getValue() {
		return this.time.getValue();
	}

	public static HLAfloat64Interval decode(byte[] buffer, int offset) {
		ByteWrapper byteWrapper = new ByteWrapper(buffer, offset);
		HLAfloat64BE float64BE = new BasicHLAfloat64BEImpl();
		try {
			float64BE.decode(byteWrapper); // Use a byte wrapper and not directly the buffer to work with offset too
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HLAfloat64IntervalImpl(float64BE.getValue());
	}
}
