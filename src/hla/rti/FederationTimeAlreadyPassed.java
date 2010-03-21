
package hla.rti;

/**
 * Public exception class FederationTimeAlreadyPassed
*/

public final class FederationTimeAlreadyPassed extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederationTimeAlreadyPassed(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederationTimeAlreadyPassed(String reason, int serial) {
	super(reason, serial);
  }  
}