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

import certi.rti.impl.*;
import hla.rti.*;
import hla.rti1516e.AttributeHandle;
import hla.rti1516e.impl.CertiObjectHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
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
    protected final static Logger LOGGER = Logger.getLogger(MessageBuffer.class.getName());
    private Vector<Byte> buffer = new Vector<Byte>();
    private Iterator<Byte> iter;	//the Iterator useful for the read operations
    private static byte BIG_ENDIAN = 1;
    private byte endianness = BIG_ENDIAN;
    private InputStream inputStream;
    private OutputStream outputStream;

    

    public MessageBuffer(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
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
     * Receives the message
     * The buffer is reseted after returning the message.
     * @throws IOException
     * @throws CertiException
     */
    synchronized public void receiveData() throws IOException, CertiException {
        this.reset();

        //Read endianess
        endianness = (byte) inputStream.read();

        //Read size
        int size = 0;
        //Same as readInt, but on the InputStream instead of the iterator
        if (endianness == BIG_ENDIAN) {
            for (int i = 3; i >= 0; i--) {
                size = size | (inputStream.read() & 0x000000FF) << i * BYTE_LENGTH;
            }
        } else {
            for (int i = 0; i <= 3; i++) {
                size = size | (inputStream.read() & 0x000000FF) << i * BYTE_LENGTH;
            }
        }

        //Read the message
        for (int i = 0; i <= size - 6; i++) {
            buffer.add((byte) inputStream.read());
        }

        iter = buffer.iterator();
    }

    /**
     * Send the message throught the socket
     * @throws IOException
     */
    synchronized public void send() throws IOException {
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
        outputStream.write(byteArray);
        this.reset();
    }

    /**
     *
     * @param attributes
     */
    public void write(hla.rti.AttributeHandleSet attributes) {
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
                attributeHandleSet.add(this.readInt());
            } catch (AttributeNotDefined ex) {
                LOGGER.severe("Error has occured while reading AttributeHandleSet from buffer.");
            }
        }

        return attributeHandleSet;
    }


    /**
     *
     * @param attributes
     */
    public void write(hla.rti1516e.AttributeHandleSet attributes) {
        this.write(attributes.size());
        for (AttributeHandle attr: attributes) {
            this.write(attr.hashCode());
        }
    }

    /**
     *
     * @return
     */
    public hla.rti1516e.AttributeHandleSet readAttributeHandleSet1516E() {
        int size = this.readInt();
        hla.rti1516e.impl.CertiAttributeHandleSet attributeHandleSet = new hla.rti1516e.impl.CertiAttributeHandleSet();
        for (int i = 0; i < size; i++) {
            attributeHandleSet.add(new CertiObjectHandle(this.readInt()));
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
        int n = this.readInt();
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
