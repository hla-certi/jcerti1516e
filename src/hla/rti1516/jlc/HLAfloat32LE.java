/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */
 
 package hla.rti1516.jlc;

/**
 * Interface for the HLA data type HLAinteger32BE.
 */
public interface HLAfloat32LE extends DataElement
{
   int getOctetBoundary();

   void encode(ByteWrapper byteWrapper);

   int getEncodedLength();

   void decode(ByteWrapper byteWrapper);

   /**
    * Returns the float value of this element.
    *
    * @return float value
    */
   float getValue();
}



