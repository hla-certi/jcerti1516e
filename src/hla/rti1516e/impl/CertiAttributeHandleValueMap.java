package hla.rti1516e.impl;

import java.util.HashMap;


import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;

public class            CertiAttributeHandleValueMap 
       extends          HashMap<AttributeHandle,byte[]> 
       implements       AttributeHandleValueMap
{
    /**
     * 
     */
    private static final long serialVersionUID = -2182010676190670534L;

    public CertiAttributeHandleValueMap() {
        super();
    }
    
    
    public CertiAttributeHandleValueMap(int capacity) {
        super( capacity );
    }


    @Override
    public ByteWrapper getValueReference(AttributeHandle key) {
        byte[] value = super.get( key );
        if( value == null )
            return null;
        else
            return new ByteWrapper( value );
    }


    @Override
    public ByteWrapper getValueReference(AttributeHandle key, ByteWrapper byteWrapper) {
        byte[] value = super.get( key );
        if( value == null )
            return null;
        byteWrapper.reassign( value, 0, value.length );
        return byteWrapper;
    }
}
