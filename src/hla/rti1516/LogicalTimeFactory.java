/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

public interface LogicalTimeFactory extends java.io.Serializable {

	public LogicalTime decode(byte[] buffer, int offset)
	  throws CouldNotDecode;
	public LogicalTime makeInitial();
	public LogicalTime makeFinal();
}



//File: LogicalTimeInterval.java
