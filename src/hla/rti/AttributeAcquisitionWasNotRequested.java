
package hla.rti;

/**
 * Public exception class AttributeAcquisitionWasNotRequested
*/

public final class AttributeAcquisitionWasNotRequested extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public AttributeAcquisitionWasNotRequested(String reason) {

	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public AttributeAcquisitionWasNotRequested(String reason, int serial) {
	super(reason, serial);
  }  
}