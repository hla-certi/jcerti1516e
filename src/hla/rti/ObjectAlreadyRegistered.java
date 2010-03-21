
package hla.rti;

/**
 * Public exception class ObjectAlreadyRegistered
*/

public final class ObjectAlreadyRegistered extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ObjectAlreadyRegistered(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public ObjectAlreadyRegistered(String reason, int serial) {
	super(reason, serial);
  }  
}