
package hla.rti;

/**
 * Public exception class FederateWasNotAskedToReleaseAttribute
*/

public final class FederateWasNotAskedToReleaseAttribute extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederateWasNotAskedToReleaseAttribute(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederateWasNotAskedToReleaseAttribute(String reason, int serial) {
	super(reason, serial);
  }  
}