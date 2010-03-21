// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2007-2010 Andrej Pancik, Yannick Bisiaux, Ronan Bossard, Samuel Reese
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
package certi.communication;

import certi.rti.impl.CertiLogicalTime;
import hla.rti.EventRetractionHandle;

/**
 * <p><code>CertiMessage</code> represents one of the messages which are exchanged between the federate and the RTIA.
 * It contains the information that are used in all messages (the <code>CertiMessageType</code> of the message, its <code>CertiExceptionType</code> and its <code>FederationTime</code>)</p>
 *
 * @author apancik@gmail.com
 * @author yannick.bisiaux@supaero.fr
 * @author ronan.bossard@supaero.fr
 * @author samuel.reese@supaero.fr
 * @version 1.0
 */
public class CertiMessage {

    private CertiMessageType type;
    private CertiExceptionType exception;
    private CertiLogicalTime federationTime;
    private String reason;
    private EventRetractionHandle eventRetraction;

    /**Creates a new instance of <code>CertiMessage</code>. The default initialization set CertiMessageType to NOT_USED, CertiExceptionType to e_NO_EXCEPTION, FederationTime to 0.
     *
     */
    public CertiMessage() {
        this.type = CertiMessageType.NOT_USED;
        this.exception = CertiExceptionType.NoException;
        this.federationTime = new CertiLogicalTime(0);
    }

    /**Creates a new instance of <code>CertiMessage</code>.
     *
     * @param type the <code>CertiMessageType</code> to set
     */
    public CertiMessage(CertiMessageType type) {
        this.type = type;
        this.exception = CertiExceptionType.NoException;
        this.federationTime = new CertiLogicalTime(0);
    }

    /**Writes in the <code>MessageBuffer</code> the state of the attributes of the <code>CertiMessage</code>.
     * The order will be the <code>CertiMessageType</code>, the <code>CertiExceptionType</code> and the federation time of the <code>CertiMessage</code>.
     *
     * @param messageBuffer the <code>MessageBuffer</code> in which the method will write the state of the <code>CertiMessage</code>
     */
    public void writeMessage(MessageBuffer messageBuffer) {
        //writeHeader
        messageBuffer.reset();
        messageBuffer.write(this.type.getType());
        messageBuffer.write(this.exception.getException());
        messageBuffer.write(this.federationTime);
    }

    /**Gives the correct values to the attributes by reading a <code>MessageBuffer</code> in which are stored those values.
     *
     * @param messageBuffer the <code>MessageBuffer</code> from which the method will read the state of the <code>CertiMessage</code>
     * @throws CertiException
     */
    public void readMessage(MessageBuffer messageBuffer) throws CertiException {
        this.exception = CertiExceptionType.reverse.get(messageBuffer.readInt());

        if (this.exception != CertiExceptionType.NoException) {
            // If the message carry an exception, the Body will only contain the exception reason.

            System.out.println("Received message exception " + this.exception.toString());
            this.reason = messageBuffer.readString();

            throw new CertiException(this.getType(), this.exception, this.reason);
        }

        this.federationTime = messageBuffer.readLogicalTime();
    }

    /**
     *
     * @return
     */
    public String getReason() {
        return reason;
    }

    /**Returns the <code>CertiMessageType</code> of this <code>CertiMessage</code>.
     * @return the <code>CertiMessageType</code>
     */
    public CertiMessageType getType() {
        return type;
    }

    /**Sets the <code>CertiMessageType</code> of this <code>CertiMessage</code>.
     * @param type the <code>CertiMessageType</code> to set
     */
    public void setType(CertiMessageType type) {
        this.type = type;
    }

    /**Returns the <code>CertiExceptionType</code> of this <code>CertiMessage</code>.
     * @return the <code>CertiExceptionType</code>
     */
    public CertiExceptionType getException() {
        return exception;
    }

    /**Sets the <code>CertiExceptionType</code> of this <code>CertiMessage</code>.
     * @param exception_ the <code>CertiExceptionType</code> to set
     */
    public void setException(CertiExceptionType exception_) {
        this.exception = exception_;
    }

    /**Returns the <code>FederationTime</code> of this <code>CertiMessage</code>.
     * @return the <code>FederationTime</code>
     */
    public CertiLogicalTime getFederationTime() {
        return federationTime;
    }

    /**Sets the <code>FederationTime</code> of this <code>CertiMessage</code>.
     * @param federationTime the <code>FederationTime</code> to set
     */
    public void setFederationTime(CertiLogicalTime federationTime) {
        this.federationTime = federationTime;
    }

    /**
     *
     * @return
     */
    public EventRetractionHandle getEventRetraction() {
        return eventRetraction;
    }

    /**
     *
     * @param eventRetraction
     */
    public void setEventRetraction(EventRetractionHandle eventRetraction) {
        this.eventRetraction = eventRetraction;
    }

    /**This function is able to give a <code>String</code> which represents the state of the message.
     * It gives its <code>CertiMessageType</code>, its <code>CertiExceptionType</code> and the federation time.
     * @return the <code>String</code> which represents the object.
     */
    @Override
    public String toString() {
        String str;

        str = this.type + ", " + this.exception + ", federation time: " + this.federationTime;
        return str;
    }
}
