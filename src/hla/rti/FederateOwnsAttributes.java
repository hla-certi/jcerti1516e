
package hla.rti;

/**
 * Public exception class FederateOwnsAttributes
*/
public final class FederateOwnsAttributes extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederateOwnsAttributes(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederateOwnsAttributes(String reason, int serial) {
	super(reason, serial);
  }  
}