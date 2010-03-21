
package hla.rti;

/**
 * Public exception class InteractionParameterNotKnown
*/

public final class InteractionParameterNotKnown extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InteractionParameterNotKnown(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InteractionParameterNotKnown(String reason, int serial) {
	super(reason, serial);
  }  
}