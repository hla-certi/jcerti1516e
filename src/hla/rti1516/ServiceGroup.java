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
 * @see hla.rti1516.RTIambassador#normalizeServiceGroup
*/

public final class ServiceGroup implements java.io.Serializable {
  private int _value; //each instance's value
  private static final int _lowestValue = 4; //fedn mgt is chapter 4
  private static int _nextToAssign = _lowestValue; //begins at lowest

  /**
  This is the only public constructor. Each user-defined instance of a ServiceGroup
  must be initialized with one of the defined static values.
  * @param otherServiceGroupValue must be a defined static value or another instance.
  */
  public ServiceGroup(ServiceGroup otherServiceGroupValue) {
    _value = otherServiceGroupValue._value;
  }

  /**
  Private to class
  */
  private ServiceGroup() {
    _value = _nextToAssign++;
  }
  
  ServiceGroup(int value)
  throws RTIinternalError
  {
    _value = value;
    if (value < _lowestValue || value >= _nextToAssign) throw new
      RTIinternalError("ServiceGroup: illegal value " + value);
  }

  /**
  * @return String with value "ServiceGroup(n)" where n is value
  */
  public String toString() {
    return "ServiceGroup(" + _value + ")";
  }

  /**
  Allows comparison with other instance of same type.
  * @return true if supplied object is of type ServiceGroup and has same value;
  false otherwise
  */
  public boolean equals(Object otherServiceGroupValue) {
    if (otherServiceGroupValue instanceof ServiceGroup)
      return _value == ((ServiceGroup)otherServiceGroupValue)._value;
    else return false;
  }

  public int hashCode() {
    return _value;
  }

  static public final ServiceGroup FEDERATION_MANAGEMENT
    = new ServiceGroup();
  static public final ServiceGroup DECLARATION_MANAGEMENT
    = new ServiceGroup();
  static public final ServiceGroup OBJECT_MANAGEMENT
    = new ServiceGroup();
  static public final ServiceGroup OWNERSHIP_MANAGEMENT
    = new ServiceGroup();
  static public final ServiceGroup TIME_MANAGEMENT
    = new ServiceGroup();
  static public final ServiceGroup DATA_DISTRIBUTION_MANAGEMENT
    = new ServiceGroup();
  static public final ServiceGroup SUPPORT_SERVICES
    = new ServiceGroup();
}



