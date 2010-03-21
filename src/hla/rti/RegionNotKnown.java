
package hla.rti;

/**
 * Public exception class RegionNotKnown
*/

public final class RegionNotKnown extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public RegionNotKnown(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public RegionNotKnown(String reason, int serial) {
	super(reason, serial);
  }  
}