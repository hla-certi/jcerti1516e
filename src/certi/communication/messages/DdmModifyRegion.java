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
import java.util.List;
import certi.rti.impl.*;

public class DdmModifyRegion extends CertiMessage {
   private int region;
   private List<CertiExtent> extentSet;

   public DdmModifyRegion() {
      super(CertiMessageType.DDM_MODIFY_REGION);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(region);
      messageBuffer.write(extentSet);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      region = messageBuffer.readInt();
      extentSet = messageBuffer.readExtents();
   }

   @Override
   public String toString() {
      return (super.toString() + ", region: " + region + ", extentSet: " + extentSet);
   }

   public int getRegion() {
      return region;
   }

   public List<CertiExtent> getExtentSet() {
      return extentSet;
   }

   public void setRegion(int newRegion) {
      this.region = newRegion;
   }

   public void setExtentSet(List<CertiExtent> newExtentSet) {
      this.extentSet = newExtentSet;
   }

}

