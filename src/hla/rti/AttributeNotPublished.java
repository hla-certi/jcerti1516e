
package hla.rti;

/**
 * Public exception class AttributeNotPublished
*/

public final class AttributeNotPublished extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeNotPublished(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeNotPublished(String reason, int serial) {
	super(reason, serial);
  }  
}