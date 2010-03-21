
package hla.rti;

/**
 * Public exception class EnableTimeConstrainedWasNotPending
*/

public final class EnableTimeConstrainedWasNotPending extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public EnableTimeConstrainedWasNotPending(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public EnableTimeConstrainedWasNotPending(String reason, int serial) {
	super(reason, serial);
  }  
}