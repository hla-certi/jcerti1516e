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
 * The user can do nothing with these but employ them as keys.
 * Implementers should provide equals, hashCode and toString
 * rather than settling for the defaults.
 * 
 */
public interface MessageRetractionHandle extends java.io.Serializable {

  /**
   * @return true if this refers to the same Message as other handle
   */
  public boolean equals(Object otherMRHandle);

  /**
   * @return int. All instances that refer to the same Message should return the
   * same hashcode.
   */
  public int hashCode();

  public String toString();
}
//end MessageRetractionHandle


//File: MessageRetractionReturn.java

/**
 * Record returned by updateAttributeValues, sendInteraction, and deleteObject
 */

