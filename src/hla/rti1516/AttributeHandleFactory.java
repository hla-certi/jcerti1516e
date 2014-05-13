/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

/**
 * The factory is used only (outside RTI) to create AttributeHandle
 * received as an attribute value or parameter value.
 */

public interface AttributeHandleFactory extends java.io.Serializable {
	public AttributeHandle decode(byte[] buffer, int offset)
	  throws CouldNotDecode, FederateNotExecutionMember;
}



//File: AttributeHandleSet.java

