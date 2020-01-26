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
package certi.communication.messages;

import certi.communication.*;
import certi.rti.impl.*;
import hla.rti.*;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ObjectInstanceHandleFactory;

public class UpdateAttributeValues extends CertiMessage {

    private int objectClass;
    private int object;
    private CertiHandleValuePairCollection suppliedAttributes;
    private EventRetractionHandle EventRetractionHandle;

    public UpdateAttributeValues() {
        super(CertiMessageType.UPDATE_ATTRIBUTE_VALUES);
    }

    @Override
    public void writeMessage(MessageBuffer messageBuffer) {
        super.writeMessage(messageBuffer); //Header

        messageBuffer.write(objectClass);
        messageBuffer.write(object);
        messageBuffer.write(suppliedAttributes);

        if (EventRetractionHandle == null) {
            messageBuffer.write(false);
        } else {
            messageBuffer.write(true);
            messageBuffer.write(EventRetractionHandle);
        }
    }

    public CertiHandleValuePairCollection getSuppliedAttributes() {
        return suppliedAttributes;
    }

    public void setSuppliedAttributes(CertiHandleValuePairCollection suppliedAttributes) {
        this.suppliedAttributes = suppliedAttributes;
    }

    @Override
    public void readMessage(MessageBuffer messageBuffer) throws CertiException {
        super.readMessage(messageBuffer); //Header

        objectClass = messageBuffer.readInt();
        object = messageBuffer.readInt();
        suppliedAttributes = messageBuffer.readHandleValuePairCollection();

        boolean hasEventRetractionHandle = messageBuffer.readBoolean();
        if (hasEventRetractionHandle) {
            EventRetractionHandle = messageBuffer.readEventRetractionHandle();
        }
    }

    @Override
    public String toString() {
        return (super.toString() + ", objectClass: " + objectClass + ", object: " + object + ", AttributeHandleValuePairSet: " + suppliedAttributes + ", EventRetractionHandle: " + EventRetractionHandle);
    }

    public int getObjectClass() {
        return objectClass;
    }

    public int getObject() {
        return object;
    }

    public void setObjectClass(int newObjectClass) {
        this.objectClass = newObjectClass;
    }

    public void setObject(int newObject) {
        this.object = newObject;
    }
}

