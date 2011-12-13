package hla.rti1516e.jlc;

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.EncoderException;

public abstract class DataElementBase implements DataElement {

    @Override
    public byte[] toByteArray() throws EncoderException {
        ByteWrapper bw = new ByteWrapper(getEncodedLength());
        encode(bw);
        return bw.array();
    }

}
