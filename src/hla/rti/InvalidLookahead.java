
package hla.rti;

/**
 * Public exception class InvalidLookahead
*/

public final class InvalidLookahead extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public InvalidLookahead(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public InvalidLookahead(String reason, int serial) {
	super(reason, serial);
  }  
}