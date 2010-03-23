
package hla.rti;

/**
 * Public exception class AttributeNotOwned
*/

public final class AttributeNotOwned extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeNotOwned(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeNotOwned(String reason, int serial) {
	super(reason, serial);
  }  
}