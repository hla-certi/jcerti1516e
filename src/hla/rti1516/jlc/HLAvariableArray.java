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

public interface HLAvariableArray extends DataElement {
	@Override
	void encode(ByteWrapper byteWrapper);

	@Override
	void decode(ByteWrapper byteWrapper);

	@Override
	int getEncodedLength();

	@Override
	int getOctetBoundary();

	/**
	 * Adds an element to this variable array.
	 *
	 * @param dataElement
	 */
	void addElement(DataElement dataElement);

	/**
	 * Returns the number of elements in this variable array.
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
	 * Returns an iterator for the elements in this variable array.
	 *
	 * @return
	 */
	Iterator iterator();
}
