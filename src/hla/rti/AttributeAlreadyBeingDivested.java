
package hla.rti;

/**
 * Public exception class AttributeAlreadyBeingDivested
*/

public final class AttributeAlreadyBeingDivested extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeAlreadyBeingDivested(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeAlreadyBeingDivested(String reason, int serial) {
	super(reason, serial);
  }  
}