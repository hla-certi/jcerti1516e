
package hla.rti;

/**
 * Public exception class FederationExecutionDoesNotExist
*/
public final class FederationExecutionDoesNotExist extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederationExecutionDoesNotExist(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederationExecutionDoesNotExist(String reason, int serial) {
	super(reason, serial);
  }  
}