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
 * LogicalTimeInterval declares an interface to an immutable time interval value
 */

public interface LogicalTimeInterval extends Comparable, java.io.Serializable 
{
   public boolean isZero();
   public boolean isEpsilon();

   /**
    * Returns a LogicalTimeInterval whose value is (this - subtrahend).
    */
   public LogicalTimeInterval subtract(LogicalTimeInterval subtrahend);
   
   public int compareTo(Object other);
   
   /**
    * Returns true iff this and other represent the same time interval.
    */
   public boolean equals(Object other);
   
   /**
    * Two LogicalTimeIntervals for which equals() is true should yield
    * same hash code
    */
   public int hashCode();
   
   public String toString();
   public int encodedLength();
   public void encode(byte[] buffer, int offset);
}
//end LogicalTimeInterval


