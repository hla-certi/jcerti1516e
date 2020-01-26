package hla.rti1516e.impl;

import java.util.HashMap;

import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.encoding.ByteWrapper;

public class CertiParameterHandleValueMap extends HashMap<ParameterHandle, byte[]> implements ParameterHandleValueMap {

	
	private static final long serialVersionUID = -1476912119313086929L;

	 /**
	    * Returns a reference to the value to which this map maps the specified key.
	    * Returns <tt>null</tt> if the map contains no mapping for this key.
	    *
	    * @param key key whose associated value is to be returned.
	    * @return a reference to the value to which this map maps the specified key, or
	    *         <tt>null</tt> if the map contains no mapping for this key.
	    */
	@Override
	public ByteWrapper getValueReference(ParameterHandle key) {
		byte[] value = super.get(key);
		if( value == null )
			return null;
		else
			return new ByteWrapper(value);
	}

	/**
	    * Returns the specified reference updated to the value to which this map
	    * maps the specified key.
	    * Returns <tt>null</tt> if the map contains no mapping for this key.
	    *
	    * @param key key whose associated value is to be returned.
	    * @return the specified reference updated to the value to which this map maps the
	    *         specified key, or <tt>null</tt> if the map contains no mapping for this key.
	    */
	@Override
	public ByteWrapper getValueReference(ParameterHandle key, ByteWrapper byteWrapper) {
		byte[] value = super.get(key);
		if(value == null)
			return null;
		byteWrapper.reassign(value, 0, value.length);
		return byteWrapper;
	}

	
}
