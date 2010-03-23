
package hla.rti;

/**
 * Public exception class FederateNotExecutionMember
*/
public final class FederateNotExecutionMember extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederateNotExecutionMember(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederateNotExecutionMember(String reason, int serial) {
	super(reason, serial);
  }  
}