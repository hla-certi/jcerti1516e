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

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;

public class DdmUnsubscribeInteraction1516E extends CertiMessage1516E {
	private int interactionClass;
	private int region;
	private boolean passive;

	public DdmUnsubscribeInteraction1516E() {
		super(CertiMessageType.DDM_UNSUBSCRIBE_INTERACTION);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(interactionClass);
		messageBuffer.write(region);
		messageBuffer.write(passive);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		interactionClass = messageBuffer.readInt();
		region = messageBuffer.readInt();
		passive = messageBuffer.readBoolean();
	}

	@Override
	public String toString() {
		return (super.toString() + ", interactionClass: " + interactionClass + ", region: " + region + ", passive: "
				+ passive);
	}

	public int getInteractionClass() {
		return interactionClass;
	}

	public int getRegion() {
		return region;
	}

	public boolean getPassive() {
		return passive;
	}

	public void setInteractionClass(int newInteractionClass) {
		this.interactionClass = newInteractionClass;
	}

	public void setRegion(int newRegion) {
		this.region = newRegion;
	}

	public void setPassive(boolean newPassive) {
		this.passive = newPassive;
	}

}
