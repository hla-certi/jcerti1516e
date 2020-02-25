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

public class GetDimensionHandle1516E extends CertiMessage1516E {
	private String dimensionName;
	private int space;
	private int dimension = 0;

	public GetDimensionHandle1516E() {
		super(CertiMessageType.GET_DIMENSION_HANDLE);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(dimensionName);
		messageBuffer.write(space);
		messageBuffer.write(dimension);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		dimensionName = messageBuffer.readString();
		space = messageBuffer.readInt();
		dimension = messageBuffer.readInt();
	}

	@Override
	public String toString() {
		return (super.toString() + ", dimensionName: " + dimensionName + ", space: " + space + ", dimension: "
				+ dimension);
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public int getSpace() {
		return space;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimensionName(String newDimensionName) {
		this.dimensionName = newDimensionName;
	}

	public void setSpace(int newSpace) {
		this.space = newSpace;
	}

	public void setDimension(int newDimension) {
		this.dimension = newDimension;
	}

}
