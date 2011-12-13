package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

public class HLAboolean extends DataElementBase implements
        hla.rti1516e.encoding.HLAboolean {

    private BasicHLAinteger32BE value;
    
    public HLAboolean() {
        value = new BasicHLAinteger32BE(0);
    }
    
    public HLAboolean(boolean b) {
        value = new BasicHLAinteger32BE(b ? 1 :0);
    }
    
    @Override
    public int getOctetBoundary() {
        return value.getOctetBoundary();
    }

    @Override
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
        value.encode(byteWrapper);
    }

    @Override
    public int getEncodedLength() {
        return value.getEncodedLength();
    }

    @Override
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        value.decode(byteWrapper);
    }

    @Override
    public void decode(byte[] bytes) throws DecoderException {
        value.decode(bytes);
    }

    @Override
    public boolean getValue() {
        return (value.getValue()==1);
    }

    @Override
    public void setValue(boolean value) {
        this.value.setValue(value ? 1 : 0);
    }

}
