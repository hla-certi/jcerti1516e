/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

public final class RangeBounds
   implements java.io.Serializable {
   public RangeBounds(long l, long u)
   {
      lower = l;
      upper = u;
   }

   public long lower;
   public long upper;

   public boolean equals(Object other)
   {
      if (other != null && other instanceof RangeBounds) {
         RangeBounds otherRangeBounds = (RangeBounds)other;
         return lower == otherRangeBounds.lower && upper == otherRangeBounds.upper;
      } else {
         return false;
      }
   }

   public int hashCode()
   {
      return (int)(lower + upper);
   }
}

//end RangeBounds



