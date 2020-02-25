package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;

public class TimeAdvanceRequestAvailable1516E extends CertiMessage1516E {

	public TimeAdvanceRequestAvailable1516E() {
		super(CertiMessageType.TIME_ADVANCE_REQUEST_AVAILABLE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

	}

	@Override
	public String toString() {
		return (super.toString());
	}
}
