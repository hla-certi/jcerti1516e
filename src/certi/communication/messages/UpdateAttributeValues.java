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

public class UpdateAttributeValues extends CertiMessage {
   private long objectClass;
   private long object;
   private byte[] tag;
   private CertiHandleValuePairCollection suppliedAttributes;
   private int resignAction;
   private boolean isMessageTimestamped;

   public UpdateAttributeValues() {
      super(CertiMessageType.UPDATE_ATTRIBUTE_VALUES);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(objectClass);
      messageBuffer.write(object);
      messageBuffer.writeBytesWithSize(tag);
      messageBuffer.write(suppliedAttributes);
      messageBuffer.write(resignAction);
      messageBuffer.write(isMessageTimestamped);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      objectClass = messageBuffer.readLong();
      object = messageBuffer.readLong();
      tag = messageBuffer.readBytesWithSize();
      suppliedAttributes = messageBuffer.readHandleValuePairCollection();
      resignAction = messageBuffer.readInt();
      isMessageTimestamped = messageBuffer.readBoolean();
   }

   @Override
   public String toString() {
      return (super.toString() + ", objectClass: " + objectClass + ", object: " + object + ", tag: " + tag + ", suppliedAttributes: " + suppliedAttributes + ", resignAction: " + resignAction + ", isMessageTimestamped: " + isMessageTimestamped);
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

   public CertiHandleValuePairCollection getSuppliedAttributes() {
      return suppliedAttributes;
   }

   public int getResignAction() {
      return resignAction;
   }

   public boolean getIsMessageTimestamped() {
      return isMessageTimestamped;
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

   public void setSuppliedAttributes(CertiHandleValuePairCollection newSuppliedAttributes) {
      this.suppliedAttributes = newSuppliedAttributes;
   }

   public void setResignAction(int newResignAction) {
      this.resignAction = newResignAction;
   }

   public void setIsMessageTimestamped(boolean newIsMessageTimestamped) {
      this.isMessageTimestamped = newIsMessageTimestamped;
   }

}

