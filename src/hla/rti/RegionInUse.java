
package hla.rti;

public final class RegionInUse extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public RegionInUse(String reason) {
	super(reason, 0);
  }
      
  /**
   * @param serial    serial number also printed with the exception
   */
  public RegionInUse(String reason, int serial) {
	super(reason, serial);
  }      
}