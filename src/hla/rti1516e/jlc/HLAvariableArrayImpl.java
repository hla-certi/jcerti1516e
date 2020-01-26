// ----------------------------------------------------------------------------
// CERTI - HLA RunTime Infrastructure
// Copyright (C) 2011 Eric Noulard
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package hla.rti1516e.jlc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import hla.rti1516e.encoding.*;

/**
 * HLAvariableArrayImpl is the implementation of type HLAvariableArray
 * It is an array with a variable size, wich contains elements with the same type T
 * @param <T> Type of elements of the array. Can be a simple type or a complexe type
 */
public class HLAvariableArrayImpl<T extends DataElement> extends DataElementBase
    implements hla.rti1516e.encoding.HLAvariableArray<T> {

    private ArrayList<T>          values;
    private DataElementFactory<T> efactory;

    /**
     * Empty contructor of a HLAvariableArray
     */
    public HLAvariableArrayImpl() {
        values  = new ArrayList<T>();
        efactory = null;
    }
    
    /**
     * Constructor of HLAvariableArray with an array of elements T
     * @param elements : array of elements of type T
     */
    public HLAvariableArrayImpl(T[] elements) {
        values = new ArrayList<T>(elements.length);
        values.addAll(Arrays.asList(elements));
        efactory = null;
    }
    
    /**
     * Constructor of HLAvariableArray with a factory and an array of elements
     * @param factory : factory to create elements of type T
     * @param elements : array of elements of type T
     */
    public HLAvariableArrayImpl(DataElementFactory<T> factory, T[] elements) {
        values = new ArrayList<T>(elements.length);
        values.addAll(Arrays.asList(elements));
        efactory = factory;
    }
    
    /**
     * Constructor of HLAvariableArray with a factory and a size
     * @param factory : factory to create elements of type T
     * @param size : number of elements in the array
     */
    public HLAvariableArrayImpl(DataElementFactory<T> factory, int size) {
        values   = new ArrayList<T>(size);
        efactory = factory;
    }
    
    
    /**
	 * Calcul the octet boundary 
	 * HLAVariableArray octet boundary is the max of all the values's octet boundary
	 * If the array is empty (during decoding), octet boundray is calculated with the size of an object T
	 * @return Value of octet boundary
	 */
    public int getOctetBoundary() {
        /* at least 4 since we encode the size */
        int obound = 4;
        //If the array is empty (to simple types)
        if(this.values.isEmpty()) {
        	T element = efactory.createElement(0);
        	obound = Math.max(obound, element.getOctetBoundary());
        }
        for (Iterator<T> it = values.iterator(); it.hasNext();) {
            T elem  = it.next();
            obound = Math.max(obound, elem.getOctetBoundary());
        }
        return obound;
    }

    /**
	 * Encode the HLAVariableArray
	 * Put in the byteWrapper : 
	 *  - the size of the array
	 *  - each value (expect last one) with padding
	 *  - last value without padding
	 * @param Object byteWrapper, initialized with the correct lenght
	 */
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
       byteWrapper.align(this.getOctetBoundary());
       byteWrapper.putInt(values.size());
       byteWrapper.align(this.getOctetBoundary());
       //same padding for all 
       int paddingSize = calculPaddingAfterEachElement(values.get(0).getEncodedLength(), this.getOctetBoundary());
       byte[] paddingBytes = new byte[paddingSize];
       if(paddingSize > 0) {
    	   for(int j =0; j<paddingSize; j++) paddingBytes[j] = 0;
       }
       //Encode each element EXCEPT last one, with padding
       for (int i = 0; i< this.values.size() - 1; i++) {
           T elem  = values.get(i);
           elem.encode(byteWrapper);
           
           if(paddingSize > 0) byteWrapper.put(paddingBytes);    
       }
     //Last element (without padding)
       this.values.get(this.values.size()-1).encode(byteWrapper);
    }
    

    /**
	 * Calcul the length necessary to encode this HLAVariableArray
	 * The lenght is the sum of :
	 * 		- the size lenght aligned (8)
	 * 		- the size of each value (expect last one) with padding
	 * 		- the size of the last value without padding
	 * @return Lenght necessary to encode the variant
	 */
    public int getEncodedLength() {
        int elength = 8;
        //int padding = calculPaddingAfterEachElement(this.values.get(0).getEncodedLength(), this.getOctetBoundary());
        //size of with element (except last one) with padding
        for (int i = 0; i< this.values.size() - 1; i++) {
            T elem  = values.get(i);
            elength += elem.getEncodedLength();
            elength += calculPaddingAfterEachElement(this.values.get(i).getEncodedLength(), this.getOctetBoundary());
        }
        //Last element (without padding)
        elength += this.values.get(this.values.size()-1).getEncodedLength();
        return elength;
    }
    
	/**
	 * Calcul the size of the padding necesary after a specific value of the array
	 * @param sizeElement : encoded lenght of a specific value
	 * @param octetBoundary : octet boundary of the HLAVariableArray
	 * @return size of the padding corresponding to the value
	 */
    public int calculPaddingAfterEachElement(int sizeElement, int octetBoundary) {
    	int padding ;
    	int r = sizeElement % octetBoundary;
    	if(r == 0) padding = 0;
    	else padding = octetBoundary - r;
    	return padding;   	
    }
    
    /**
	 * Decode the HLAVariableArray
	 * 2 cases possible :
	 * 	  - if the objets can be created with a factory and the factory is initialized, 
	 * 	  	we create new element with the factory and add them in the array
	 * 	  - if the factory is not initialized (for exemple for complexe types with differents structures),
	 * 		then the structure must have been defined before the decoding
	 * If we are not in one of the cases, throw a DecoderException
	 * @param Object byteWrapper, initialized with the correct lenght, with position reset
	 */
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
    	byteWrapper.align(this.getOctetBoundary());
        int nbElem = byteWrapper.getInt();
        byteWrapper.align(getOctetBoundary());
        
        if(this.efactory != null) {
        	//Simple type : Create elements and add them to the array
        	values.clear();
            for (int i = 0; i<nbElem - 1;++i) {
                T elem = efactory.createElement(i);
                elem.decode(byteWrapper);
                values.add(elem);
                //Pass the padding
                byteWrapper.advance(this.calculPaddingAfterEachElement(elem.getEncodedLength(), this.getOctetBoundary()));
            }
            T elem = efactory.createElement(nbElem - 1);
            elem.decode(byteWrapper);
            values.add(elem);
        } else if(!this.values.isEmpty()){
        	//Complexe type : decode this elements with the pre-defined structure
        	//Encode each element EXCEPT last one, with padding
            for (int i = 0; i< nbElem- 1; i++) {
                T elem  = values.get(i);
                elem.encode(byteWrapper);
                int paddingSize = calculPaddingAfterEachElement(values.get(i).getEncodedLength(), this.getOctetBoundary()); 
                if(paddingSize > 0) {
                	byte[] paddingBytes = new byte[paddingSize];
              	    for(int j =0; j<paddingSize; j++) paddingBytes[j] = 0;
                    byteWrapper.put(paddingBytes); 
                 }
            }
            //Last element (without padding)
            this.values.get(nbElem-1).encode(byteWrapper);
        } else {
        	throw new DecoderException("VariableArray must have a factory, or its structure have to be predefined");
        }
        
        
    }

    /**
     * Add an element T in the array
     * @param dataElement to add
     */
    public void addElement(T dataElement) {
        values.add(dataElement);
    }
    
    /**
     * Return the object T at a specific position
     * @param index : position of the object
     * @return object T at the position of the specified index
     */
    public T get(int index) {
        return values.get(index);
    }

    /**
     * Size of the array
     * @return the size of the array
     */
    public int size() {
        return values.size();
    }
    
    /**
     * Create an iterator on the array
     * @return iterator on the array
     */
    public Iterator<T> iterator() {
        return values.iterator();
    }

    /**
     * Change the size of the array
     * @param new size of the array
     */
    public void resize(int newSize) {
        values.ensureCapacity(newSize);
    }

}
