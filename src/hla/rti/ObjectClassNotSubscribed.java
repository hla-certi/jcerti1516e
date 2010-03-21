
package hla.rti;

/**
 * Public exception class ObjectClassNotSubscribed
*/

public final class ObjectClassNotSubscribed extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ObjectClassNotSubscribed(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public ObjectClassNotSubscribed(String reason, int serial) {
	super(reason, serial);
  }  
}