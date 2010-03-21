
package hla.rti;

/**
 * Public exception class EventNotKnown
*/

public final class EventNotKnown extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public EventNotKnown(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public EventNotKnown(String reason, int serial) {
	super(reason, serial);
  }  
}