
package hla.rti;

/**
 * Public exception class InteractionClassNotKnown
*/

public final class InteractionClassNotKnown extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InteractionClassNotKnown(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InteractionClassNotKnown(String reason, int serial) {
	super(reason, serial);
  }  
}