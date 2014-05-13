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

public final class SaveStatus implements java.io.Serializable {
  private int _value; //each instance's value
  private static final int _lowestValue = 1;
  private static int _nextToAssign = _lowestValue; //begins at lowest

  /**
  This is the only public constructor. Each user-defined instance of a SaveStatus
  must be initialized with one of the defined static values.
  * @param otherSaveStatusValue must be a defined static value or another instance.
  */
  public SaveStatus(SaveStatus otherSaveStatusValue) {
    _value = otherSaveStatusValue._value;
  }

  /**
  Private to class
  */
  private SaveStatus() {
    _value = _nextToAssign++;
  }
  
  SaveStatus(int value)
  throws RTIinternalError
  {
    _value = value;
    if (value < _lowestValue || value >= _nextToAssign) throw new
      RTIinternalError("SaveStatus: illegal value " + value);
  }

  /**
  * @return String with value "SaveStatus(n)" where n is value
  */
  public String toString() {
    return "SaveStatus(" + _value + ")";
  }

  /**
  Allows comparison with other instance of same type.
  * @return true if supplied object is of type SaveStatus and has same value;
  false otherwise
  */
  public boolean equals(Object otherSaveStatusValue) {
    if (otherSaveStatusValue instanceof SaveStatus)
      return _value == ((SaveStatus)otherSaveStatusValue)._value;
    else return false;
  }

  public int hashCode() {
    return _value;
  }

  static public final SaveStatus NO_SAVE_IN_PROGRESS
    = new SaveStatus();
  static public final SaveStatus FEDERATE_INSTRUCTED_TO_SAVE
    = new SaveStatus();
  static public final SaveStatus FEDERATE_SAVING
    = new SaveStatus();
  static public final SaveStatus FEDERATE_WAITING_FOR_FEDERATION_TO_SAVE
    = new SaveStatus();
}



