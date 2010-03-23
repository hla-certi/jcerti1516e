
package hla.rti;

public final class ArrayIndexOutOfBounds extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public ArrayIndexOutOfBounds(String reason) {
	super(reason, 0);
  }    

  /**
   * @param serial    serial number also printed with the exception
   */
  public ArrayIndexOutOfBounds(String reason, int serial) {
	super(reason, serial);
  }    
}