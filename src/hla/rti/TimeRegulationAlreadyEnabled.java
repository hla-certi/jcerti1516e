
package hla.rti;

/**
 * Public exception class TimeRegulationAlreadyEnabled
*/

public final class TimeRegulationAlreadyEnabled extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public TimeRegulationAlreadyEnabled(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public TimeRegulationAlreadyEnabled(String reason, int serial) {
	super(reason, serial);
  }  
}