
package hla.rti;

public final class ConcurrentAccessAttempted extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ConcurrentAccessAttempted(String reason) {
	super(reason, 0);
  }      

  /**
   * @param serial    serial number also printed with the exception
   */
  public ConcurrentAccessAttempted(String reason, int serial) {
	super(reason, serial);
  }      
}