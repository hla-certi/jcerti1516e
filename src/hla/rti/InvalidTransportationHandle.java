
package hla.rti;

/**
 * Public exception class InvalidTransportationHandle
*/

public final class InvalidTransportationHandle extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InvalidTransportationHandle(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InvalidTransportationHandle(String reason, int serial) {
	super(reason, serial);
  }  
}