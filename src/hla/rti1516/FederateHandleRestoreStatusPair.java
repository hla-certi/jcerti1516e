/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

public final class FederateHandleRestoreStatusPair implements java.io.Serializable {
	public FederateHandleRestoreStatusPair(FederateHandle fh, RestoreStatus rs) {
		handle = fh;
		status = rs;
	}

	public FederateHandle handle;
	public RestoreStatus status;
}
//end FederateHandleRestoreStatusPair

//File: FederateHandleSaveStatusPair.java

/**
 * Array of these records returned by (4.17) federationSaveStatusResponse
 */
