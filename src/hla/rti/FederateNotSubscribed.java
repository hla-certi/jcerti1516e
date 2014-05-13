/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */
 

package hla.rti;


public final class FederateNotSubscribed extends RTIexception {

  /**
   * @param reason    String to be carried with exception
   */
  public FederateNotSubscribed(String reason) {
	super(reason, 0);
  }

  /**
   * @param serial    serial number also printed with the exception
   */
  public FederateNotSubscribed(String reason, int serial) {
	super(reason, serial);
  }
}
