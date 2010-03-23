
package hla.rti;

/**
 * Public exception class ObjectClassNotDefined
*/

public final class ObjectClassNotDefined extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ObjectClassNotDefined(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public ObjectClassNotDefined(String reason, int serial) {
	super(reason, serial);
  }  
}