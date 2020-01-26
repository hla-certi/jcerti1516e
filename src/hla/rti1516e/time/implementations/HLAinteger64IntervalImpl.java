package hla.rti1516e.time.implementations;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.exceptions.InvalidLogicalTimeInterval;
import hla.rti1516e.jlc.BasicHLAinteger64BEImpl;
import hla.rti1516e.time.HLAinteger64Interval;

/**
 * Implementation of HLAinteger64Interval
 * The time value is represented on a HLAinteger64BE value
 */
public class HLAinteger64IntervalImpl implements HLAinteger64Interval{

	private static final long serialVersionUID = 6701723749006756029L;
	
	private HLAinteger64BE time;

	/**
	 * Default constructor
	 * Set time to 0
	 */
	public HLAinteger64IntervalImpl() {
		time = new BasicHLAinteger64BEImpl(0);
 	}
	
	/**
	 * Create a new HLAinteger64Interval initialized to a value l
	 * @param l : value to iniialized the new HLAinteger64Interval
	 */
	public HLAinteger64IntervalImpl(long l) {
		time = new BasicHLAinteger64BEImpl(l);
 	}
	
	/**
	 * Compare the value of the HLAinteger64Interval to 0
	 * @return true if the time is equal to 0, false if not
	 */
	@Override
	public boolean isZero() {
		return this.time.getValue() == 0;
	}

	/**
	 * Compare the value of the HLAinteger64Interval to epsilon (resolution de l'integer)
	 * @return true if the time is equal to epsilon, false if not
	 */
	@Override
	public boolean isEpsilon() {
		return this.time.getValue() == 1;
	}

	/**
	 * Create a new HLAinteger64Interval witch value is the sum of this and addend
	 * @param addend : HLAinteger64Interval to add
	 * @returna new HLAinteger64Interval witch value is the sum of this and addend
	 */
	@Override
	public HLAinteger64Interval add(HLAinteger64Interval addend)
			throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAinteger64Interval integer64Interval =  new HLAinteger64IntervalImpl(this.time.getValue() + addend.getValue());
		return integer64Interval;
	}

	/**
	 * Create a new HLAinteger64Interval witch value is the difference between this and addend
	 * @param subtrahend : HLAinteger64Interval to addsubstract
	 * @returna new HLAinteger64Interval witch value is the sum of this and addend
	 */
	@Override
	public HLAinteger64Interval subtract(HLAinteger64Interval subtrahend)
			throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
		HLAinteger64Interval integer64Interval =  new HLAinteger64IntervalImpl(this.time.getValue() - subtrahend.getValue());
		return integer64Interval;
	}

	/**
	 * Compare the value of this HLAinteger64Interval the value of the HLAinteger64Interval other
	 * @return 0 if this = other, 1 if this > other, -1 if this < other
	 */
	@Override
	public int compareTo(HLAinteger64Interval other) {
		if(this.time.getValue() == other.getValue()) return 0;
		if(this.time.getValue() > other.getValue()) return 1;
		return -1;
	}

	/**
     * Calcul the size necesary to encode the object
     * @return the size necesary to encode the object
     */
	@Override
	public int encodedLength() {
		return this.time.getEncodedLength();
	}

	/**
	 * Encode this object value in a buffer, at the offset in parameter
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
	 * @returnthe time value of the object
	 */
	@Override
	public long getValue() {
		// TODO Auto-generated method stub
		return this.time.getValue();
	}
	
	public static HLAinteger64Interval decode( byte[] buffer, int offset )
	{
		ByteWrapper byteWrapper = new ByteWrapper(buffer, offset);
		HLAinteger64BE integer64BE = new BasicHLAinteger64BEImpl();
		try {
			integer64BE.decode(byteWrapper); //Use a byte wrapper and not directly the buffer to work with offset too
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return new HLAinteger64IntervalImpl(integer64BE.getValue());
	}

}
