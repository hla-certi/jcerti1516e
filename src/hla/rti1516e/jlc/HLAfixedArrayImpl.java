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
import hla.rti1516e.encoding.DataElementFactory;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.EncoderException;

/**
 * HLAfixedArrayImpl is the implementation of type HLAfixedArray It is an array
 * with a fixe size, wich contains elements with the same type T
 * 
 * @param <T> Type of elements of the array. Can be a simple type or a complexe
 *            type
 */
public class HLAfixedArrayImpl<T extends DataElement> extends DataElementBase
		implements hla.rti1516e.encoding.HLAfixedArray<T> {

	private ArrayList<T> values;

	/**
	 * Contructor of an HLAvariableArray with the size of the array
	 * 
	 * @param size : size of the array to construct
	 */
	public HLAfixedArrayImpl(int size) {
		values = new ArrayList<>(size);
	}

	/**
	 * Contructor of an HLAfixedArray with an array of element of type T
	 * 
	 * @param elements : array witch contains all the elements to add in the
	 *                 HLAfixedArray
	 */
	public HLAfixedArrayImpl(T[] elements) {
		values = new ArrayList<>(elements.length);
		values.addAll(Arrays.asList(elements));
	}

	/**
	 * Constructor of HLAfixedArray with a factory and a size
	 * 
	 * @param factory : factory to create elements of type T
	 * @param size    : number of elements in the array
	 */
	public HLAfixedArrayImpl(DataElementFactory<T> factory, int size) {
		values = new ArrayList<>(size);
	}

	/**
	 * Calcul the octet boundary HLAfixedArray octet boundary is the max of all the
	 * values's octet boundary
	 * 
	 * @return Value of octet boundary
	 */
	@Override
	public int getOctetBoundary() {
		/* at least 4 since we encode the size */
		int obound = 4;
		for (Iterator<T> it = values.iterator(); it.hasNext();) {
			T elem = it.next();
			obound = Math.max(obound, elem.getOctetBoundary());
		}
		return obound;
	}

	/**
	 * Calcul the size of the padding necesary after a specific value of the array
	 * 
	 * @param sizeElement   : encoded lenght of a specific value
	 * @param octetBoundary : octet boundary of the HLAfixedArray
	 * @return size of the padding corresponding to the value
	 */
	public int calculPaddingAfterEachElement(int sizeElement, int octetBoundary) {
		int padding;
		int r = sizeElement % octetBoundary;
		if (r == 0)
			padding = 0;
		else
			padding = octetBoundary - r;
		return padding;
	}

	/**
	 * Encode the HLAfixedArray Put in the byteWrapper : - each value (expect last
	 * one) with padding - last value without padding
	 * 
	 * @param Object byteWrapper, initialized with the correct lenght
	 */
	@Override
	public void encode(ByteWrapper byteWrapper) throws EncoderException {
		byteWrapper.align(getOctetBoundary());

		// Encode each element EXCEPT last one, with padding
		for (int i = 0; i < this.values.size() - 1; i++) {
			T elem = values.get(i);
			elem.encode(byteWrapper);
			int paddingSize = calculPaddingAfterEachElement(values.get(i).getEncodedLength(), this.getOctetBoundary());
			if (paddingSize > 0) {
				byte[] paddingBytes = new byte[paddingSize];
				for (int j = 0; j < paddingSize; j++)
					paddingBytes[j] = 0;
				byteWrapper.put(paddingBytes);
			}
		}
		// Last element (without padding)
		this.values.get(this.values.size() - 1).encode(byteWrapper);
	}

	/**
	 * Calcul the length necessary to encode this HLAfixedArray The lenght is the
	 * sum of : - the size of each value (expect last one) with padding - the size
	 * of the last value without padding
	 * 
	 * @return Lenght necessary to encode the variant
	 */
	@Override
	public int getEncodedLength() {
		int elength = 0;
		int padding = calculPaddingAfterEachElement(this.values.get(0).getEncodedLength(), this.getOctetBoundary());
		// size of with element (except last one) with padding
		for (int i = 0; i < this.values.size() - 1; i++) {
			T elem = values.get(i);
			elength += elem.getEncodedLength();
			elength += padding;
		}
		// Last element (without padding)
		elength += this.values.get(this.values.size() - 1).getEncodedLength();
		return elength;
	}

	/**
	 * Decode the HLAfixedArray The structure must have been defined before the
	 * decoding
	 * 
	 * @param Object byteWrapper, initialized with the correct lenght, with position
	 *               reset
	 */
	@Override
	public void decode(ByteWrapper byteWrapper) throws DecoderException {
		byteWrapper.align(getOctetBoundary());
		int nbElem = values.size();// byteWrapper.getInt();
		if (nbElem == 0)
			throw new DecoderException("HLAfixedArray is empty");
		int padding = this.calculPaddingAfterEachElement(this.values.get(0).getEncodedLength(),
				this.getOctetBoundary());
		for (int i = 0; i < nbElem - 1; ++i) {
			this.values.get(i).decode(byteWrapper);
			// Pass the padding
			byteWrapper.advance(padding);
		}
		// get last element
		this.values.get(nbElem - 1).decode(byteWrapper);
	}

	/**
	 * Add an element of type T to the array
	 * 
	 * @param dataElement : element to add
	 */
	public void addElement(T dataElement) {
		values.add(dataElement);
	}

	/**
	 * Size of the array
	 * 
	 * @return the size of the array
	 */
	@Override
	public int size() {
		return values.size();
	}

	/**
	 * Return the object T at a specific position
	 * 
	 * @param index : position of the object
	 * @return object T at the position of the specified index
	 */
	@Override
	public T get(int index) {
		return values.get(index);
	}

	/**
	 * Create an iterator on the array
	 * 
	 * @return iterator on the array
	 */
	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

	/**
	 * Change the size of the array
	 * 
	 * @param new size of the array
	 */
	public void resize(int newSize) {
		values.ensureCapacity(newSize);
	}

}
