
package hla.rti;

/**
 * Public exception class TimeAdvanceWasNotInProgress
*/

public final class TimeAdvanceWasNotInProgress extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public TimeAdvanceWasNotInProgress(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public TimeAdvanceWasNotInProgress(String reason, int serial) {
	super(reason, serial);
  }  
}