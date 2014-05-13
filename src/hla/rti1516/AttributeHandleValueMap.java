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
Keys are AttributeHandles; values are byte[].
All operations are required, none optional.
Null mappings are not allowed.
put(), putAll(), and remove() should throw IllegalArgumentException to enforce
types of keys and mappings.
 */
public interface AttributeHandleValueMap
  extends java.util.Map, Cloneable, java.io.Serializable {
}
//end AttributeHandleValueMap



//File: AttributeHandleValueMapFactory.java
