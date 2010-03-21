
package hla.rti;

/**
 * Public exception class RestoreInProgress
*/
public final class RestoreInProgress extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public RestoreInProgress(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public RestoreInProgress(String reason, int serial) {
	super(reason, serial);
  }  
}