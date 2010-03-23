
package hla.rti;

/**
 * Public exception class TimeAdvanceAlreadyInProgress
*/

public final class TimeAdvanceAlreadyInProgress extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public TimeAdvanceAlreadyInProgress(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public TimeAdvanceAlreadyInProgress(String reason, int serial) {
	super(reason, serial);
  }  
}