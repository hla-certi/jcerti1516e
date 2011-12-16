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
 * <code>CertiMessage</code> represents one of the messages which are exchanged between the federate and the RTIA.
 * It contains the information that are used in all messages (the <code>CertiMessageType</code> of the message, its <code>CertiExceptionType</code> and its <code>FederationTime</code>). It is also responsible for handling of writing the message header to the <code>MessageBuffer</code>.
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 * @author <a href = "mailto:yannick.bisiaux@supaero.fr">Yannick Bisiaux</a>
 * @author <a href = "mailto:ronan.bossard@supaero.fr">Ronan Bossard</a>
 * @author <a href = "mailto:samuel.reese@supaero.fr">Samuel Reese</a>
 * @version 3.3.3
 */
public class CertiMessage {

    private CertiMessageType type;
    private CertiExceptionType exception;
    private CertiLogicalTime federationTime;
    private EventRetractionHandle eventRetraction;
    private byte[] tag;
    private String label;

    /**
     * Creates a new instance of <code>CertiMessage</code>. The default initialization sets message type to NOT_USED, exception type to NO_EXCEPTION and federation time to 0.
     */
    public CertiMessage() {
        this.type = CertiMessageType.NOT_USED;
        this.exception = CertiExceptionType.NO_EXCEPTION;
    }

    /**
     * Creates a new instance of <code>CertiMessage</code> with specified message type.
     *
     * @param type the <code>CertiMessageType</code> to set
     */
    public CertiMessage(CertiMessageType type) {
        this.type = type;
        this.exception = CertiExceptionType.NO_EXCEPTION;
    }

    /**
     * Writes in the <code>MessageBuffer</code> the state of the attributes of the <code>CertiMessage</code>.
     * The order will be the <code>CertiMessageType</code>, the <code>CertiExceptionType</code> and the federation time of the <code>CertiMessage</code>.
     *
     * @param messageBuffer the <code>MessageBuffer</code> in which the method will write the state of the <code>CertiMessage</code>
     */
    public void writeMessage(MessageBuffer messageBuffer) {
        
        synchronized (messageBuffer) {

            //write the header
            messageBuffer.reset();
            //HEADER
            messageBuffer.write(this.type.getType());
            messageBuffer.write(this.exception.getException());
            //messageBuffer.write(this.federationTime);

            //BASIC MESSAGE
            messageBuffer.write(federationTime != null);
            if (federationTime != null) {
                messageBuffer.write(federationTime.getTime());
            }

            messageBuffer.write(label != null);
            if (label != null) {
                messageBuffer.write(label);
            }

            messageBuffer.write(tag != null);
            if (tag != null) {
                messageBuffer.write(tag);
            }
        }  // end of synchronized(messageBuffer)
    }

    /**
     * Gives the correct values to the message attributes by reading them from specified <code>MessageBuffer</code>.
     *
     * @param messageBuffer the <code>MessageBuffer</code> from which the method will read the state of the <code>CertiMessage</code>
     * @throws CertiException
     */
    public void readMessage(MessageBuffer messageBuffer) throws CertiException {
        
        synchronized (messageBuffer) {

            this.exception = CertiExceptionType.reverse.get(messageBuffer.readInt());

            if (this.exception != CertiExceptionType.NO_EXCEPTION) {
                // If the message carry an exception, the Body will only contain the exception reason.

                throw new CertiException(this.exception, messageBuffer.readString()); //Reads the reason and throws the exception
            }
            //BASIC MESSAGE

            //this.federationTime = messageBuffer.readLogicalTime();
            boolean timestamped = messageBuffer.readBoolean();
            if (timestamped) {
                federationTime = messageBuffer.readLogicalTime();
            }

            boolean labelled = messageBuffer.readBoolean();
            if (labelled) {
                label = messageBuffer.readString();
            }

            boolean tagged = messageBuffer.readBoolean();
            if (tagged) {
                tag = messageBuffer.readBytes();
            }
        } // end of synchronized(messageBuffer)
    }

    /**
     * Returns the <code>CertiMessageType</code> of this <code>CertiMessage</code>.
     *
     * @return the <code>CertiMessageType</code>
     */
    public CertiMessageType getType() {
        return type;
    }

    /**
     * Sets the <code>CertiMessageType</code> of this <code>CertiMessage</code>.
     *
     * @param type the <code>CertiMessageType</code> to set
     */
    public void setType(CertiMessageType type) {
        this.type = type;
    }

    /**
     * Returns the <code>CertiExceptionType</code> of this <code>CertiMessage</code>.
     *
     * @return the <code>CertiExceptionType</code>
     */
    public CertiExceptionType getException() {
        return exception;
    }

    /**
     * Sets the <code>CertiExceptionType</code> of this <code>CertiMessage</code>.
     * 
     * @param exception the <code>CertiExceptionType</code> to set
     */
    public void setException(CertiExceptionType exception) {
        this.exception = exception;
    }

    /**
     * Returns the <code>FederationTime</code> of this <code>CertiMessage</code>.
     *
     * @return the <code>FederationTime</code>
     */
    public CertiLogicalTime getFederationTime() {
        return federationTime;
    }

    /**
     * Sets the <code>FederationTime</code> of this <code>CertiMessage</code>.
     *
     * @param federationTime the <code>FederationTime</code> to set
     */
    public void setFederationTime(CertiLogicalTime federationTime) {
        this.federationTime = federationTime;
    }

    /**
     * Return the event retraction handle corresponding to this message
     * 
     * @return event retraction handle
     */
    public EventRetractionHandle getEventRetraction() {
        return eventRetraction;
    }

    /**
     * Set the event retraction handle corresponding to this message
     *
     * @param eventRetraction event retraction handle
     */
    public void setEventRetraction(EventRetractionHandle eventRetraction) {
        this.eventRetraction = eventRetraction;
    }

    public byte[] getTag() {
        return tag;
    }

    public void setTag(byte[] tag) {
        this.tag = tag;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**This function is able to give a <code>String</code> which represents the state of the message.
     * It gives its <code>CertiMessageType</code>, its <code>CertiExceptionType</code> and the federation time.
     *
     * @return the <code>String</code> which represents the object.
     */
    @Override
    public String toString() {
        return this.type + ", " + this.exception + ", federation time: " + this.federationTime;
    }
}
