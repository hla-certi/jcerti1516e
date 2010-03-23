
package hla.rti;

/**
 * Public exception class SpaceNotDefined
*/

public final class SpaceNotDefined extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public SpaceNotDefined(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public SpaceNotDefined(String reason, int serial) {
	super(reason, serial);
  }  
}