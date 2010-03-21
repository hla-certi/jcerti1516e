
package hla.rti;

/**
 * Public exception class FederationExecutionAlreadyExists
*/
public final class FederationExecutionAlreadyExists extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederationExecutionAlreadyExists(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederationExecutionAlreadyExists(String reason, int serial) {
	super(reason, serial);
  }  
}