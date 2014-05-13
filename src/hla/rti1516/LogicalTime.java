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
 * LogicalTime declares an interface to an immutable time value
 */

public interface LogicalTime extends Comparable, java.io.Serializable
{
   public boolean isInitial();
   public boolean isFinal();

   /**
    * Returns a LogicalTime whose value is (this + val).
    */
   public LogicalTime add(LogicalTimeInterval val)
   throws IllegalTimeArithmetic;
   
   /**
    * Returns a LogicalTime whose value is (this - val).
    */
   public LogicalTime subtract(LogicalTimeInterval val)
   throws IllegalTimeArithmetic;
   
   /**
    * Returns a LogicalTimeInterval whose value is the time
    * interval between this and val.
    */
   public LogicalTimeInterval distance(LogicalTime val);

   public int compareTo(Object other);
   
   /**
    * Returns true iff this and other represent the same logical time
    * Supports standard Java mechanisms.
    */
   public boolean equals(Object other);
   
   /**
    * Two LogicalTimes for which equals() is true should yield
    * same hash code
    */
   public int hashCode();
   
   public String toString();
   public int encodedLength();
   public void encode(byte[] buffer, int offset);

}//end LogicalTime


