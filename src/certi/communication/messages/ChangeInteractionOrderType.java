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
package certi.communication.messages;

import certi.communication.CertiException;
import certi.communication.CertiMessage;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;

public class ChangeInteractionOrderType extends CertiMessage {
	private int interactionClass;
	private short transport;
	private short order;

	public ChangeInteractionOrderType() {
		super(CertiMessageType.CHANGE_INTERACTION_ORDER_TYPE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(interactionClass);
		messageBuffer.write(transport);
		messageBuffer.write(order);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		interactionClass = messageBuffer.readInt();
		transport = messageBuffer.readShort();
		order = messageBuffer.readShort();
	}

	@Override
	public String toString() {
		return (super.toString() + ", interactionClass: " + interactionClass + ", transport: " + transport + ", order: "
				+ order);
	}

	public int getInteractionClass() {
		return interactionClass;
	}

	public short getTransport() {
		return transport;
	}

	public short getOrder() {
		return order;
	}

	public void setInteractionClass(int newInteractionClass) {
		this.interactionClass = newInteractionClass;
	}

	public void setTransport(short newTransport) {
		this.transport = newTransport;
	}

	public void setOrder(short newOrder) {
		this.order = newOrder;
	}

}
