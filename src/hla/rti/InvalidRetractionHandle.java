
package hla.rti;

/**
 * Public exception class InvalidRetractionHandle
*/

public final class InvalidRetractionHandle extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InvalidRetractionHandle(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InvalidRetractionHandle(String reason, int serial) {
	super(reason, serial);
  }  
}