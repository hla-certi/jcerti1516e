/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

public final class TimeQueryReturn
    implements java.io.Serializable
{
  public TimeQueryReturn (boolean tiv, LogicalTime lt) {
    timeIsValid = tiv;
    time = lt;
  }

  public boolean     timeIsValid;
  public LogicalTime time;
  
  public boolean equals(Object other)
  {
     if (other instanceof TimeQueryReturn) {
        TimeQueryReturn tqrOther = (TimeQueryReturn)other;
        if (timeIsValid == false && tqrOther.timeIsValid == false) {
           return true;
        } else if (timeIsValid == true && tqrOther.timeIsValid == true) {
           return time.equals(tqrOther.time);
        } else {
           return false;
        }
     } else {
        return false;
     }
  }

   public int hashCode()
   {
      return (timeIsValid ? time.hashCode() : 7);
   }

   public String toString()
  {
     return "" + timeIsValid + " " + time;
  }
}
//end TimeQueryReturn



