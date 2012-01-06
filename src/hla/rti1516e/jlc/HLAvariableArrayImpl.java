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

public class HLAvariableArrayImpl<T extends DataElement> extends DataElementBase
    implements hla.rti1516e.encoding.HLAvariableArray<T> {

    private ArrayList<T>          values;
    private DataElementFactory<T> efactory;

    public HLAvariableArrayImpl() {
        values  = new ArrayList<T>(20);
        efactory = null;
    }
    
    public HLAvariableArrayImpl(T[] elements) {
        values = new ArrayList<T>(elements.length);
        values.addAll(Arrays.asList(elements));
        efactory = null;
    }
    
    public HLAvariableArrayImpl(DataElementFactory<T> factory) {
        values   = new ArrayList<T>(20);
        efactory = factory;
    }
    
    public int getOctetBoundary() {
        /* at least 4 since we encode the size */
        int obound = 4;
        for (Iterator<T> it = values.iterator(); it.hasNext();) {
            T elem  = it.next();
            obound = Math.max(obound, elem.getOctetBoundary());
        }
        return obound;
    }

    public void encode(ByteWrapper byteWrapper) throws EncoderException {
       byteWrapper.align(getOctetBoundary());
       byteWrapper.putInt(values.size());
       for (Iterator<T> it = values.iterator(); it.hasNext();) {
           T elem  = it.next();
           elem.encode(byteWrapper);
       }
    }

    public int getEncodedLength() {
        int elength = 4;
        for (Iterator<T> it = values.iterator(); it.hasNext();) {
            T elem  = it.next();
            elength += elem.getEncodedLength();
        }
        return elength;
    }

    public void decode(ByteWrapper byteWrapper) throws DecoderException {
        byteWrapper.align(getOctetBoundary());
        int nbElem = byteWrapper.getInt();
        values.ensureCapacity(nbElem);
        /* FIXME we may optimize this in order to avoid reallocation 
         * we should
         *  - verify size
         *  - trimToSize
         *  - clear 
         *  - add 
         */
        values.clear();
        for (int i = 0; i<nbElem;++i) {
            T elem = efactory.createElement(i);
            elem.decode(byteWrapper);
            values.add(elem);
        }
    }

    public void addElement(T dataElement) {
        values.add(dataElement);
    }

    public int size() {
        return values.size();
    }

    public T get(int index) {
        return values.get(index);
    }

    public Iterator<T> iterator() {
        return values.iterator();
    }

    public void resize(int newSize) {
        values.ensureCapacity(newSize);
    }

}
