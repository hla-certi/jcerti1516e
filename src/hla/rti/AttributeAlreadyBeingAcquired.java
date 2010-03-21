
package hla.rti;

/**
 * Public exception class AttributeAlreadyBeingAcquired
*/

public final class AttributeAlreadyBeingAcquired extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeAlreadyBeingAcquired(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeAlreadyBeingAcquired(String reason, int serial) {
	super(reason, serial);
  }  
}