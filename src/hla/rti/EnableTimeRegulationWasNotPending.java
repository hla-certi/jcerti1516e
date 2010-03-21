
package hla.rti;

/**
 * Public exception class EnableTimeRegulationWasNotPending
*/

public final class EnableTimeRegulationWasNotPending extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public EnableTimeRegulationWasNotPending(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public EnableTimeRegulationWasNotPending(String reason, int serial) {
	super(reason, serial);
  }  
}