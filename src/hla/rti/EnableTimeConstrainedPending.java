
package hla.rti;

/**
 * Public exception class EnableTimeConstrainedPending
*/

public final class EnableTimeConstrainedPending extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public EnableTimeConstrainedPending(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public EnableTimeConstrainedPending(String reason, int serial) {
	super(reason, serial);
  }  
}