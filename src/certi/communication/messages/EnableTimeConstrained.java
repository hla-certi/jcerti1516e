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
import hla.rti.LogicalTimeInterval;

public class EnableTimeConstrained extends CertiMessage {
   private boolean booleanValue;
   private LogicalTimeInterval lookahead;

   public EnableTimeConstrained() {
      super(CertiMessageType.ENABLE_TIME_CONSTRAINED);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(booleanValue);
      messageBuffer.write(lookahead);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      booleanValue = messageBuffer.readBoolean();
      lookahead = messageBuffer.readLogicalTimeInterval();
   }

   @Override
   public String toString() {
      return (super.toString() + ", booleanValue: " + booleanValue + ", lookahead: " + lookahead);
   }

   public boolean getBooleanValue() {
      return booleanValue;
   }

   public LogicalTimeInterval getLookahead() {
      return lookahead;
   }

   public void setBooleanValue(boolean newBooleanValue) {
      this.booleanValue = newBooleanValue;
   }

   public void setLookahead(LogicalTimeInterval newLookahead) {
      this.lookahead = newLookahead;
   }

}

