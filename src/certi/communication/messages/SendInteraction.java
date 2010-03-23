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

public class SendInteraction extends CertiMessage {

    private long interactionClass;
    private byte[] tag;
    private CertiHandleValuePairCollection suppliedParameters;
    private long region;
    private int resignAction;
    private boolean booleanValue;

    public SendInteraction() {
        super(CertiMessageType.SEND_INTERACTION);
    }

    @Override
    public void writeMessage(MessageBuffer messageBuffer) {
        super.writeMessage(messageBuffer); //Header

        messageBuffer.write(interactionClass);
        messageBuffer.writeBytesWithSize(tag);
        messageBuffer.write(suppliedParameters);
        messageBuffer.write(region);
        messageBuffer.write(resignAction);
        messageBuffer.write(booleanValue);
    }

    @Override
    public void readMessage(MessageBuffer messageBuffer) throws CertiException {
        super.readMessage(messageBuffer); //Header

        interactionClass = messageBuffer.readLong();
        tag = messageBuffer.readBytesWithSize();
        suppliedParameters = messageBuffer.readHandleValuePairCollection();
        region = messageBuffer.readLong();
        resignAction = messageBuffer.readInt();
        booleanValue = messageBuffer.readBoolean();
    }

    @Override
    public String toString() {
        return (super.toString() + ", interactionClass: " + interactionClass + ", tag: " + tag + ", suppliedParameters: " + suppliedParameters + ", region: " + region + ", resignAction: " + resignAction + ", booleanValue: " + booleanValue);
    }

    public long getInteractionClass() {
        return interactionClass;
    }

    public byte[] getTag() {
        return tag;
    }

    public CertiHandleValuePairCollection getSuppliedParameters() {
        return suppliedParameters;
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

    public void setSuppliedParameters(CertiHandleValuePairCollection newSuppliedParameters) {
        this.suppliedParameters = newSuppliedParameters;
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

