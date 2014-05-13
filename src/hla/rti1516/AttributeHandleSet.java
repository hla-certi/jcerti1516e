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
All Set operations are required, none are optional.
add() and remove() should throw IllegalArgumentException if the argument is not
an AttributeHandle.
addAll(), removeAll() and retainAll() should throw IllegalArgumentException if
the argument is not an AttributeHandleSet
*/

public interface AttributeHandleSet
  extends java.util.Set, Cloneable, java.io.Serializable {
}

//end AttributeHandleSet


//File: AttributeHandleSetFactory.java
