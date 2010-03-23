
package hla.rti;

public final class OwnershipAcquisitionPending extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public OwnershipAcquisitionPending(String reason) {
	super(reason, 0);
  }      

  /**
   * @param serial    serial number also printed with the exception
   */
  public OwnershipAcquisitionPending(String reason, int serial) {
	super(reason, serial);
  }      
}