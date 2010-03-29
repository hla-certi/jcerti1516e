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
import hla.rti.ReceivedInteraction;
import hla.rti.Region;
import hla.rti.SuppliedAttributes;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiReceivedInteraction implements ReceivedInteraction {

    private int orderType;
    private int transportationType;
    private Region region;
    private SuppliedAttributes parameters;

    /**
     *
     * @param orderType
     * @param transportationType
     * @param region
     * @param attributes
     */
    public CertiReceivedInteraction(int orderType, int transportationType, Region region, SuppliedAttributes attributes) {
        this.orderType = orderType;
        this.transportationType = transportationType;
        this.region = region;
        this.parameters = attributes;
    }

    public int getOrderType() {
        return orderType;
    }

    public int getParameterHandle(int index) throws ArrayIndexOutOfBounds {
        return parameters.getHandle(index);
    }

    public Region getRegion() {
        return region;
    }

    public int getTransportType() {
        return transportationType;
    }

    public byte[] getValue(int index) throws ArrayIndexOutOfBounds {
        return parameters.getValue(index);
    }

    public int getValueLength(int index) throws ArrayIndexOutOfBounds {
        return parameters.getValueLength(index);
    }

    public byte[] getValueReference(int index) throws ArrayIndexOutOfBounds {
        return parameters.getValueReference(index);
    }

    public int size() {
        return parameters.size();
    }
}
