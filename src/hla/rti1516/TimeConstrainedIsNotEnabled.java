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

 * Public exception class TimeConstrainedIsNotEnabled

*/

public final class TimeConstrainedIsNotEnabled extends RTIexception {
  public TimeConstrainedIsNotEnabled(String msg) {
    super(msg);
  }
}



//File: TimeQueryReturn.java

/**
 * Record returned by (8.16) queryLBTS and (8.18) queryMinimumNextEventTime
 */

