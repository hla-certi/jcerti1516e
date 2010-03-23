
package hla.rti;

/**
 * Public exception class UnableToPerformSave
*/

public final class UnableToPerformSave extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public UnableToPerformSave(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public UnableToPerformSave(String reason, int serial) {
	super(reason, serial);
  }  
}