
package hla.rti;

/**
 * Public exception class AsynchronousDeliveryAlreadyEnabled
*/
public final class AsynchronousDeliveryAlreadyEnabled extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AsynchronousDeliveryAlreadyEnabled(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AsynchronousDeliveryAlreadyEnabled(String reason, int serial) {
	super(reason, serial);
  }  
}