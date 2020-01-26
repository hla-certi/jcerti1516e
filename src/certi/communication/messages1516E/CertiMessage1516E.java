package certi.communication.messages1516E;

import certi.communication.*;
import certi.rti1516e.impl.CertiLogicalTime1516E;

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
public class CertiMessage1516E extends CertiMessage {
    private CertiLogicalTime1516E certiLogicalTime1516E;

    public CertiMessage1516E(CertiMessageType messageType) {

        super(messageType);
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
            //BASIC MESSAGE
            messageBuffer.write(certiLogicalTime1516E != null);
            if (certiLogicalTime1516E != null) {
               messageBuffer.write(certiLogicalTime1516E.getTime());
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
            boolean timestamped = messageBuffer.readBoolean();
            if (timestamped) {
               certiLogicalTime1516E = new CertiLogicalTime1516E(messageBuffer.readDouble());
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
     * Returns the <code>FederationTime</code> of this <code>CertiMessage</code>.
     * @return the <code>FederationTime</code>
     */
    public CertiLogicalTime1516E getFederationTime1516E() {
        return certiLogicalTime1516E;
    }

    public void setFederationTime1516E(CertiLogicalTime1516E fedTime){
        this.certiLogicalTime1516E = fedTime;
    }

}
