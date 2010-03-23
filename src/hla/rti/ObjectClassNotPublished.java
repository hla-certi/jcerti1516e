
package hla.rti;

/**
 * Public exception class ObjectClassNotPublished
*/

public final class ObjectClassNotPublished extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ObjectClassNotPublished(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public ObjectClassNotPublished(String reason, int serial) {
	super(reason, serial);
  }  
}