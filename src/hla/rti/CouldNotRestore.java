
package hla.rti;

/**
 * Public exception class CouldNotRestore
*/

public final class CouldNotRestore extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public CouldNotRestore(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public CouldNotRestore(String reason, int serial) {
	super(reason, serial);
  }  
}