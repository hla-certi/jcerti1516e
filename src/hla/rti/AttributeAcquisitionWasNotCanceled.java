
package hla.rti;

/**
 * Public exception class AttributeAcquisitionWasNotCanceled
*/

public final class AttributeAcquisitionWasNotCanceled extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeAcquisitionWasNotCanceled(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeAcquisitionWasNotCanceled(String reason, int serial) {

	super(reason, serial);
  }  
}