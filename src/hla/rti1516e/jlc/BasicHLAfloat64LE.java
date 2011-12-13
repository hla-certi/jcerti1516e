package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAfloat64LE;

public class BasicHLAfloat64LE extends DataElementBase implements HLAfloat64LE {

    private double value;
    
    public BasicHLAfloat64LE() {
        value = 0.0;
    }
    
    public BasicHLAfloat64LE(double d) {
        value = d;
    }
    
    @Override
    public int getOctetBoundary() {
        return 4;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        long d_as_l = Double.doubleToLongBits(value);
        byteWrapper.put((int)(d_as_l >>>  0) & 0xFF);
        byteWrapper.put((int)(d_as_l >>>  8) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 16) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 24) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 32) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 40) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 48) & 0xFF);
        byteWrapper.put((int)(d_as_l >>> 56) & 0xFF);
    }

    @Override
    public int getEncodedLength() {
        return 8;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        long d_as_l;
        d_as_l  = 0L;
        d_as_l += (long)((byteWrapper.get() & 0xFF) <<  0);
        d_as_l += (long)((byteWrapper.get() & 0xFF) <<  8);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 16);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 24);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 32);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 40);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 48);
        d_as_l += (long)((byteWrapper.get() & 0xFF) << 56);
        value = Double.longBitsToDouble(d_as_l);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        
        long d_as_l;
        d_as_l  = 0L;
        d_as_l += (long)((bytes[0] & 0xFF) <<  0);
        d_as_l += (long)((bytes[1] & 0xFF) <<  8);
        d_as_l += (long)((bytes[2] & 0xFF) << 16);
        d_as_l += (long)((bytes[3] & 0xFF) << 24);
        d_as_l += (long)((bytes[4] & 0xFF) << 32);
        d_as_l += (long)((bytes[5] & 0xFF) << 40);
        d_as_l += (long)((bytes[6] & 0xFF) << 48);
        d_as_l += (long)((bytes[7] & 0xFF) << 56);
        value = Double.longBitsToDouble(d_as_l);
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public void setValue(double value) {
        this.value = value;
    }

}
