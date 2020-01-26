package hla.rti1516e.time.implementations;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAinteger64BE;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.jlc.BasicHLAinteger64BEImpl;
import hla.rti1516e.time.HLAinteger64Interval;
import hla.rti1516e.time.HLAinteger64Time;

/**
 * Implementation of HLAinteger64Time
 * The time value is represented on a HLAinteger64BE value
 */
public class HLAinteger64TimeImpl implements HLAinteger64Time{

	private static final long serialVersionUID = 4345161986504497605L;
	
	private HLAinteger64BE time;
	
	/**
	 * Default constructor
	 * Set time to 0
	 */
	public HLAinteger64TimeImpl() {
		time =  new BasicHLAinteger64BEImpl(0);
	}
	
	/**
	 * Create a new HLAinteger64Time initialized to a value l
	 * @param l : value to iniialized the new HLAinteger64Time
	 */
	public HLAinteger64TimeImpl(long l) {
		time =  new BasicHLAinteger64BEImpl(l);
	}

	/**
	 * Check if the time is initial (equal to 0)
	 * @return true if the time is inital, false if not 
	 */
	@Override
	public boolean isInitial() {
		return time.getValue() == 0;
	}

	/**
	 * Check if the time is final (equal to Long.MAX_VALUE)
	 * @return true if the time is final, false if not 
	 */
	@Override
	public boolean isFinal() {
		return time.getValue() == Long.MAX_VALUE;
	}

	/**
	 * Create a new HLAinteger64Time whitch is the sum of the HLAinteger64Time and the one in parameter
	 * @param val : HLAinteger64Time to add 
	 * @return : a new HLAinteger64Time whitch is the sum of the HLAinteger64Time and the one in parameter
	 */
	@Override
	public HLAinteger64Time add(HLAinteger64Interval val) throws IllegalTimeArithmetic {
		HLAinteger64Time integer64time =  new HLAinteger64TimeImpl(time.getValue() + val.getValue());
		return integer64time;
	}

	/**
	 * Create a new HLAinteger64Time whitch is the difference of the HLAinteger64Time and the one in parameter
	 * @param val : HLAinteger64Time to add 
	 * @return : a new HLAinteger64Time whitch is the difference of the HLAinteger64Time and the one in parameter
	 */
	@Override
	public HLAinteger64Time subtract(HLAinteger64Interval val) throws IllegalTimeArithmetic {
		HLAinteger64Time integer64time =  new HLAinteger64TimeImpl(time.getValue() - val.getValue());
		return integer64time;
	}

	/**
	 * Calcul the absolute value between curent time and a other time
	 * @return the absolute value between curent time and a other time
	 */
	@Override
	public HLAinteger64Interval distance(HLAinteger64Time val) {
		HLAinteger64Interval integer64Interval = new HLAinteger64IntervalImpl(Math.abs(this.getValue() - val.getValue()));
		return integer64Interval;
	}

	/**
	 * Compare the value of this HLAinteger64Time the value of the HLAinteger64Time other
	 * @return 0 if this = other, 1 if this > other, -1 if this < other
	 */
	@Override
	public int compareTo(HLAinteger64Time other) {
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
		/*
		if(buffer.length - offset < this.encodedLength()) throw new CouldNotEncode("Length disponible in the buffer is not sufficient");
		long value = this.time.getValue();		
		buffer[offset]   = (byte)(value >>> 56);
		buffer[offset+1] = (byte)(value >>> 48);
		buffer[offset+2] = (byte)(value >>> 40);
		buffer[offset+3] = (byte)(value >>> 32);
		buffer[offset+4] = (byte)(value >>> 24);
		buffer[offset+5] = (byte)(value >>> 16);
		buffer[offset+6] = (byte)(value >>> 8);
		buffer[offset+7] = (byte)(value >>> 0);
		*/
	}

	/**
	 * Get the time value of the object
	 * @returnthe time value of the object
	 */
	@Override
	public long getValue() {
		return this.time.getValue();
	}
	
	/**
	 * Static methode to decode an HLAinteger64Time object
	 * @param buffer : buffer to decode
	 * @param offset : offset to start to decode
	 * @return a new HLAinteger64Interval decoded
	 * @throws DecoderException
	 */
	public static HLAinteger64Time decode( byte[] buffer, int offset )
	{
		ByteWrapper byteWrapper = new ByteWrapper(buffer, offset);
		HLAinteger64BE integer64BE = new BasicHLAinteger64BEImpl();
		try {
			integer64BE.decode(byteWrapper); //Use a byte wrapper and not directly the buffer to work with offset too
		} catch (DecoderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return new HLAinteger64TimeImpl(integer64BE.getValue());
	}

}
