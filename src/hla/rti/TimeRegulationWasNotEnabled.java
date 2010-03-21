
package hla.rti;

/**
 * Public exception class TimeRegulationWasNotEnabled
*/

public final class TimeRegulationWasNotEnabled extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */

  public TimeRegulationWasNotEnabled(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public TimeRegulationWasNotEnabled(String reason, int serial) {
	super(reason, serial);
  }  
}