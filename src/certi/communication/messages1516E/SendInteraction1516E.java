package certi.communication.messages1516E;

import certi.communication.*;

import hla.rti.*;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.impl.CertiObjectHandle;
import hla.rti1516e.impl.CertiParameterHandleValueMap;

import java.util.Set;

public class SendInteraction1516E extends CertiMessage1516E {

 private int interactionClass;
 private ParameterHandleValueMap suppliedParameters;
 private int region;
 private EventRetractionHandle EventRetractionHandle;

 public SendInteraction1516E() {
     super(CertiMessageType.SEND_INTERACTION);
 }

 @Override
 public void writeMessage(MessageBuffer messageBuffer) {
     super.writeMessage(messageBuffer); //Header

     messageBuffer.write(interactionClass);
     messageBuffer.write(suppliedParameters.size());
     Set<ParameterHandle> keys = suppliedParameters.keySet();
     for (ParameterHandle k: keys) {
         messageBuffer.write(k.hashCode());
         messageBuffer.write(suppliedParameters.get(k));
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
     int size = messageBuffer.readInt();
     CertiParameterHandleValueMap parameterHandleValueMap = new CertiParameterHandleValueMap();
     for (int i = 0; i < size; i++) {
         ParameterHandle hashCode = new CertiObjectHandle(messageBuffer.readInt());
         byte[] value = messageBuffer.readBytes();
         parameterHandleValueMap.put(hashCode, value);
     }
     region = messageBuffer.readInt();

     boolean hasEventRetractionHandle = messageBuffer.readBoolean();
     if (hasEventRetractionHandle) {
         EventRetractionHandle = messageBuffer.readEventRetractionHandle();
     }
 }

 @Override
 public String toString() {
     return (super.toString() + ", interactionClass: " + interactionClass + ", ParameterHandleValuePairSet: " + suppliedParameters + ", region: " + region + ", EventRetractionHandle: " + EventRetractionHandle);
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

 public ParameterHandleValueMap getSuppliedParameters() {
     return suppliedParameters;
 }

 public void setSuppliedParameters(ParameterHandleValueMap suppliedParameters) {
     this.suppliedParameters = suppliedParameters;
 }
}

