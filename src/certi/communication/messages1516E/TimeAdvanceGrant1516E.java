package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;

public class TimeAdvanceGrant1516E extends CertiMessage1516E {

	public TimeAdvanceGrant1516E() {
		super(CertiMessageType.TIME_ADVANCE_GRANT);
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
