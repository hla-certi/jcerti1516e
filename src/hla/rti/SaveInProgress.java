
package hla.rti;

/**
 * Public exception class SaveInProgress
*/
public final class SaveInProgress extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public SaveInProgress(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public SaveInProgress(String reason, int serial) {
	super(reason, serial);
  }  
}