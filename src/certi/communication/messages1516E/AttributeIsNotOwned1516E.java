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

public class AttributeIsNotOwned1516E extends CertiMessage1516E {
	private int object;
	private int attribute;
	private int federate;

	public AttributeIsNotOwned1516E() {
		super(CertiMessageType.ATTRIBUTE_IS_NOT_OWNED);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(object);
		messageBuffer.write(attribute);
		messageBuffer.write(federate);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		object = messageBuffer.readInt();
		attribute = messageBuffer.readInt();
		federate = messageBuffer.readInt();
	}

	@Override
	public String toString() {
		return (super.toString() + ", object: " + object + ", attribute: " + attribute + ", federate: " + federate);
	}

	public int getObject() {
		return object;
	}

	public int getAttribute() {
		return attribute;
	}

	public int getFederate() {
		return federate;
	}

	public void setObject(int newObject) {
		this.object = newObject;
	}

	public void setAttribute(int newAttribute) {
		this.attribute = newAttribute;
	}

	public void setFederate(int newFederate) {
		this.federate = newFederate;
	}

}
