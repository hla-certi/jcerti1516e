
package hla.rti;

/**
 * Public exception class FederateInternalError
*/
public final class FederateInternalError extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederateInternalError(String reason) {
	super(reason, 0);
  }
  
  /**
   * @param serial    serial number also printed with the exception
   */
  public FederateInternalError(String reason, int serial) {
	super(reason, serial);
  }  
}