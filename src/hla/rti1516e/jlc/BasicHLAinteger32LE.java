package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAinteger32LE;

public class BasicHLAinteger32LE extends DataElementBase implements
        HLAinteger32LE {

    private int value;
    
    public BasicHLAinteger32LE() {
        value = 0;
    }
    
    public BasicHLAinteger32LE(int value) {
        this.value = value;
    }
    
    @Override
    public int getOctetBoundary() {
        return 4;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put((int)(value >>>  0) & 0xFF);
        byteWrapper.put((int)(value >>>  8) & 0xFF);
        byteWrapper.put((int)(value >>> 16) & 0xFF);
        byteWrapper.put((int)(value >>> 24) & 0xFF);
    }

    @Override
    public int getEncodedLength() {
        return 4;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value  = 0;
        value += (int)((byteWrapper.get() & 0xFF) <<  0);
        value += (int)((byteWrapper.get() & 0xFF) <<  8);
        value += (int)((byteWrapper.get() & 0xFF) << 16);
        value += (int)((byteWrapper.get() & 0xFF) << 24);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        value  = 0;
        value += (int)((bytes[0] & 0xFF) <<  0);
        value += (int)((bytes[1] & 0xFF) <<  8);
        value += (int)((bytes[2] & 0xFF) << 16);
        value += (int)((bytes[3] & 0xFF) << 24);
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

}
