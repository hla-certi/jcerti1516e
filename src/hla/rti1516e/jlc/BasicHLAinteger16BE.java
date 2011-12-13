package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;
import hla.rti1516e.encoding.HLAinteger16BE;

public class BasicHLAinteger16BE extends DataElementBase implements
        HLAinteger16BE {

    private short value;
    
    public BasicHLAinteger16BE() {
        value = 0;
    }
    
    public BasicHLAinteger16BE(short value) {
        this.value = value;
    }
    
    @Override
    public int getOctetBoundary() {
        return 2;
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        byteWrapper.align(getOctetBoundary());
        byteWrapper.put((int)(value >>>  8) & 0xFF);
        byteWrapper.put((int)(value >>>  0) & 0xFF);
    }

    @Override
    public int getEncodedLength() {
        return 2;
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        value  = 0;
        value += (short)((byteWrapper.get() & 0xFF) <<  8);
        value += (short)((byteWrapper.get() & 0xFF) <<  0);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        value  = 0;
        value += (short)((bytes[0] & 0xFF) <<  8);
        value += (short)((bytes[1] & 0xFF) <<  0);
    }

    @Override
    public short getValue() {
        return value;
    }

    @Override
    public void setValue(short value) {
        this.value = value;
    }

}
