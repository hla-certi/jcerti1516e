package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAinteger32BE;

public class BasicHLAinteger32BE extends DataElementBase implements
        HLAinteger32BE {

    private int value;
    
    public BasicHLAinteger32BE() {
        value = 0;
    }
    
    public BasicHLAinteger32BE(int value) {
        this.value = value;
    }
    
    @Override
    public int getOctetBoundary() {
        return 4;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put((int)(value >>> 24) & 0xFF);
        byteWrapper.put((int)(value >>> 16) & 0xFF);
        byteWrapper.put((int)(value >>>  8) & 0xFF);
        byteWrapper.put((int)(value >>>  0) & 0xFF);
    }

    @Override
    public int getEncodedLength() {
        return 4;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value  = 0;
        value += (int)((byteWrapper.get() & 0xFF) << 24);
        value += (int)((byteWrapper.get() & 0xFF) << 16);
        value += (int)((byteWrapper.get() & 0xFF) <<  8);
        value += (int)((byteWrapper.get() & 0xFF) <<  0);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        value  = 0;
        value += (int)((bytes[4] & 0xFF) << 24);
        value += (int)((bytes[5] & 0xFF) << 16);
        value += (int)((bytes[6] & 0xFF) <<  8);
        value += (int)((bytes[7] & 0xFF) <<  0);
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
