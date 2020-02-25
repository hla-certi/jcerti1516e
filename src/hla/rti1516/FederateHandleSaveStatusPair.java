/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

public final class FederateHandleSaveStatusPair implements java.io.Serializable {
	public FederateHandleSaveStatusPair(FederateHandle fh, SaveStatus ss) {
		handle = fh;
		status = ss;
	}

	public FederateHandle handle;
	public SaveStatus status;
}
//end FederateHandleSaveStatusPair

//File: FederateHandleSet.java
