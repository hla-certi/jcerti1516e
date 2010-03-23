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

import certi.communication.CertiException;
import certi.communication.MessageBuffer;
import certi.communication.CertiMessageType;
import certi.communication.CertiMessage;
import certi.rti.impl.CertiHandleValuePairCollection;
import hla.rti.AttributeHandleSet;
import hla.rti.SuppliedAttributes;
import certi.rti.impl.CertiExtent;
import java.util.List;
import hla.rti.Region;
import hla.rti.FederateHandleSet;
import hla.rti.SuppliedParameters;
import certi.rti.impl.CertiLogicalTime;
import certi.rti.impl.CertiLogicalTimeInterval;
import hla.rti.LogicalTime;
import hla.rti.LogicalTimeInterval;
import hla.rti.ReflectedAttributes;
import hla.rti.ReceivedInteraction;

public class RemoveObjectInstance extends CertiMessage {
   private long objectClass;
   private long object;
   private byte[] tag;
   private String name;
   private String label;
   private int resignAction;
   private boolean booleanValue;

   public RemoveObjectInstance() {
      super(CertiMessageType.REMOVE_OBJECT_INSTANCE);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(objectClass);
      messageBuffer.write(object);
      messageBuffer.writeBytesWithSize(tag);
      messageBuffer.write(name);
      messageBuffer.write(label);
      messageBuffer.write(resignAction);
      messageBuffer.write(booleanValue);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      objectClass = messageBuffer.readLong();
      object = messageBuffer.readLong();
      tag = messageBuffer.readBytesWithSize();
      name = messageBuffer.readString();
      label = messageBuffer.readString();
      resignAction = messageBuffer.readInt();
      booleanValue = messageBuffer.readBoolean();
   }

   @Override
   public String toString() {
      return (super.toString() + ", objectClass: " + objectClass + ", object: " + object + ", tag: " + tag + ", name: " + name + ", label: " + label + ", resignAction: " + resignAction + ", booleanValue: " + booleanValue);
   }

   public long getObjectClass() {
      return objectClass;
   }

   public long getObject() {
      return object;
   }

   public byte[] getTag() {
      return tag;
   }

   public String getName() {
      return name;
   }

   public String getLabel() {
      return label;
   }

   public int getResignAction() {
      return resignAction;
   }

   public boolean getBooleanValue() {
      return booleanValue;
   }

   public void setObjectClass(long newObjectClass) {
      this.objectClass = newObjectClass;
   }

   public void setObject(long newObject) {
      this.object = newObject;
   }

   public void setTag(byte[] newTag) {
      this.tag = newTag;
   }

   public void setName(String newName) {
      this.name = newName;
   }

   public void setLabel(String newLabel) {
      this.label = newLabel;
   }

   public void setResignAction(int newResignAction) {
      this.resignAction = newResignAction;
   }

   public void setBooleanValue(boolean newBooleanValue) {
      this.booleanValue = newBooleanValue;
   }

}

