// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2010 Andrej Pancik
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
package certi.rti.impl;

import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.ReflectedAttributes;
import hla.rti.Region;
import hla.rti.SuppliedAttributes;

/**
 * This packages the attributes supplied to the federate for
 * reflectAttributeValues. This is conceptually an array
 * with an initial capacity and the ability to grow.
 * You enumerate by stepping index from 0 to size()-1.
 *
 */
public class CertiReflectedAttributes implements ReflectedAttributes {

    private int orderType;
    private int transportationType;
    private Region region;
    private SuppliedAttributes attributes;

    /**
     *
     * @param orderType
     * @param transportationType
     * @param region
     * @param attributes
     */
    public CertiReflectedAttributes(int orderType, int transportationType, Region region, SuppliedAttributes attributes) {
        this.orderType = orderType;
        this.transportationType = transportationType;
        this.region = region;
        this.attributes = attributes;
    }

    /**
     * Return attribute handle at index position.
     * @return int attribute handle
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public int getAttributeHandle(int index) throws ArrayIndexOutOfBounds {
        return attributes.getHandle(index);
    }

    /**
     * Return order handle at index position.
     * @return int order type
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public int getOrderType(int index) throws ArrayIndexOutOfBounds {
        return orderType;
    }

    /**
     * Return Region handle at index position.
     * @return int region handle
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public Region getRegion(int index) throws ArrayIndexOutOfBounds {
        //TODO CHECK!
        return region;
    }

    /**
     * Return transport handle at index position.
     * @return int transport type
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public int getTransportType(int index) throws ArrayIndexOutOfBounds {
        //TODO CHECK!
        return transportationType;
    }

    /**
     * Return copy of value at index position.
     * @return byte[] copy (clone) of value
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public byte[] getValue(int index) throws ArrayIndexOutOfBounds {
        return attributes.getValue(index);
    }

    /**
     * Return length of value at index position.
     * @return int value length
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public int getValueLength(int index) throws ArrayIndexOutOfBounds {
        return attributes.getValueLength(index);
    }

    /**
     * Get the reference of the value at position index (not a clone)
     * @return byte[] the reference
     * @param index int
     * @exception hla.rti.ArrayIndexOutOfBounds
     */
    public byte[] getValueReference(int index) throws ArrayIndexOutOfBounds {
        return attributes.getValueReference(index);
    }

    /**
     * @return int Number of attribute handle-value pairs
     */
    public int size() {
        return attributes.size();
    }
}
