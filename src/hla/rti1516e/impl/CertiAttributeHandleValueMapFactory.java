package hla.rti1516e.impl;

import hla.rti1516e.AttributeHandleValueMap;
import hla.rti1516e.AttributeHandleValueMapFactory;

public class CertiAttributeHandleValueMapFactory implements AttributeHandleValueMapFactory{

    /**
     * 
     */
    private static final long serialVersionUID = 5232339402334242600L;

    @Override
    public AttributeHandleValueMap create(int capacity) {
        return new CertiAttributeHandleValueMap(capacity);
    }
}
