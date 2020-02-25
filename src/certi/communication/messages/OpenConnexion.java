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

public class OpenConnexion extends CertiMessage {
	private int versionMajor;
	private int versionMinor;

	public OpenConnexion() {
		super(CertiMessageType.OPEN_CONNEXION);
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(versionMajor);
		messageBuffer.write(versionMinor);
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header

		versionMajor = messageBuffer.readInt();
		versionMinor = messageBuffer.readInt();
	}

	@Override
	public String toString() {
		return (super.toString() + ", versionMajor: " + versionMajor + ", versionMinor: " + versionMinor);
	}

	public int getVersionMajor() {
		return versionMajor;
	}

	public int getVersionMinor() {
		return versionMinor;
	}

	public void setVersionMajor(int newVersionMajor) {
		this.versionMajor = newVersionMajor;
	}

	public void setVersionMinor(int newVersionMinor) {
		this.versionMinor = newVersionMinor;
	}

}
