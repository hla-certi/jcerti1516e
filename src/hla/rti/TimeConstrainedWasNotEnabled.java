
package hla.rti;

/**
 * Public exception class TimeConstrainedWasNotEnabled
*/

public final class TimeConstrainedWasNotEnabled extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public TimeConstrainedWasNotEnabled(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public TimeConstrainedWasNotEnabled(String reason, int serial) {
	super(reason, serial);
  }  
}