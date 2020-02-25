// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi.communication.messages1516E;

import java.util.ArrayList;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;

public class JoinFederationExecution1516E_V2 extends CertiMessage1516E {
	private int federate;
	private String federateType;
	private String federationName;
	private String federateName;
	private ArrayList<String> additionalFomModules;
	private byte rtiVersion = 3;

	public JoinFederationExecution1516E_V2() {
		super(CertiMessageType.JOIN_FEDERATION_EXECUTION_V4);
		additionalFomModules = new ArrayList<>();
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(this.federate);
		if (federateName != null) {
			messageBuffer.write(true);
			messageBuffer.write(this.federateName);
		} else {
			messageBuffer.write(false);
		}
		messageBuffer.write(this.federateType);
		messageBuffer.write(this.rtiVersion);
		messageBuffer.write(this.federationName);
		messageBuffer.write(this.additionalFomModules.size());
		for (String s : this.additionalFomModules) {
			messageBuffer.write(s);
		}
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		federate = messageBuffer.readInt();
		boolean hasFederateName = messageBuffer.readBoolean();
		if (hasFederateName)
			this.federateName = messageBuffer.readString();
		else
			this.federateName = null;

		this.federateType = messageBuffer.readString();
		this.rtiVersion = messageBuffer.readByte();
		this.federationName = messageBuffer.readString();
		int additionalFomModulesSize = messageBuffer.readInt();
		for (int i = 0; i < additionalFomModulesSize; i++) {
			this.additionalFomModules.add(messageBuffer.readString());
		}
	}

	@Override
	public String toString() {
		String result = (super.toString() + ", federate: " + federate + ", federationName: " + federationName
				+ ", federateName: " + federateName);
		for (String s : this.additionalFomModules)
			result += s + ", ";
		return result;
	}

	public int getFederate() {
		return federate;
	}

	public String getFederationName() {
		return federationName;
	}

	public String getFederateType() {
		return this.federateType;
	}

	public String getFederateName() {
		return federateName;
	}

	public String[] getAdditionalFomModules() {
		return (String[]) this.additionalFomModules.toArray();
	}

	public void setFederate(int newFederate) {
		this.federate = newFederate;
	}

	public void setFederationName(String newFederationName) {
		this.federationName = newFederationName;
	}

	public void setFederateName(String newFederateName) {
		this.federateName = newFederateName;
	}

	public void setFederateType(String federateType) {
		this.federateType = federateType;
	}

	public void setAdditionalFomModules(String[] additionalFomModules) {
		if (additionalFomModules != null)
			for (String s : additionalFomModules)
				this.additionalFomModules.add(s);
	}

}
