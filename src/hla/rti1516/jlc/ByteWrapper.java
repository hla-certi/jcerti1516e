/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516.jlc;

/**
 * Utility class for managing data in byte arrays.
 */
public class ByteWrapper
{
   private int _offset;
   private int _pos;
   private int _limit;
   private byte[] _buffer;

   /**
    * Construct a ByteWrapper backed by a byte array with the specified length.
    *
    * @param length
    */
   public ByteWrapper(int length)
   {
      this(new byte[length]);
   }

   /**
    * Constructs a ByteWrapper backed by the specified byte array. (Changes to
    * the Byte Wrapper will write through to the specified byte array.)
    *
    * @param buffer
    */
   public ByteWrapper(byte[] buffer)
   {
      this(buffer, 0, buffer.length);
   }

   /**
    * Constructs a ByteWrapper backed by the specified byte array. (Changes to
    * the Byte Wrapper will write through to the specified byte array.)
    * The current position will be at the offset. Limit will be at buffer.length.
    *
    * @param buffer
    * @param offset
    */
   public ByteWrapper(byte[] buffer, int offset)
   {
      this(buffer, offset, buffer.length);
   }

   private ByteWrapper(byte[] buffer, int offset, int limit)
   {
      _buffer = buffer;
      _offset = offset;
      _pos = _offset;
      _limit = limit;
   }

   /**
    * Resets current position to the start of the ByteWrapper.
    */
   public void reset()
   {
      _pos = _offset;
   }

   private void verify(int length)
   {
      if (_pos + length > _limit) {
         throw new ArrayIndexOutOfBoundsException(_pos + length);
      }
   }

   /**
    * Reads the next four byte of the ByteWrapper as a hi-endian
    * 32-bit integer. The ByteWrapper's current position is increased by 4.
    *
    * @return decoded value
    */
   public final int getInt()
   {
      verify(4);
      int value = 0;
      value += ((int)_buffer[_pos++] & 0xFF) << 24;
      value += ((int)_buffer[_pos++] & 0xFF) << 16;
      value += ((int)_buffer[_pos++] & 0xFF) << 8;
      value += ((int)_buffer[_pos++] & 0xFF) << 0;

      return value;
   }

   /**
    * Reads the next byte of the ByteWrapper. The ByteWrapper's current
    * position is increased by 1.
    *
    * @return decoded value
    */
   public final int get()
   {
      verify(1);
      return (int)_buffer[_pos++] & 0xFF;
   }

   /**
    * Reads dest.length bytes from the ByteWrapper into dest. The ByteWrapper's
    * current position is increased by dest.length.
    *
    * @param dest
    */
   public final void get(byte[] dest)
   {
      verify(dest.length);
      System.arraycopy(_buffer, _pos, dest, 0, dest.length);
      _pos += dest.length;
   }

   /**
    * Writes value to the ByteWrapper as a hi-endian 32-bit integer. The
    * ByteWrapper's current position is increased by 4.
    *
    * @param value
    */
   public final void putInt(int value)
   {
      verify(4);
      put((value >>> 24) & 0xFF);
      put((value >>> 16) & 0xFF);
      put((value >>> 8) & 0xFF);
      put((value >>> 0) & 0xFF);
   }

   /**
    * Puts a byte in the wrapped byte array and advances the current position
    * by 1.
    *
    * @param b Byte to put.
    */
   public final void put(int b)
   {
      verify(1);
      _buffer[_pos++] = (byte)b;
   }

   /**
    * Puts a byte array in the wrapped byte array and advances the current
    * posisiton by the size of the byte array.
    *
    * @param src Byte array to put.
    */
   public final void put(byte[] src)
   {
      verify(src.length);
      System.arraycopy(src, 0, _buffer, _pos, src.length);
      _pos += src.length;
   }

   /**
    * Returns the backing array.
    *
    * @return
    */
   public final byte[] array()
   {
      return _buffer;
   }

   /**
    * Returns the current position.
    *
    * @return
    */
   public final int getPos()
   {
      return _pos;
   }

   /**
    * Advances the current position by n.
    *
    * @param n
    */
   public final void advance(int n)
   {
      verify(n);
      _pos += n;
   }

   /**
    * Advances the current position until the specified alignment is
    * achieved.
    *
    * @param alignment
    */
   public final void align(int alignment)
   {
      while ((_pos % alignment) != 0) {
         advance(1);
      }
   }

   /**
    * Creates a ByteWrapper backed by the same byte array using the current
    * position as its offset.
    *
    * @return
    */
   public final ByteWrapper slice()
   {
      return new ByteWrapper(_buffer, _pos);
   }

   /**
    * Creates a ByteWrapper backed by the same byte array using the current
    * position as its offset, and the specified length to mark the limit.
    *
    * @return
    */
   public final ByteWrapper slice(int length)
   {
      verify(length);
      return new ByteWrapper(_buffer, _pos, _pos + length);
   }

   public String toString()
   {
      return "ByteWrapper{" +
         "_offset=" + _offset +
         ", _pos=" + _pos +
         ", _limit=" + _limit +
         ", _buffer=" + _buffer +
         "}";
   }
}



