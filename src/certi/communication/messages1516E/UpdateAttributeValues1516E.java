package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import certi.rti.impl.CertiEventRetractionHandle;
import certi.rti.impl.CertiHandleValuePairCollection;

public class UpdateAttributeValues1516E extends CertiMessage1516E {

	private int objectClass;
	private int object;
	private CertiHandleValuePairCollection suppliedAttributes;
	private CertiEventRetractionHandle EventRetractionHandle;

	public UpdateAttributeValues1516E() {

		super(CertiMessageType.UPDATE_ATTRIBUTE_VALUES);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(objectClass);
		messageBuffer.write(object);
		messageBuffer.write(suppliedAttributes);

		messageBuffer.write(suppliedAttributes.size());

		if (EventRetractionHandle == null) {
			messageBuffer.write(false);
		} else {
			messageBuffer.write(true);
			messageBuffer.write(EventRetractionHandle);
		}
	}

	public CertiHandleValuePairCollection getSuppliedAttributes() {
		return suppliedAttributes;
	}

	public void setSuppliedAttributes(CertiHandleValuePairCollection suppliedAttributes) {
		this.suppliedAttributes = suppliedAttributes;
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		objectClass = messageBuffer.readInt();
		object = messageBuffer.readInt();
		suppliedAttributes = messageBuffer.readHandleValuePairCollection();

		boolean hasEventRetractionHandle = messageBuffer.readBoolean();
		if (hasEventRetractionHandle) {
			EventRetractionHandle = (CertiEventRetractionHandle) messageBuffer.readEventRetractionHandle();
		}
	}

	@Override
	public String toString() {
		return (super.toString() + ", objectClass: " + objectClass + ", object: " + object
				+ ", AttributeHandleValuePairSet: " + suppliedAttributes + ", EventRetractionHandle: "
				+ EventRetractionHandle);
	}

	public int getObjectClass() {
		return objectClass;
	}

	public int getObject() {
		return object;
	}

	public void setObjectClass(int newObjectClass) {
		this.objectClass = newObjectClass;
	}

	public void setObject(int newObject) {
		this.object = newObject;
	}

	public CertiEventRetractionHandle getEventRetractionHandle() {
		return EventRetractionHandle;
	}

}
