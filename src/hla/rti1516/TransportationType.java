/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

public class TransportationType implements java.io.Serializable {
  protected int _value; //each instance's value
  private static final int _lowestValue = 1;
  protected static int _nextToAssign = _lowestValue; //begins at lowest

  /**
  This is the only public constructor. Each user-defined instance of a TransportationType
  must be initialized with one of the defined static values.
  * @param otherTransportationTypeValue must be a defined static value or another instance.
  */
  public TransportationType(TransportationType otherTransportationTypeValue) {
    _value = otherTransportationTypeValue._value;
  }

  /**
  Private to class and subclasses
  */
  protected TransportationType() {
    _value = _nextToAssign++;
  }
  
  TransportationType(int value)
  throws RTIinternalError
  {
    _value = value;
    if (value < _lowestValue || value >= _nextToAssign) throw new
      RTIinternalError("TransportationType: illegal value " + value);
  }

  /**
  * @return String with value "TransportationType(n)" where n is value
  */
  public String toString() {
    return "TransportationType(" + _value + ")";
  }

  /**
  Allows comparison with other instance of same type.
  * @return true if supplied object is of type TransportationType and has same value;
  false otherwise
  */
  public boolean equals(Object otherTransportationTypeValue) {
    if (otherTransportationTypeValue instanceof TransportationType)
      return _value == ((TransportationType)otherTransportationTypeValue)._value;
    else return false;
  }

  public int hashCode() {
    return _value;
  }

  public int encodedLength() {
    return  1;
  }

  public void encode(byte[] buffer, int offset) {
    buffer[offset] = (byte)_value;
  }

  public static TransportationType decode(byte[] buffer, int offset)
    throws CouldNotDecode
  {
    int val = buffer[offset];
    TransportationType neo;
    try {
      neo = new TransportationType(val);
    }
    catch (RTIinternalError e) {
      throw new CouldNotDecode(e.getMessage());
    }
    return neo;
  }

  static public final TransportationType HLA_RELIABLE
    = new TransportationType();
  static public final TransportationType HLA_BEST_EFFORT
    = new TransportationType();
}



