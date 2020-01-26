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

import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DataElement;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * HLAfixedRecordImpl is the implementation of type HLAfixedRecord
 * It is an array with a variable size, wich contains elements witch can be all types (simple or complexe)
 * The element doesn't have to be all the same tyoe
 */
public class HLAfixedRecordImpl extends DataElementBase
    implements hla.rti1516e.encoding.HLAfixedRecord {

    private ArrayList<DataElement> values;

    /**
     * Empty constructor of a HLAfixedRecord 
     */
    public HLAfixedRecordImpl() {
        values  = new ArrayList<DataElement>();
    }
    
    /**
     * Construcor of an HLAfixedRecord with the number of element of the array
     * @param size : number of elments of the array
     */
    public HLAfixedRecordImpl(int size) {
        values  = new ArrayList<DataElement>(size);
    }

    /**
     * Constructor of an HLAfixedRecord with afisrt element 
     * @param e : first element to add to the array
     */
    public HLAfixedRecordImpl(DataElement e) {
        values  = new ArrayList<DataElement>();
        values.add(e);
    }
    
    /**
     * Constructor of an HLAfixedRecord
     * @param elems : array of all the elements to add to the array
     */
    public HLAfixedRecordImpl(DataElement[] elems) {
        values  = new ArrayList<DataElement>();
        values.addAll(Arrays.asList(elems));
    }
    
    /**
    * Add an element of type T to the array
    * @param e : element to add
    */
    public void add(DataElement e) {
        values.add(e);
    }
    
    /**
   	 * Calcul the octet boundary 
   	 * HLAfixedRecord octet boundary is the max of all the values's octet boundary
   	 * @return Value of octet boundary
   	 */
    public int getOctetBoundary() {
        /* at least 4 since we encode the size */
        int obound = 4;
        for (Iterator<DataElement> it = values.iterator(); it.hasNext();) {
            DataElement elem  = it.next();
            obound = Math.max(obound, elem.getOctetBoundary());
        }
        return obound;
    }

    /**
	 * Encode the HLAfixedRecord
	 * Put in the byteWrapper : 
	 *  - each value (expect last one) with padding
	 *  - last value without padding
	 * @param Object byteWrapper, initialized with the correct lenght
	 */
    public void encode(ByteWrapper byteWrapper) throws EncoderException {
       byteWrapper.align(getOctetBoundary());
       //Add padding at the end of each element EXCEPT the last 
       for (int i =0; i< this.values.size() -1; i++){
    	   DataElement e = this.values.get(i);
           e.encode(byteWrapper);
           int paddingSize = calculPaddingAfterEachElements(e.getEncodedLength(), this.getOctetBoundary());
           if(paddingSize > 0) {
        	   byte[] paddingBytes = new byte[paddingSize];
        	   for(int j =0; j<paddingSize; j++) paddingBytes[j] = 0;
        	   byteWrapper.put(paddingBytes);
           }
       }
       //last element without padding
       this.values.get(this.values.size()-1).encode(byteWrapper);    
    }

    /**
	 * Calcul the length necessary to encode this HLAfixedRecord
	 * The lenght is the sum of :
	 * 		- the size of each value (expect last one) + padding
	 * 		- the size of the last value without padding
	 * @return Lenght necessary to encode the variant
	 */
    public int getEncodedLength() {
        int elength = 0;
        for (DataElement e :this.values) {
           elength += e.getEncodedLength() + calculPaddingAfterEachElements(e.getEncodedLength(), this.getOctetBoundary());
        }
        return elength;
    }

    /**
   	 * Decode the HLAfixedRecord
   	 * The structure must have been defined before the decoding
   	 * @param Object byteWrapper, initialized with the correct lenght, with position reset
   	 */
    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        for (int i =0; i< this.values.size() -1; i++){
     	   	DataElement e = this.values.get(i);
        	e.decode(byteWrapper);
        	int paddingSize = calculPaddingAfterEachElements(e.getEncodedLength(), this.getOctetBoundary());
        	byteWrapper.advance(paddingSize);
        }
        this.values.get(this.values.size()-1).decode(byteWrapper);

    }
    
    /**
	 * Calcul the size of the padding necesary after a specific value of the array
	 * @param sizeElement : encoded lenght of a specific value
	 * @param octetBoundary : octet boundary of the HLAfixedRecord
	 * @return size of the padding corresponding to the value
	 */
    public int calculPaddingAfterEachElements(int sizeElement, int octetBoundary) {
    	int padding ;
    	int r = sizeElement % octetBoundary;
    	if(r == 0) padding = 0;
    	else padding = octetBoundary - r;
    	return padding;
    }

    /**
     * Size of the array
     * @return the size of the array
     */
    public int size() {
        return values.size();
    }

    /**
     * Return the object T at a specific position
     * @param index : position of the object
     * @return object T at the position of the specified index
     */
    public DataElement get(int index) {
        return values.get(index);
    }

    /**
     * Create an iterator on the array
     * @return iterator on the array
     */
    public Iterator<DataElement> iterator() {
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
