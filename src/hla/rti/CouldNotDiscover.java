
package hla.rti;

/**
 * Public exception class CouldNotDiscover
*/

public final class CouldNotDiscover extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public CouldNotDiscover(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public CouldNotDiscover(String reason, int serial) {
	super(reason, serial);
  }  
}