
package hla.rti;

/**
 * Public exception class InteractionParameterNotDefined
*/

public final class InteractionParameterNotDefined extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InteractionParameterNotDefined(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InteractionParameterNotDefined(String reason, int serial) {
	super(reason, serial);
  }  
}