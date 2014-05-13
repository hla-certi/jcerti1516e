/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516;

public final class MessageRetractionReturn
    implements java.io.Serializable
{
  public MessageRetractionReturn(boolean rhiv, MessageRetractionHandle mrh) {
    retractionHandleIsValid = rhiv;
    handle = mrh;
  }

  public boolean                 retractionHandleIsValid;
  public MessageRetractionHandle handle;
}
//end MessageRetractionReturn



//File: MobileFederateServices.java

