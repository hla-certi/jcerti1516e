
package hla.rti;

/**
 * Public exception class FederatesCurrentlyJoined
*/
public final class FederatesCurrentlyJoined extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederatesCurrentlyJoined(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederatesCurrentlyJoined(String reason, int serial) {
	super(reason, serial);
  }  
}