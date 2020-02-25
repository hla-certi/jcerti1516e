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

public class GetAttributeSpaceHandle extends CertiMessage {
	private int objectClass;
	private int attribute;
	private int space = 0;

	public GetAttributeSpaceHandle() {
		super(CertiMessageType.GET_ATTRIBUTE_SPACE_HANDLE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(objectClass);
		messageBuffer.write(attribute);
		messageBuffer.write(space);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		objectClass = messageBuffer.readInt();
		attribute = messageBuffer.readInt();
		space = messageBuffer.readInt();
	}

	@Override
	public String toString() {
		return (super.toString() + ", objectClass: " + objectClass + ", attribute: " + attribute + ", space: " + space);
	}

	public int getObjectClass() {
		return objectClass;
	}

	public int getAttribute() {
		return attribute;
	}

	public int getSpace() {
		return space;
	}

	public void setObjectClass(int newObjectClass) {
		this.objectClass = newObjectClass;
	}

	public void setAttribute(int newAttribute) {
		this.attribute = newAttribute;
	}

	public void setSpace(int newSpace) {
		this.space = newSpace;
	}

}
