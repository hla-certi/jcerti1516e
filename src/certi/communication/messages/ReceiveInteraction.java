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
import hla.rti.*;

public class ReceiveInteraction extends CertiMessage {

    private int interactionClass;
    private ReceivedInteraction receivedInteraction;
    private int region;
    private EventRetractionHandle EventRetractionHandle;

    public ReceiveInteraction() {
        super(CertiMessageType.RECEIVE_INTERACTION);
    }

    @Override
    public void writeMessage(MessageBuffer messageBuffer) {
        super.writeMessage(messageBuffer); //Header

        messageBuffer.write(interactionClass);
        messageBuffer.write(receivedInteraction);
        messageBuffer.write(region);
        if (EventRetractionHandle == null) {
            messageBuffer.write(false);
        } else {
            messageBuffer.write(true);
            messageBuffer.write(EventRetractionHandle);
        }
    }

    @Override
    public void readMessage(MessageBuffer messageBuffer) throws CertiException {
        super.readMessage(messageBuffer); //Header

        interactionClass = messageBuffer.readInt();
        receivedInteraction = messageBuffer.readReceivedInteraction();
        region = messageBuffer.readInt();
        boolean hasEventRetractionHandle = messageBuffer.readBoolean();
        if (hasEventRetractionHandle) {
            EventRetractionHandle = messageBuffer.readEventRetractionHandle();
        }
    }

    @Override
    public String toString() {
        return (super.toString() + ", interactionClass: " + interactionClass + ", ParameterHandleValuePairSet: " + receivedInteraction + ", region: " + region + ", EventRetractionHandle: " + EventRetractionHandle);
    }

    public int getInteractionClass() {
        return interactionClass;
    }

    public int getRegion() {
        return region;
    }

    public void setInteractionClass(int newInteractionClass) {
        this.interactionClass = newInteractionClass;
    }

    public void setRegion(int newRegion) {
        this.region = newRegion;
    }

    public ReceivedInteraction getReceivedInteraction() {
        return receivedInteraction;
    }

    public void setReceivedInteraction(ReceivedInteraction receivedInteraction) {
        this.receivedInteraction = receivedInteraction;
    }
}

