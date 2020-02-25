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
import hla.rti.AttributeHandleSet;

public class ChangeAttributeOrderType extends CertiMessage {
	private short transport;
	private short order;
	private int object;
	private AttributeHandleSet attributes;

	public ChangeAttributeOrderType() {
		super(CertiMessageType.CHANGE_ATTRIBUTE_ORDER_TYPE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(transport);
		messageBuffer.write(order);
		messageBuffer.write(object);
		messageBuffer.write(attributes);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		transport = messageBuffer.readShort();
		order = messageBuffer.readShort();
		object = messageBuffer.readInt();
		attributes = messageBuffer.readAttributeHandleSet();
	}

	@Override
	public String toString() {
		return (super.toString() + ", transport: " + transport + ", order: " + order + ", object: " + object
				+ ", attributes: " + attributes);
	}

	public short getTransport() {
		return transport;
	}

	public short getOrder() {
		return order;
	}

	public int getObject() {
		return object;
	}

	public AttributeHandleSet getAttributes() {
		return attributes;
	}

	public void setTransport(short newTransport) {
		this.transport = newTransport;
	}

	public void setOrder(short newOrder) {
		this.order = newOrder;
	}

	public void setObject(int newObject) {
		this.object = newObject;
	}

	public void setAttributes(AttributeHandleSet newAttributes) {
		this.attributes = newAttributes;
	}

}
