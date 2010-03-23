
package hla.rti;

/**
 * Public exception class ErrorReadingFED
*/
public final class ErrorReadingFED extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ErrorReadingFED(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public ErrorReadingFED(String reason, int serial) {
	super(reason, serial);
  }  
}