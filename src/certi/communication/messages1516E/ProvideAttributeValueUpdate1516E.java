package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.AttributeHandleSet;
import hla.rti1516e.impl.CertiAttributeHandleSet;
import hla.rti1516e.impl.CertiObjectHandle;

public class ProvideAttributeValueUpdate1516E extends CertiMessage1516E {

	public ProvideAttributeValueUpdate1516E() {
		super(CertiMessageType.PROVIDE_ATTRIBUTE_VALUE_UPDATE);
	}

	private int object;
	private AttributeHandleSet attributes;

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
		attributes = new CertiAttributeHandleSet();
		for (int i = 0; i < size; i++) {
			int handleCode = messageBuffer.readInt();
			AttributeHandle handle = new CertiObjectHandle(handleCode);
			attributes.add(handle);
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
