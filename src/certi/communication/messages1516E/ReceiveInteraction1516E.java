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
package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiExceptionType;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import hla.rti.EventRetractionHandle;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.impl.CertiObjectHandle;
import hla.rti1516e.impl.CertiParameterHandleValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ReceiveInteraction1516E extends CertiMessage1516E {

    private int interactionClass;
    //private ReceivedInteraction receivedInteraction;
    private ParameterHandleValueMap parameterHandleValueMap;
    private int region;
    private EventRetractionHandle EventRetractionHandle;

    public ReceiveInteraction1516E() {
        super(CertiMessageType.RECEIVE_INTERACTION);
    }

    @Override
    public void writeMessage(MessageBuffer messageBuffer) {
        super.writeMessage(messageBuffer); //Header

        messageBuffer.write(interactionClass);

        Set<ParameterHandle> keys = parameterHandleValueMap.keySet();

        messageBuffer.write(parameterHandleValueMap.size());
        for(ParameterHandle key : keys){
            messageBuffer.write(key.hashCode());
        }
        messageBuffer.write(parameterHandleValueMap.size());
        for(ParameterHandle key : keys){
            messageBuffer.write(parameterHandleValueMap.get(key));
        }
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
        parameterHandleValueMap = new CertiParameterHandleValueMap();
        int sizeKeys = messageBuffer.readInt();
        List<ParameterHandle> keys = new ArrayList<>();
        for(int i = 0; i < sizeKeys; i++){
            keys.add(new CertiObjectHandle(messageBuffer.readInt()));
        }
        int sizeValues = messageBuffer.readInt();
        if(sizeKeys != sizeValues){
            throw new CertiException(CertiExceptionType.ARRAY_INDEX_OUT_OF_BOUNDS, "Error while reading parameterHandleValueMap : values size doesn't match with hanldes size");
        }
        region = messageBuffer.readInt();
        boolean hasEventRetractionHandle = messageBuffer.readBoolean();
        if (hasEventRetractionHandle) {
            EventRetractionHandle = messageBuffer.readEventRetractionHandle();
        }
    }

    @Override
    public String toString() {
        return (super.toString() + ", interactionClass: " + interactionClass + ", ParameterHandleValuePairSet: " + parameterHandleValueMap.toString() + ", region: " + region + ", EventRetractionHandle: " + EventRetractionHandle);
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

    public ParameterHandleValueMap getParameterHandleValueMap() {
        return parameterHandleValueMap;
    }

    public void setParameterHandleValueMap(ParameterHandleValueMap parameterHandleValueMap) {
        this.parameterHandleValueMap = parameterHandleValueMap;
    }


//    public ReceivedInteraction getReceivedInteraction() {
//        return receivedInteraction;
//    }
//
//    public void setReceivedInteraction(ReceivedInteraction receivedInteraction) {
//        this.receivedInteraction = receivedInteraction;
//    }
}

