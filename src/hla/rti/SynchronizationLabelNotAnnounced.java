
package hla.rti;

/**
 * Public exception class AttributeNotDefined
*/
public final class SynchronizationLabelNotAnnounced extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public SynchronizationLabelNotAnnounced(String reason) {
	super(reason, 0);
  }  

  /**
   * @param serial    serial number also printed with the exception
   */
  public SynchronizationLabelNotAnnounced(String reason, int serial) {
	super(reason, serial);
  }  
}