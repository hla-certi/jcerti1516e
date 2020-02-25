package hla.rti1516e.time.implementations;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAfloat64BE;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.jlc.BasicHLAfloat64BEImpl;
import hla.rti1516e.time.HLAfloat64Interval;
import hla.rti1516e.time.HLAfloat64Time;

public class HLAfloat64TimeImpl implements HLAfloat64Time {

	private static final long serialVersionUID = -3473708294638432364L;
	private HLAfloat64BE time;

	/**
	 * Default constructor Set time to 0
	 */
	public HLAfloat64TimeImpl() {
		time = new BasicHLAfloat64BEImpl(0);
	}

	/**
	 * Create a new HLAfloat64Time initialized to a value l
	 * 
	 * @param l : value to iniialized the new HLAfloat64Time
	 */
	public HLAfloat64TimeImpl(double d) {
		time = new BasicHLAfloat64BEImpl(d);
	}

	/**
	 * Check if the time is initial (equal to 0)
	 * 
	 * @return true if the time is inital, false if not
	 */
	@Override
	public boolean isInitial() {
		return time.getValue() == 0;
	}

	/**
	 * Check if the time is final (equal to double.MAX_VALUE)
	 * 
	 * @return true if the time is final, false if not
	 */
	@Override
	public boolean isFinal() {
		return time.getValue() == Double.MAX_VALUE;
	}

	/**
	 * Create a new HLAfloat64Time whitch is the sum of the HLAfloat64Time and the
	 * one in parameter
	 * 
	 * @param val : HLAfloat64Time to add
	 * @return : a new HLAfloat64Time whitch is the sum of the HLAfloat64Time and
	 *         the one in parameter
	 */
	@Override
	public HLAfloat64Time add(HLAfloat64Interval val) throws IllegalTimeArithmetic {
		HLAfloat64Time float64time = new HLAfloat64TimeImpl(time.getValue() + val.getValue());
		return float64time;
	}

	/**
	 * Create a new HLAfloat64Time whitch is the difference of the HLAfloat64Time
	 * and the one in parameter
	 * 
	 * @param val : HLAfloat64Time to add
	 * @return : a new HLAfloat64Time whitch is the difference of the HLAfloat64Time
	 *         and the one in parameter
	 */
	@Override
	public HLAfloat64Time subtract(HLAfloat64Interval val) throws IllegalTimeArithmetic {
		HLAfloat64Time float64time = new HLAfloat64TimeImpl(time.getValue() - val.getValue());
		return float64time;
	}

	/**
	 * Calcul the absolute value between curent time and a other time
	 * 
	 * @return the absolute value between curent time and a other time
	 */
	@Override
	public HLAfloat64Interval distance(HLAfloat64Time val) {
		HLAfloat64Interval float64Interval = new HLAfloat64IntervalImpl(Math.abs(this.getValue() - val.getValue()));
		return float64Interval;
	}

	/**
	 * Compare the value of this HLAfloat64Time the value of the HLAfloat64Time
	 * other
	 * 
	 * @return 0 if this = other, 1 if this > other, -1 if this < other
	 */
	@Override
	public int compareTo(HLAfloat64Time other) {
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

	public static HLAfloat64Time decode(byte[] buffer, int offset) {
		ByteWrapper byteWrapper = new ByteWrapper(buffer, offset);
		HLAfloat64BE float64BE = new BasicHLAfloat64BEImpl();
		try {
			float64BE.decode(byteWrapper); // Use a byte wrapper and not directly the buffer to work with offset too
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new HLAfloat64TimeImpl(float64BE.getValue());
	}

}
