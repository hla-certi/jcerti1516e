
package hla.rti;

/**
 * Public exception class InvalidRegionContext
*/

public final class InvalidRegionContext extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InvalidRegionContext(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InvalidRegionContext(String reason, int serial) {
	super(reason, serial);
  }  
}