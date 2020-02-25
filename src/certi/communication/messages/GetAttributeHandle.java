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

public class GetAttributeHandle extends CertiMessage {
	private int objectClass;
	private String attributeName;
	private int attribute = 0;

	public GetAttributeHandle() {
		super(CertiMessageType.GET_ATTRIBUTE_HANDLE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(objectClass);
		messageBuffer.write(attributeName);
		messageBuffer.write(attribute);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		objectClass = messageBuffer.readInt();
		attributeName = messageBuffer.readString();
		attribute = messageBuffer.readInt();
	}

	@Override
	public String toString() {
		return (super.toString() + ", objectClass: " + objectClass + ", attributeName: " + attributeName
				+ ", attribute: " + attribute);
	}

	public int getObjectClass() {
		return objectClass;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public int getAttribute() {
		return attribute;
	}

	public void setObjectClass(int newObjectClass) {
		this.objectClass = newObjectClass;
	}

	public void setAttributeName(String newAttributeName) {
		this.attributeName = newAttributeName;
	}

	public void setAttribute(int newAttribute) {
		this.attribute = newAttribute;
	}

}
