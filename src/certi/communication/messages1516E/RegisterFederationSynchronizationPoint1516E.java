package certi.communication.messages1516E;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.impl.CertiFederateHandleSet;
import hla.rti1516e.impl.CertiObjectHandle;

public class RegisterFederationSynchronizationPoint1516E extends CertiMessage1516E {
	private FederateHandleSet federateSet = new CertiFederateHandleSet();

	public RegisterFederationSynchronizationPoint1516E() {
		super(CertiMessageType.REGISTER_FEDERATION_SYNCHRONIZATION_POINT);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header
		messageBuffer.write(federateSet.size());
		for (FederateHandle f : federateSet) {
			f.hashCode();
			messageBuffer.write(f.hashCode());
		}
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		int size = messageBuffer.readInt();
		CertiFederateHandleSet federateHandleSet = new CertiFederateHandleSet();
		for (int i = 0; i < size; i++) {
			federateHandleSet.add(new CertiObjectHandle(messageBuffer.readInt()));
		}
	}

	@Override
	public String toString() {
		return (super.toString() + ", federateSet: " + federateSet);
	}

	public FederateHandleSet getFederateSet() {
		return federateSet;
	}

	public void setFederateSet(FederateHandleSet newFederateSet) {
		this.federateSet = newFederateSet;
	}

}
