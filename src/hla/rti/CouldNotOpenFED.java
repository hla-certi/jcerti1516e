
package hla.rti;

/**
 * Public exception class CouldNotOpenFED
*/
public final class CouldNotOpenFED extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public CouldNotOpenFED(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public CouldNotOpenFED(String reason, int serial) {
	super(reason, serial);
  }  
}