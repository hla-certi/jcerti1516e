
package hla.rti;

/**
 * Public exception class InvalidFederationTime
*/

public final class InvalidFederationTime extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InvalidFederationTime(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InvalidFederationTime(String reason, int serial) {
	super(reason, serial);
  }  
}