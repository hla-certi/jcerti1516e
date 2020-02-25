package certi.rti.impl;

import hla.rti.EventRetractionHandle;

/**
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 */
public class CertiEventRetractionHandle implements EventRetractionHandle {

	public CertiEventRetractionHandle(int sendingFederate, long SN) {
		this.sendingFederate = sendingFederate;
		this.SN = SN;
	}

	private int sendingFederate;
	private long SN;

	public long getSN() {
		return SN;
	}

	public void setSN(long SN) {
		this.SN = SN;
	}

	public int getSendingFederate() {
		return sendingFederate;
	}

	public void setSendingFederate(int sendingFederate) {
		this.sendingFederate = sendingFederate;
	}
}
