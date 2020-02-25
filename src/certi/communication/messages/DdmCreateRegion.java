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

import java.util.List;

import certi.communication.CertiException;
import certi.communication.CertiMessage;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;
import certi.rti.impl.CertiExtent;

public class DdmCreateRegion extends CertiMessage {
	private int space;
	private List<CertiExtent> extentSet;
	private int region;

	public DdmCreateRegion() {
		super(CertiMessageType.DDM_CREATE_REGION);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(space);
		messageBuffer.write(extentSet);
		messageBuffer.write(region);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		space = messageBuffer.readInt();
		extentSet = messageBuffer.readExtents();
		region = messageBuffer.readInt();
	}

	@Override
	public String toString() {
		return (super.toString() + ", space: " + space + ", extentSet: " + extentSet + ", region: " + region);
	}

	public int getSpace() {
		return space;
	}

	public List<CertiExtent> getExtentSet() {
		return extentSet;
	}

	public int getRegion() {
		return region;
	}

	public void setSpace(int newSpace) {
		this.space = newSpace;
	}

	public void setExtentSet(List<CertiExtent> newExtentSet) {
		this.extentSet = newExtentSet;
	}

	public void setRegion(int newRegion) {
		this.region = newRegion;
	}

}
