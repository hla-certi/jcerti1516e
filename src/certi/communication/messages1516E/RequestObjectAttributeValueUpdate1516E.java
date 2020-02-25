package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.impl.CertiObjectHandle;

public class RequestObjectAttributeValueUpdate1516E extends CertiMessage1516E {
	private int object;
	private AttributeHandleSet attributes;

	public RequestObjectAttributeValueUpdate1516E() {
		super(CertiMessageType.REQUEST_OBJECT_ATTRIBUTE_VALUE_UPDATE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(object);
		messageBuffer.write(attributes.size());
		for (AttributeHandle attr : attributes) {
			messageBuffer.write(attr.hashCode());
		}
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		object = messageBuffer.readInt();
		int size = messageBuffer.readInt();
		for (int i = 0; i < size; i++) {
			attributes.add(new CertiObjectHandle(messageBuffer.readInt()));
		}
	}

	@Override
	public String toString() {
		return (super.toString() + ", object: " + object + ", attributes: " + attributes);
	}

	public int getObject() {
		return object;
	}

	public AttributeHandleSet getAttributes() {
		return attributes;
	}

	public void setObject(int newObject) {
		this.object = newObject;
	}

	public void setAttributes(AttributeHandleSet newAttributes) {
		this.attributes = newAttributes;
	}
}
