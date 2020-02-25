/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516.jlc;

public interface DataElement {
	/**
	 * Returns the octet boundary of this element.
	 *
	 * @return
	 */
	int getOctetBoundary();

	/**
	 * Encodes this element into the specified ByteWrapper.
	 *
	 * @param byteWrapper
	 */
	void encode(ByteWrapper byteWrapper);

	/**
	 * Returns the size in bytes of this element's encoding.
	 *
	 * @return size
	 */
	int getEncodedLength();

	/**
	 * Returns a byte array with this element encoded.
	 *
	 * @return byte array with encoded element
	 */
	byte[] toByteArray();

	/**
	 * Decodes this element from the ByteWrapper.
	 *
	 * @param byteWrapper
	 */
	void decode(ByteWrapper byteWrapper);
}
