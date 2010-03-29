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
import certi.communication.*;

public class JoinFederationExecution extends CertiMessage {
   private int federate;
   private String federationName;
   private String federateName;

   public JoinFederationExecution() {
      super(CertiMessageType.JOIN_FEDERATION_EXECUTION);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(federate);
      messageBuffer.write(federationName);
      messageBuffer.write(federateName);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      federate = messageBuffer.readInt();
      federationName = messageBuffer.readString();
      federateName = messageBuffer.readString();
   }

   @Override
   public String toString() {
      return (super.toString() + ", federate: " + federate + ", federationName: " + federationName + ", federateName: " + federateName);
   }

   public int getFederate() {
      return federate;
   }

   public String getFederationName() {
      return federationName;
   }

   public String getFederateName() {
      return federateName;
   }

   public void setFederate(int newFederate) {
      this.federate = newFederate;
   }

   public void setFederationName(String newFederationName) {
      this.federationName = newFederationName;
   }

   public void setFederateName(String newFederateName) {
      this.federateName = newFederateName;
   }

}

