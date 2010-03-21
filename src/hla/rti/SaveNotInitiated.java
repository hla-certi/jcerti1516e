
package hla.rti;

/**
 * Public exception class SaveNotInitiated
*/

public final class SaveNotInitiated extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public SaveNotInitiated(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public SaveNotInitiated(String reason, int serial) {
	super(reason, serial);
  }  
}