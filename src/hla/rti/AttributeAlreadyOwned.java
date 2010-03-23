
package hla.rti;

/**
 * Public exception class AttributeAlreadyOwned
*/

public final class AttributeAlreadyOwned extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeAlreadyOwned(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeAlreadyOwned(String reason, int serial) {
	super(reason, serial);
  }  
}