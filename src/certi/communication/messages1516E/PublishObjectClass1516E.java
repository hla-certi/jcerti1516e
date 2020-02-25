package certi.communication.messages1516E;

import certi.communication.CertiException;
//import certi.communication.CertiMessage;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.impl.CertiObjectHandle;

public class PublishObjectClass1516E extends CertiMessage1516E {
	public PublishObjectClass1516E() {
		super(CertiMessageType.PUBLISH_OBJECT_CLASS);
	}

	private int objectClass;
	private AttributeHandleSet attributes;

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header
		messageBuffer.write(objectClass);
		messageBuffer.write(attributes.size());
		for (AttributeHandle attr : attributes) {
			messageBuffer.write(attr.hashCode());
		}
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header
		objectClass = messageBuffer.readInt();
		int size = messageBuffer.readInt();
		for (int i = 0; i < size; i++) {
			attributes.add(new CertiObjectHandle(messageBuffer.readInt()));
		}
	}

	@Override
	public String toString() {
		return (super.toString() + ", objectClass: " + objectClass + ", attributes: " + attributes);
	}

	public int getObjectClass() {
		return objectClass;
	}

	public AttributeHandleSet getAttributes() {
		return attributes;
	}

	public void setObjectClass(int newObjectClass) {
		this.objectClass = newObjectClass;
	}

	public void setAttributes(AttributeHandleSet newAttributes) {
		this.attributes = newAttributes;
	}
}
