
package hla.rti;

/**
 * Public exception class InteractionClassNotSubscribed
*/

public final class InteractionClassNotSubscribed extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InteractionClassNotSubscribed(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InteractionClassNotSubscribed(String reason, int serial) {
	super(reason, serial);
  }  
}