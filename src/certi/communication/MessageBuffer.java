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

import certi.communication.messages.*;
import certi.rti.impl.*;
import hla.rti.*;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class is used to process all the primary data types and to store them into a Vector<Byte>. It can handle sending and receiving of <code>CertiMessage</code> from streams.
 *
 * The JVM is big endian, so all the write methods are big endians.
 * The read methods can handle both big and little endians.
 *
 * @author <a href = "mailto:apancik@gmail.com">Andrej Pancik</a>
 * @author <a href = "mailto:yannick.bisiaux@supaero.fr">Yannick Bisiaux</a>
 * @author <a href = "mailto:ronan.bossard@supaero.fr">Ronan Bossard</a>
 * @author <a href = "mailto:samuel.reese@supaero.fr">Samuel Reese</a>
 * @version 3.3.3
 */
public class MessageBuffer {
    public static final int BYTE_LENGTH = 8;

    private final static Logger LOGGER = Logger.getLogger(MessageBuffer.class.getName());
    private Vector<Byte> buffer = new Vector<Byte>();
    private Iterator<Byte> iter;	//the Iterator useful for the read operations
    private static byte BIG_ENDIAN = 1;
    private byte endianness;

    /**
     *
     */
    public MessageBuffer() {
        endianness = BIG_ENDIAN;
        reset();
    }

    /**
     * This method set the buffer back in the state just after its creation
     * should be used before re-using the buffer to write.
     * It is already automatically done when a message is read from the socket.
     */
    public void reset() {
        buffer.clear();
    }

    /**
     * Write the supplied byte at the end of the buffer.
     *
     * @param b
     */
    public void write(byte b) {
        buffer.add(b);
    }

    /**
     * Read the byte from the buffer and return it
     *
     * @return byte
     * @throws NoSuchElementException
     */
    public byte readByte() throws NoSuchElementException {
        return iter.next();
    }

    /**
     * Write byte array to buffer. First is the length of the array, stored as a integer, then the bytes themselves.
     *
     * @param array array to store
     */
    public void write(byte[] array) {
        this.write(array.length);

        for (byte b : array) {
            buffer.add(b);
        }
    }

    /**
     * Read an array of bytes from the buffer. First read the size (integer) and then the vales.
     *
     * @return read array
     */
    public byte[] readBytes() {
        int size = this.readInt(); //Treat first integer as length

        byte[] byteArray = new byte[size];
        for (int i = 0; i < size; i++) {
            byteArray[i] = this.readByte();
        }

        return byteArray;
    }

    /**
     * Write short to the buffer in a big endian way
     *
     * @param s short to be written
     */
    public void write(short s) {
        buffer.add((byte) (s >>> BYTE_LENGTH));
        buffer.add((byte) s);
    }

    /**
     * Read short from the buffer with correct endianess.
     * 
     * Beware! there are no unsigned integers of any size in java.
     * This methods assume that the short it is trying to read is inside the bounds of a signed short.
     * There are no conversion to an int if it is not the case.
     *
     * @return read short
     * @throws NoSuchElementException
     */
    public short readShort() throws NoSuchElementException {
        int s;

        if (endianness == BIG_ENDIAN) {
            s = ((int) iter.next() & 0x00FF) << BYTE_LENGTH | ((int) iter.next() & 0x00FF);
        } else {
            s = ((int) iter.next() & 0x00FF) | ((int) iter.next() & 0x00FF) << BYTE_LENGTH;
        }
        return (short) s;
    }

    /**
     * Write integer to the buffer in a big endian way
     * 
     * @param b integer to be written
     */
    public void write(int b) {
        for (int i = 3; i >= 0; i--) {
            buffer.add((byte) (b >>> i * BYTE_LENGTH));
        }
    }

    /**
     * Read integer form the buffer with correct endianess.
     * 
     * Beware! there are no unsigned integers of any size in java.
     * This methods assume that the int it is trying to read is inside the bounds of a signed int.
     * There are no conversion to an long if it is not the case.
     *
     * @return an int
     * @throws NoSuchElementException
     */
    public int readInt() throws NoSuchElementException {
        int i = 0;
        if (endianness == BIG_ENDIAN) {
            for (int s = 3; s >= 0; s--) {
                i = i | (((int) iter.next()) & 0x000000FF) << BYTE_LENGTH * s;
            }
        } else {
            for (int s = 0; s <= 3; s++) {
                i = i | ((int) iter.next() & 0x000000FF) << s * BYTE_LENGTH;
            }
        }
        return i;
    }

    /**
     * Write long to the buffer in a big endian way
     *
     * @param l long to be written
     */
    public void write(long l) {
        for (int i = 7; i >= 0; i--) {
            buffer.add((byte) (l >>> i * BYTE_LENGTH));
        }
    }

    /**
     * The endianness is taken care of.
     * Beware! there are no unsigned integers of any size in java.
     * This methods assume that the long it is trying to read is inside the bounds of a signed long.
     * There are no conversion to something bigger if it is not the case.
     * @return
     * @throws NoSuchElementException
     */
    public long readLong() throws NoSuchElementException {
        long l = 0;
        if (endianness == BIG_ENDIAN) {
            for (int i = 7; i >= 0; i--) {
                l = l | ((long) iter.next() & 0x00000000000000FF) << i * BYTE_LENGTH;
            }
        } else {
            for (int i = 0; i <= 7; i++) {
                l = l | ((long) iter.next() & 0x00000000000000FF) << i * BYTE_LENGTH;
            }
        }
        return l;
    }

    /**
     * Write in a big endian, IEEE 754 way
     * @param f
     * @throws NoSuchElementException
     */
    public void write(float f) throws NoSuchElementException {
        //The floatToIntBits return exactly what we are looking for: IEEE 754 coding of the double
        this.write(Float.floatToIntBits(f));
    }

    /**
     * The endianness is taken care of.
     * @return a java (big endian IEEE 754) float
     * @throws NoSuchElementException
     */
    public float readFloat() throws NoSuchElementException {
        return Float.intBitsToFloat(this.readInt());
    }

    /**Write in a big endian, IEEE 754 way
     * @param dbl
     */
    public void write(double dbl) {
        this.write(Double.doubleToLongBits(dbl));
    }

    /**
     * The endianness is taken care of.
     * @return a java (big endian IEEE 754) double
     */
    public double readDouble() {
        return Double.longBitsToDouble(this.readLong());
    }

    /**Write a boolean, actually a byte set as 0 or 1.
     * @param bool
     */
    public void write(boolean bool) {
        buffer.add(bool ? (byte) 1 : (byte) 0);
    }

    /**
     *
     * @return
     * @throws NoSuchElementException
     */
    public boolean readBoolean() throws NoSuchElementException {
        return iter.next() == 1;
    }

    /**
     * Write a String: first an int with the size, then all the char, each one coded on two bytes in UTF-16BE.
     * @param string the String we want to write
     */
    public void write(String string) {
        if (string == null) {
            write("");
        } else {
            this.write(string.getBytes());
        }
    }

    /**
     * The endianness is taken care of.
     * @return
     */
    public String readString() {
        return new String(readBytes());
    }

    /**
     * Instantiate <code>CertiMessage</code> from supplied message type. It does not use reflection for safety.
     *
     * @param messageType message type to instantiate
     * @return instantiated message
     */
    public CertiMessage instantiate(CertiMessageType messageType) {
        LOGGER.info("Trying to instantiate " + messageType);
        switch (messageType) {
            case OPEN_CONNEXION:
                return new OpenConnexion();
            case CLOSE_CONNEXION:
                return new CloseConnexion();
            case CREATE_FEDERATION_EXECUTION:
                return new CreateFederationExecution();
            case DESTROY_FEDERATION_EXECUTION:
                return new DestroyFederationExecution();
            case REGISTER_FEDERATION_SYNCHRONIZATION_POINT:
                return new RegisterFederationSynchronizationPoint();
            case ANNOUNCE_SYNCHRONIZATION_POINT:
                return new AnnounceSynchronizationPoint();
            case REQUEST_FEDERATION_RESTORE_FAILED:
                return new RequestFederationRestoreFailed();
            case SYNCHRONIZATION_POINT_REGISTRATION_FAILED:
                return new SynchronizationPointRegistrationFailed();
            case SYNCHRONIZATION_POINT_REGISTRATION_SUCCEEDED:
                return new SynchronizationPointRegistrationSucceeded();
            case SYNCHRONIZATION_POINT_ACHIEVED:
                return new SynchronizationPointAchieved();
            case FEDERATION_SYNCHRONIZED:
                return new FederationSynchronized();
            case REQUEST_FEDERATION_RESTORE:
                return new RequestFederationRestore();
            case REQUEST_FEDERATION_RESTORE_SUCCEEDED:
                return new RequestFederationRestoreSucceeded();
            case INITIATE_FEDERATE_RESTORE:
                return new InitiateFederateRestore();
            case INITIATE_FEDERATE_SAVE:
                return new InitiateFederateSave();
            case REQUEST_FEDERATION_SAVE:
                return new RequestFederationSave();
            case UNPUBLISH_OBJECT_CLASS:
                return new UnpublishObjectClass();
            case UNSUBSCRIBE_OBJECT_CLASS:
                return new UnsubscribeObjectClass();
            case START_REGISTRATION_FOR_OBJECT_CLASS:
                return new StartRegistrationForObjectClass();
            case STOP_REGISTRATION_FOR_OBJECT_CLASS:
                return new StopRegistrationForObjectClass();
            case IS_ATTRIBUTE_OWNED_BY_FEDERATE:
                return new IsAttributeOwnedByFederate();
            case QUERY_ATTRIBUTE_OWNERSHIP:
                return new QueryAttributeOwnership();
            case ATTRIBUTE_IS_NOT_OWNED:
                return new AttributeIsNotOwned();
            case INFORM_ATTRIBUTE_OWNERSHIP:
                return new InformAttributeOwnership();
            case NEGOTIATED_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
                return new NegotiatedAttributeOwnershipDivestiture();
            case REQUEST_ATTRIBUTE_OWNERSHIP_ASSUMPTION:
                return new RequestAttributeOwnershipAssumption();
            case ATTRIBUTE_OWNERSHIP_ACQUISITION:
                return new AttributeOwnershipAcquisition();
            case REQUEST_ATTRIBUTE_OWNERSHIP_RELEASE:
                return new RequestAttributeOwnershipRelease();
            case ATTRIBUTE_OWNERSHIP_ACQUISITION_IF_AVAILABLE:
                return new AttributeOwnershipAcquisitionIfAvailable();
            case ATTRIBUTE_OWNERSHIP_ACQUISITION_NOTIFICATION:
                return new AttributeOwnershipAcquisitionNotification();
            case ATTRIBUTE_OWNERSHIP_UNAVAILABLE:
                return new AttributeOwnershipUnavailable();
            case UNCONDITIONAL_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
                return new UnconditionalAttributeOwnershipDivestiture();
            case CANCEL_NEGOTIATED_ATTRIBUTE_OWNERSHIP_DIVESTITURE:
                return new CancelNegotiatedAttributeOwnershipDivestiture();
            case ATTRIBUTE_OWNERSHIP_RELEASE_RESPONSE:
                return new AttributeOwnershipReleaseResponse();
            case CANCEL_ATTRIBUTE_OWNERSHIP_ACQUISITION:
                return new CancelAttributeOwnershipAcquisition();
            case CONFIRM_ATTRIBUTE_OWNERSHIP_ACQUISITION_CANCELLATION:
                return new ConfirmAttributeOwnershipAcquisitionCancellation();
            case ATTRIBUTE_OWNERSHIP_DIVESTITURE_NOTIFICATION:
                return new AttributeOwnershipDivestitureNotification();
            case DDM_ASSOCIATE_REGION:
                return new DdmAssociateRegion();
            case DDM_REGISTER_OBJECT:
                return new DdmRegisterObject();
            case DDM_SUBSCRIBE_ATTRIBUTES:
                return new DdmSubscribeAttributes();
            case DDM_UNASSOCIATE_REGION:
                return new DdmUnassociateRegion();
            case DDM_UNSUBSCRIBE_ATTRIBUTES:
                return new DdmUnsubscribeAttributes();
            case DDM_SUBSCRIBE_INTERACTION:
                return new DdmSubscribeInteraction();
            case DDM_UNSUBSCRIBE_INTERACTION:
                return new DdmUnsubscribeInteraction();
            case GET_ATTRIBUTE_SPACE_HANDLE:
                return new GetAttributeSpaceHandle();
            case DDM_CREATE_REGION:
                return new DdmCreateRegion();
            case GET_INTERACTION_SPACE_HANDLE:
                return new GetInteractionSpaceHandle();
            case JOIN_FEDERATION_EXECUTION:
                return new JoinFederationExecution();
            case PUBLISH_OBJECT_CLASS:
                return new PublishObjectClass();
            case SUBSCRIBE_OBJECT_CLASS_ATTRIBUTES:
                return new SubscribeObjectClassAttributes();
            case REGISTER_OBJECT_INSTANCE:
                return new RegisterObjectInstance();
            case UPDATE_ATTRIBUTE_VALUES:
                return new UpdateAttributeValues();
            case REFLECT_ATTRIBUTE_VALUES:
                return new ReflectAttributeValues();
            case DISCOVER_OBJECT_INSTANCE:
                return new DiscoverObjectInstance();
            case DELETE_OBJECT_INSTANCE:
                return new DeleteObjectInstance();
            case REMOVE_OBJECT_INSTANCE:
                return new RemoveObjectInstance();
            case LOCAL_DELETE_OBJECT_INSTANCE:
                return new LocalDeleteObjectInstance();
            case GET_OBJECT_CLASS_HANDLE:
                return new GetObjectClassHandle();
            case GET_OBJECT_CLASS_NAME:
                return new GetObjectClassName();
            case GET_ATTRIBUTE_HANDLE:
                return new GetAttributeHandle();
            case GET_ATTRIBUTE_NAME:
                return new GetAttributeName();
            case GET_OBJECT_CLASS:
                return new GetObjectClass();
            case GET_SPACE_HANDLE:
                return new GetSpaceHandle();
            case GET_SPACE_NAME:
                return new GetSpaceName();
            case GET_DIMENSION_HANDLE:
                return new GetDimensionHandle();
            case GET_DIMENSION_NAME:
                return new GetDimensionName();
            case SEND_INTERACTION:
                return new SendInteraction();
            case RECEIVE_INTERACTION:
                return new ReceiveInteraction();
            case GET_INTERACTION_CLASS_HANDLE:
                return new GetInteractionClassHandle();
            case GET_INTERACTION_CLASS_NAME:
                return new GetInteractionClassName();
            case GET_PARAMETER_HANDLE:
                return new GetParameterHandle();
            case GET_PARAMETER_NAME:
                return new GetParameterName();
            case CHANGE_ATTRIBUTE_TRANSPORTATION_TYPE:
                return new ChangeAttributeTransportationType();
            case CHANGE_ATTRIBUTE_ORDER_TYPE:
                return new ChangeAttributeOrderType();
            case CHANGE_INTERACTION_TRANSPORTATION_TYPE:
                return new ChangeInteractionTransportationType();
            case CHANGE_INTERACTION_ORDER_TYPE:
                return new ChangeInteractionOrderType();
            case GET_TRANSPORTATION_HANDLE:
                return new GetTransportationHandle();
            case GET_TRANSPORTATION_NAME:
                return new GetTransportationName();
            case GET_ORDERING_HANDLE:
                return new GetOrderingHandle();
            case GET_ORDERING_NAME:
                return new GetOrderingName();
            case DDM_MODIFY_REGION:
                return new DdmModifyRegion();
            case DDM_DELETE_REGION:
                return new DdmDeleteRegion();
            case GET_OBJECT_INSTANCE_HANDLE:
                return new GetObjectInstanceHandle();
            case GET_OBJECT_INSTANCE_NAME:
                return new GetObjectInstanceName();
            case RESIGN_FEDERATION_EXECUTION:
                return new ResignFederationExecution();
            case PUBLISH_INTERACTION_CLASS:
                return new PublishInteractionClass();
            case UNPUBLISH_INTERACTION_CLASS:
                return new UnpublishInteractionClass();
            case SUBSCRIBE_INTERACTION_CLASS:
                return new SubscribeInteractionClass();
            case UNSUBSCRIBE_INTERACTION_CLASS:
                return new UnsubscribeInteractionClass();
            case TURN_INTERACTIONS_ON:
                return new TurnInteractionsOn();
            case TURN_INTERACTIONS_OFF:
                return new TurnInteractionsOff();
            case ENABLE_TIME_REGULATION:
                return new EnableTimeRegulation();
            case DISABLE_TIME_REGULATION:
                return new DisableTimeRegulation();
            case ENABLE_TIME_CONSTRAINED:
                return new EnableTimeConstrained();
            case DISABLE_TIME_CONSTRAINED:
                return new DisableTimeConstrained();
            case ENABLE_CLASS_RELEVANCE_ADVISORY_SWITCH:
                return new EnableClassRelevanceAdvisorySwitch();
            case DISABLE_CLASS_RELEVANCE_ADVISORY_SWITCH:
                return new DisableClassRelevanceAdvisorySwitch();
            case ENABLE_INTERACTION_RELEVANCE_ADVISORY_SWITCH:
                return new EnableInteractionRelevanceAdvisorySwitch();
            case DISABLE_INTERACTION_RELEVANCE_ADVISORY_SWITCH:
                return new DisableInteractionRelevanceAdvisorySwitch();
            case ENABLE_ATTRIBUTE_RELEVANCE_ADVISORY_SWITCH:
                return new EnableAttributeRelevanceAdvisorySwitch();
            case DISABLE_ATTRIBUTE_RELEVANCE_ADVISORY_SWITCH:
                return new DisableAttributeRelevanceAdvisorySwitch();
            case ENABLE_ATTRIBUTE_SCOPE_ADVISORY_SWITCH:
                return new EnableAttributeScopeAdvisorySwitch();
            case DISABLE_ATTRIBUTE_SCOPE_ADVISORY_SWITCH:
                return new DisableAttributeScopeAdvisorySwitch();
            case TICK_REQUEST:
                return new TickRequest();
            case REQUEST_CLASS_ATTRIBUTE_VALUE_UPDATE:
                return new RequestClassAttributeValueUpdate();
            case REQUEST_OBJECT_ATTRIBUTE_VALUE_UPDATE:
                return new RequestObjectAttributeValueUpdate();
            case PROVIDE_ATTRIBUTE_VALUE_UPDATE:
                return new ProvideAttributeValueUpdate();
            case MODIFY_LOOKAHEAD:
                return new ModifyLookahead();
            case QUERY_LOOKAHEAD:
                return new QueryLookahead();
            case FEDERATE_SAVE_BEGUN:
                return new FederateSaveBegun();
            case FEDERATE_SAVE_COMPLETE:
                return new FederateSaveComplete();
            case FEDERATE_SAVE_NOT_COMPLETE:
                return new FederateSaveNotComplete();
            case FEDERATE_RESTORE_COMPLETE:
                return new FederateRestoreComplete();
            case FEDERATE_RESTORE_NOT_COMPLETE:
                return new FederateRestoreNotComplete();
            case QUERY_FEDERATE_TIME:
                return new QueryFederateTime();
            case ENABLE_ASYNCHRONOUS_DELIVERY:
                return new EnableAsynchronousDelivery();
            case DISABLE_ASYNCHRONOUS_DELIVERY:
                return new DisableAsynchronousDelivery();
            case QUERY_LBTS:
                return new QueryLbts();
            case TIME_ADVANCE_REQUEST:
                return new TimeAdvanceRequest();
            case TIME_ADVANCE_REQUEST_AVAILABLE:
                return new TimeAdvanceRequestAvailable();
            case NEXT_EVENT_REQUEST:
                return new NextEventRequest();
            case NEXT_EVENT_REQUEST_AVAILABLE:
                return new NextEventRequestAvailable();
            case FLUSH_QUEUE_REQUEST:
                return new FlushQueueRequest();
            case TIME_ADVANCE_GRANT:
                return new TimeAdvanceGrant();
            case TIME_REGULATION_ENABLED:
                return new TimeRegulationEnabled();
            case TIME_CONSTRAINED_ENABLED:
                return new TimeConstrainedEnabled();
            case QUERY_MIN_NEXT_EVENT_TIME:
                return new QueryMinNextEventTime();
            case TICK_REQUEST_STOP:
                return new TickRequestStop();
            case TICK_REQUEST_NEXT:
                return new TickRequestNext();
            default:
                LOGGER.severe("Received an unknown type of message: " + messageType);
                throw new UnsupportedOperationException("Message is not supported yet.");
        }
    }

    /**
     * Receives the message
     * The buffer is reseted after returning the message.
     * @param in
     * @return
     * @throws IOException
     * @throws CertiException
     */
    public CertiMessage receive(InputStream in) throws IOException, CertiException {
        endianness = (byte) in.read();
        int size = 0;
        //Same as readInt, but on the InputStream instead of the iterator
        if (endianness == BIG_ENDIAN) {
            for (int i = 3; i >= 0; i--) {
                size = size | (in.read() & 0x000000FF) << i * BYTE_LENGTH;
            }
        } else {
            for (int i = 0; i <= 3; i++) {
                size = size | (in.read() & 0x000000FF) << i * BYTE_LENGTH;
            }
        }

        for (int i = 0; i <= size - 6; i++) {
            buffer.add((byte) in.read());
        }

        iter = buffer.iterator();

        CertiMessage message = instantiate(CertiMessageType.reverseType.get(this.readInt())); //read first integer from header and instantiate class

        message.readMessage(this);

        return message;
    }

    /**
     * Send the message throught the socket
     * @param out
     * @throws IOException
     */
    public void send(OutputStream out) throws IOException {
        //Construct the header
        Vector<Byte> message = new Vector<Byte>();

        message.add(BIG_ENDIAN);

        int size = (buffer.size() + 5);
        for (int i = 3; i >= 0; i--) {
            message.add((byte) (size >>> i * BYTE_LENGTH));
        }

        //Add constructed body to buffer
        message.addAll(buffer);

        //Copy constructed data to byte array
        byte[] byteArray = new byte[message.size()];
        for (int i = 0; i < message.size(); i++) {
            byteArray[i] = message.get(i);
        }

        //Write it to output stream
        out.write(byteArray);
        this.reset();
    }

    /**
     *
     * @param attributes
     */
    public void write(AttributeHandleSet attributes) {
        this.write(attributes.size());

        HandleIterator iterator = attributes.handles();
        this.write(iterator.first());
        for (int i = 1; i < attributes.size(); i++) { //Starting at 1 since first value was written with iterator.first()
            this.write(iterator.next());
        }
    }

    /**
     *
     * @return
     */
    public AttributeHandleSet readAttributeHandleSet() {
        int size = this.readInt();

        CertiAttributeHandleSet attributeHandleSet = new CertiAttributeHandleSet();

        for (int i = 0; i < size; i++) {
            try {
                attributeHandleSet.add((int) this.readInt());
            } catch (AttributeNotDefined ex) {
                LOGGER.severe("Error has occured while reading AttributeHandleSet from buffer.");
            }
        }

        return attributeHandleSet;
    }

    /**
     *
     * @param extents
     */
    public void write(List<CertiExtent> extents) {

        this.write(extents.size());
        if (extents.size() > 0) {
            int numberOfDimensions = extents.get(0).getNumberOfDimensions();
            this.write(numberOfDimensions);

            for (CertiExtent extent : extents) {
                for (int handle = 1; handle <= numberOfDimensions; handle++) {
                    try {
                        this.write(extent.getRangeLowerBound(handle));
                        this.write(extent.getRangeUpperBound(handle));
                    } catch (ArrayIndexOutOfBounds ex) {
                        LOGGER.severe("Error occured while writing extents to buffer.");
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public List<CertiExtent> readExtents() {
        int numberOfExtents = this.readInt();
        List<CertiExtent> extents = new ArrayList<CertiExtent>(numberOfExtents);

        if (numberOfExtents > 0) {
            int numberOfDimensions = (int) this.readLong();

            for (int i = 0; i < numberOfExtents; i++) {
                CertiExtent extent = new CertiExtent();
                for (int handle = 1; handle <= numberOfDimensions; handle++) {
                    extent.setRangeLowerBound(handle, this.readLong());
                    extent.setRangeUpperBound(handle, this.readLong());
                }
                extents.add(extent);
            }
        }
        return extents;
    }

    /**
     *
     * @param pairCollection
     */
    public void write(CertiHandleValuePairCollection pairCollection) {
        try {
            this.write(pairCollection.size());
            for (int i = 0; i < pairCollection.size(); i++) {
                this.write(pairCollection.getHandle(i));
            }

            this.write(pairCollection.size());
            for (int i = 0; i < pairCollection.size(); i++) {
                this.write(pairCollection.getValue(i));
            }
        } catch (ArrayIndexOutOfBounds ex) {
            LOGGER.severe("index array out of bounds");
        }
    }

    /**
     *
     * @return
     */
    public CertiHandleValuePairCollection readHandleValuePairCollection() {
        int size = this.readInt();

        CertiHandleValuePairCollection pairCollection = new CertiHandleValuePairCollection(size);

        List<Integer> handles = new ArrayList<Integer>(size);

        for (int i = 0; i < size; i++) {
            handles.add(this.readInt()); //TODO oznacit pretypovanie
        }

        size = this.readInt();

        for (int i = 0; i < size; i++) {
            pairCollection.add(handles.get(i), this.readBytes());
        }

        return pairCollection;
    }

    /**
     *
     * @param regions
     */
    public void writeRegions(List<Long> regions) { //TODO Refactor name
        int n = regions.size();
        this.write(n);
        for (int i = 0; i < n; i++) {
            this.write(regions.get(i));
        }
    }

    /**
     *
     * @return
     */
    public List<Long> readRegions() {
        int n = (int) this.readInt();
        List<Long> regions = new ArrayList<Long>(n);

        for (int i = 0; i < n; i++) {
            regions.add(this.readLong());
        }

        return regions;
    }

    /**
     *
     * @param federationTime
     */
    public void write(CertiLogicalTime federationTime) {
        write(federationTime.getTime());
    }

    /**
     *
     * @return
     */
    public CertiLogicalTime readLogicalTime() {
        return new CertiLogicalTime(readDouble());
    }

    /**
     *
     * @param attributes
     */
    public void write(FederateHandleSet attributes) {
        if (attributes == null) {
            this.write(0);
        } else {
            this.write(attributes.size());

            HandleIterator iterator = attributes.handles();

            this.write(iterator.first());

            for (int i = 1; i < attributes.size(); i++) {
                this.write(iterator.next());
            }
        }
    }

    /**
     *
     * @return
     */
    public FederateHandleSet readFederateHandleSet() {
        int size = this.readInt();

        CertiFederateHandleSet attributeHandleSet = new CertiFederateHandleSet();

        for (int i = 0; i < size; i++) {
            attributeHandleSet.add(this.readInt());
        }

        return attributeHandleSet;
    }

    /**
     *
     * @param lookahead
     */
    public void write(LogicalTimeInterval lookahead) {
        this.write(lookahead == null ? 0 : ((CertiLogicalTimeInterval) lookahead).getInterval());
    }

    /**
     *
     * @return
     */
    public LogicalTimeInterval readLogicalTimeInterval() {
        return new CertiLogicalTimeInterval(this.readDouble());
    }

    /**
     *
     * @param reflectedAttributes
     */
    public void write(ReflectedAttributes reflectedAttributes) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *
     * @return
     */
    public ReflectedAttributes readReflectedAttributes() {
        return new CertiReflectedAttributes(0, 0, null, this.readHandleValuePairCollection());
    }

    /**
     *
     * @param receivedInteraction
     */
    public void write(ReceivedInteraction receivedInteraction) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *
     * @return
     */
    public ReceivedInteraction readReceivedInteraction() {
        //TODO Finish
        return new CertiReceivedInteraction(0, 0, null, this.readHandleValuePairCollection());
    }

    public void write(EventRetractionHandle EventRetractionHandle) {
        this.write(((CertiEventRetractionHandle) EventRetractionHandle).getSendingFederate());
        this.write(((CertiEventRetractionHandle) EventRetractionHandle).getSN());
    }

    public EventRetractionHandle readEventRetractionHandle() {
        int sendingFederate = this.readInt();
        long SN = this.readLong();

        return new CertiEventRetractionHandle(sendingFederate, SN);
    }
}
