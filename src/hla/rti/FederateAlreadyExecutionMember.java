
package hla.rti;

/**
 * Public exception class FederateAlreadyExecutionMember
*/
public final class FederateAlreadyExecutionMember extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederateAlreadyExecutionMember(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederateAlreadyExecutionMember(String reason, int serial) {
	super(reason, serial);
  }  
}