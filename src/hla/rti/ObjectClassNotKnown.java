
package hla.rti;

/**
 * Public exception class ObjectClassNotKnown
*/

public final class ObjectClassNotKnown extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ObjectClassNotKnown(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public ObjectClassNotKnown(String reason, int serial) {
	super(reason, serial);
  }  
}