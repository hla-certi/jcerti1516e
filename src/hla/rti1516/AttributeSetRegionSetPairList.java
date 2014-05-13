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
 * This packages the attributes supplied to the RTI for various DDM services with
 * the regions to be used with the attributes.
 * Elements are AttributeRegionAssociations.
 * All operations are required, none optional.
 * add(), addAll(), and set() should throw IllegalArgumentException to enforce
 * type of elements.
 */
public interface AttributeSetRegionSetPairList
  extends java.util.List, Cloneable, java.io.Serializable {
}

//end AttributeSetRegionSetPairList


//File: AttributeSetRegionSetPairListFactory.java
