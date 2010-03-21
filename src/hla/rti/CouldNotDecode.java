
package hla.rti;

public final class CouldNotDecode extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public CouldNotDecode(String reason) {
	super(reason, 0);
  }      

  /**
   * @param serial    serial number also printed with the exception
   */
  public CouldNotDecode(String reason, int serial) {
	super(reason, serial);
  }      
}