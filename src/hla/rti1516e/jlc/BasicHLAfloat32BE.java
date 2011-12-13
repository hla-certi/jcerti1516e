package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAfloat32BE;

public class BasicHLAfloat32BE extends DataElementBase implements HLAfloat32BE {

    private float value;
    
    public BasicHLAfloat32BE() {
        value = 0.0f;
    }
    
    public BasicHLAfloat32BE(float f) {
        value = f;
    }
    
    @Override
    public int getOctetBoundary() {
        return 4;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        int f_as_i = Float.floatToIntBits(value);
        byteWrapper.put((int)(f_as_i >>> 24) & 0xFF);
        byteWrapper.put((int)(f_as_i >>> 16) & 0xFF);
        byteWrapper.put((int)(f_as_i >>>  8) & 0xFF);
        byteWrapper.put((int)(f_as_i >>>  0) & 0xFF);
    }

    @Override
    public int getEncodedLength() {
        return 4;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        int f_as_i;
        f_as_i  = 0;
        f_as_i += (int)((byteWrapper.get() & 0xFF) << 24);
        f_as_i += (int)((byteWrapper.get() & 0xFF) << 16);
        f_as_i += (int)((byteWrapper.get() & 0xFF) <<  8);
        f_as_i += (int)((byteWrapper.get() & 0xFF) <<  0);
        value = Float.intBitsToFloat(f_as_i);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        
        int f_as_i;
        f_as_i  = 0;
        f_as_i += (long)((bytes[4] & 0xFF) << 24);
        f_as_i += (long)((bytes[5] & 0xFF) << 16);
        f_as_i += (long)((bytes[6] & 0xFF) <<  8);
        f_as_i += (long)((bytes[7] & 0xFF) <<  0);
        value = Float.intBitsToFloat(f_as_i);
    }

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }

}
