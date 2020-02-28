/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516.jlc;

public interface HLAinteger16BE extends DataElement {
	@Override
	int getOctetBoundary();

	@Override
	void encode(ByteWrapper byteWrapper);

	@Override
	int getEncodedLength();

	@Override
	void decode(ByteWrapper byteWrapper);

	short getValue();
}
