package hla.rti1516e.impl;

import hla.rti1516e.AttributeHandle;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.InteractionClassHandle;
import hla.rti1516e.MessageRetractionHandle;
import hla.rti1516e.ObjectClassHandle;
import hla.rti1516e.ObjectInstanceHandle;
import hla.rti1516e.ParameterHandle;

public class CertiObjectHandle implements ObjectClassHandle, ObjectInstanceHandle, AttributeHandle, FederateHandle,
		ParameterHandle, InteractionClassHandle, MessageRetractionHandle {

	/**
	 * 
	 */
	public static final int EncodedLength = 8;

	private static final long serialVersionUID = 5519702354700153251L;

	protected int hashCode;

	public CertiObjectHandle(int hashCode) {
		this.hashCode = hashCode;
	}

	@Override
	public boolean equals(Object newHandle) {
		if (newHandle instanceof CertiObjectHandle)
			return ((CertiObjectHandle) newHandle).hashCode == this.hashCode;
		else
			return false;
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	@Override
	public int encodedLength() {
		return EncodedLength;
	}

	@Override
	public void encode(byte[] buffer, int offset) {
		if (buffer.length >= (EncodedLength + offset)) {
			buffer[offset] = (byte) (this.hashCode >> 24);
			buffer[offset + 1] = (byte) (this.hashCode >> 16);
			buffer[offset + 2] = (byte) (this.hashCode >> 8);
			buffer[offset + 3] = (byte) (this.hashCode);
		} else {
			// throw err
		}
	}

}
