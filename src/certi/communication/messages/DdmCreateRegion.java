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

public class DdmCreateRegion extends CertiMessage {
   private long space;
   private long number;
   private long region;

   public DdmCreateRegion() {
      super(CertiMessageType.DDM_CREATE_REGION);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(space);
      messageBuffer.write(number);
      messageBuffer.write(region);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      space = messageBuffer.readLong();
      number = messageBuffer.readLong();
      region = messageBuffer.readLong();
   }

   @Override
   public String toString() {
      return (super.toString() + ", space: " + space + ", number: " + number + ", region: " + region);
   }

   public long getSpace() {
      return space;
   }

   public long getNumber() {
      return number;
   }

   public long getRegion() {
      return region;
   }

   public void setSpace(long newSpace) {
      this.space = newSpace;
   }

   public void setNumber(long newNumber) {
      this.number = newNumber;
   }

   public void setRegion(long newRegion) {
      this.region = newRegion;
   }

}

