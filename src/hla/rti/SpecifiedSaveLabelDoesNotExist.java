
package hla.rti;

/**
 * Public exception class SpecifiedSaveLabelDoesNotExist
*/

public final class SpecifiedSaveLabelDoesNotExist extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public SpecifiedSaveLabelDoesNotExist(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public SpecifiedSaveLabelDoesNotExist(String reason, int serial) {
	super(reason, serial);
  }  
}