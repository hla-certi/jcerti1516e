
package hla.rti;

/**
 * Public exception class InteractionClassNotPublished
*/

public final class InteractionClassNotPublished extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InteractionClassNotPublished(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InteractionClassNotPublished(String reason, int serial) {
	super(reason, serial);
  }  
}