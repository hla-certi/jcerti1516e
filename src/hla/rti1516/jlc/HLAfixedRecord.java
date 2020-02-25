/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516.jlc;

import java.util.Iterator;

/**
 * Interface for the HLA data type HLAfixedRecord.
 */
public interface HLAfixedRecord extends DataElement {
	@Override
	int getOctetBoundary();

	@Override
	void encode(ByteWrapper byteWrapper);

	@Override
	int getEncodedLength();

	@Override
	void decode(ByteWrapper byteWrapper);

	/**
	 * Adds an element to this fixed record.
	 *
	 * @param dataElement
	 */
	void add(DataElement dataElement);

	/**
	 * Returns the number of elements in this fixed record.
	 *
	 * @return
	 */
	int size();

	/**
	 * Returns element at the specified index.
	 *
	 * @param index
	 * @return
	 */
	DataElement get(int index);

	/**
	 * Returns an iterator for the elements in this fixed record.
	 *
	 * @return
	 */
	Iterator iterator();
}
