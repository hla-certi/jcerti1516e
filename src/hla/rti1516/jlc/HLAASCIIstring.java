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
 * Interface for the HLA data type HLAunicodeString.
 */
public interface HLAASCIIstring extends DataElement {
	@Override
	void encode(ByteWrapper byteWrapper);

	@Override
	void decode(ByteWrapper byteWrapper);

	@Override
	int getEncodedLength();

	@Override
	int getOctetBoundary();

	/**
	 * Returns the string value of this element.
	 *
	 * @return string value
	 */
	String getValue();
}
