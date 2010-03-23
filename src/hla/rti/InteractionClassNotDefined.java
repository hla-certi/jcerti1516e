
package hla.rti;

/**
 * Public exception class InteractionClassNotDefined
*/

public final class InteractionClassNotDefined extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InteractionClassNotDefined(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InteractionClassNotDefined(String reason, int serial) {
	super(reason, serial);
  }  
}