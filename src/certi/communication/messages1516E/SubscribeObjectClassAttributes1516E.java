package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.impl.CertiObjectHandle;

public class SubscribeObjectClassAttributes1516E extends CertiMessage1516E {
   private int objectClass;
   private AttributeHandleSet attributes;
   private boolean active = true;

   public SubscribeObjectClassAttributes1516E() {
      super(CertiMessageType.SUBSCRIBE_OBJECT_CLASS_ATTRIBUTES);
   }

   @Override
   public void writeMessage(MessageBuffer messageBuffer) {
      super.writeMessage(messageBuffer); //Header

      messageBuffer.write(objectClass);
      messageBuffer.write(attributes.size());
      for (AttributeHandle attr: attributes) {
         messageBuffer.write(attr.hashCode());
      }
      messageBuffer.write(active);
   }

   @Override
   public void readMessage(MessageBuffer messageBuffer) throws CertiException {
      super.readMessage(messageBuffer); //Header 

      objectClass = messageBuffer.readInt();
      int size = messageBuffer.readInt();
      for (int i = 0; i < size; i++) {
         attributes.add(new CertiObjectHandle(messageBuffer.readInt()));
      }
      active = messageBuffer.readBoolean();
   }

   @Override
   public String toString() {
      return (super.toString() + ", objectClass: " + objectClass + ", attributes: " + attributes + ", active: " + active);
   }

   public int getObjectClass() {
      return objectClass;
   }

   public AttributeHandleSet getAttributes() {
      return attributes;
   }

   public boolean getActive() {
      return active;
   }

   public void setObjectClass(int newObjectClass) {
      this.objectClass = newObjectClass;
   }

   public void setAttributes(AttributeHandleSet newAttributes) {
      this.attributes = newAttributes;
   }

   public void setActive(boolean newActive) {
      this.active = newActive;
   }
}

