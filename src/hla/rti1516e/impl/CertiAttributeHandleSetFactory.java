package hla.rti1516e.impl;

import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.AttributeHandleSetFactory;

public class CertiAttributeHandleSetFactory implements AttributeHandleSetFactory{

    /**
     * 
     */
    private static final long serialVersionUID = 916985261072486019L;

    @Override
    public AttributeHandleSet create() {
        return new CertiAttributeHandleSet();
    }

}
