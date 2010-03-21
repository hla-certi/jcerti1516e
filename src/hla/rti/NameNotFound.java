
package hla.rti;

/**
 * Public exception class NameNotFound
*/

public final class NameNotFound extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public NameNotFound(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public NameNotFound(String reason, int serial) {
	super(reason, serial);
  }  
}