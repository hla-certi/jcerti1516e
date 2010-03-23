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

public class ReceiveInteraction extends CertiMessage {
   private long interactionClass;
   private byte[] tag;
   private ReceivedInteraction receivedInteraction;
   private long region;
   private int resignAction;
   private boolean booleanValue;

   public ReceiveInteraction() {
      super(CertiMessageType.RECEIVE_INTERACTION);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(interactionClass);
      messageBuffer.writeBytesWithSize(tag);
      messageBuffer.write(receivedInteraction);
      messageBuffer.write(region);
      messageBuffer.write(resignAction);
      messageBuffer.write(booleanValue);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      interactionClass = messageBuffer.readLong();
      tag = messageBuffer.readBytesWithSize();
      receivedInteraction = messageBuffer.readReceivedInteraction();
      region = messageBuffer.readLong();
      resignAction = messageBuffer.readInt();
      booleanValue = messageBuffer.readBoolean();
   }

   @Override
   public String toString() {
      return (super.toString() + ", interactionClass: " + interactionClass + ", tag: " + tag + ", receivedInteraction: " + receivedInteraction + ", region: " + region + ", resignAction: " + resignAction + ", booleanValue: " + booleanValue);
   }

   public long getInteractionClass() {
      return interactionClass;
   }

   public byte[] getTag() {
      return tag;
   }

   public ReceivedInteraction getReceivedInteraction() {
      return receivedInteraction;
   }

   public long getRegion() {
      return region;
   }

   public int getResignAction() {
      return resignAction;
   }

   public boolean getBooleanValue() {
      return booleanValue;
   }

   public void setInteractionClass(long newInteractionClass) {
      this.interactionClass = newInteractionClass;
   }

   public void setTag(byte[] newTag) {
      this.tag = newTag;
   }

   public void setReceivedInteraction(ReceivedInteraction newReceivedInteraction) {
      this.receivedInteraction = newReceivedInteraction;
   }

   public void setRegion(long newRegion) {
      this.region = newRegion;
   }

   public void setResignAction(int newResignAction) {
      this.resignAction = newResignAction;
   }

   public void setBooleanValue(boolean newBooleanValue) {
      this.booleanValue = newBooleanValue;
   }

}

