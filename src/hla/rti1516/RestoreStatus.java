/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

/**
 An enumerated type (not a Java Enumeration!)
*/

public final class RestoreStatus implements java.io.Serializable {
  private int _value; //each instance's value
  private static final int _lowestValue = 1;
  private static int _nextToAssign = _lowestValue; //begins at lowest

  /**
  This is the only public constructor. Each user-defined instance of a RestoreStatus
  must be initialized with one of the defined static values.
  * @param otherRestoreStatusValue must be a defined static value or another instance.
  */
  public RestoreStatus(RestoreStatus otherRestoreStatusValue) {
    _value = otherRestoreStatusValue._value;
  }

  /**
  Private to class
  */
  private RestoreStatus() {
    _value = _nextToAssign++;
  }
  
  RestoreStatus(int value)
  throws RTIinternalError
  {
    _value = value;
    if (value < _lowestValue || value >= _nextToAssign) throw new
      RTIinternalError("RestoreStatus: illegal value " + value);
  }

  /**
  * @return String with value "RestoreStatus(n)" where n is value
  */
  public String toString() {
    return "RestoreStatus(" + _value + ")";
  }

  /**
  Allows comparison with other instance of same type.
  * @return true if supplied object is of type RestoreStatus and has same value;
  false otherwise
  */
  public boolean equals(Object otherRestoreStatusValue) {
    if (otherRestoreStatusValue instanceof RestoreStatus)
      return _value == ((RestoreStatus)otherRestoreStatusValue)._value;
    else return false;
  }

  public int hashCode() {
    return _value;
  }

  static public final RestoreStatus NO_RESTORE_IN_PROGRESS
    = new RestoreStatus();
  static public final RestoreStatus FEDERATE_RESTORE_REQUEST_PENDING
    = new RestoreStatus();
  static public final RestoreStatus FEDERATE_WAITING_FOR_RESTORE_TO_BEGIN
    = new RestoreStatus();
  static public final RestoreStatus FEDERATE_PREPARED_TO_RESTORE
    = new RestoreStatus();
  static public final RestoreStatus FEDERATE_RESTORING
    = new RestoreStatus();
  static public final RestoreStatus FEDERATE_WAITING_FOR_FEDERATION_TO_RESTORE
    = new RestoreStatus();
}



