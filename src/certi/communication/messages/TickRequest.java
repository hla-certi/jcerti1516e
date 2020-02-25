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

public class TickRequest extends CertiMessage {
	private boolean multiple;
	private double minTickTime;
	private double maxTickTime;

	public TickRequest() {
		super(CertiMessageType.TICK_REQUEST);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(multiple);
		messageBuffer.write(minTickTime);
		messageBuffer.write(maxTickTime);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		multiple = messageBuffer.readBoolean();
		minTickTime = messageBuffer.readDouble();
		maxTickTime = messageBuffer.readDouble();
	}

	@Override
	public String toString() {
		return (super.toString() + ", multiple: " + multiple + ", minTickTime: " + minTickTime + ", maxTickTime: "
				+ maxTickTime);
	}

	public boolean isMultiple() {
		return multiple;
	}

	public double getMinTickTime() {
		return minTickTime;
	}

	public double getMaxTickTime() {
		return maxTickTime;
	}

	public void setMultiple(boolean newMultiple) {
		this.multiple = newMultiple;
	}

	public void setMinTickTime(double newMinTickTime) {
		this.minTickTime = newMinTickTime;
	}

	public void setMaxTickTime(double newMaxTickTime) {
		this.maxTickTime = newMaxTickTime;
	}

}
