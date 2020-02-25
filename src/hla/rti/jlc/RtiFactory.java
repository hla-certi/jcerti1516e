/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti.jlc;

import hla.rti.AttributeHandleSet;
import hla.rti.FederateHandleSet;
import hla.rti.SuppliedAttributes;
import hla.rti.SuppliedParameters;

public interface RtiFactory {
	RTIambassadorEx createRtiAmbassador() throws hla.rti.RTIinternalError;

	AttributeHandleSet createAttributeHandleSet();

	FederateHandleSet createFederateHandleSet();

	SuppliedAttributes createSuppliedAttributes();

	SuppliedParameters createSuppliedParameters();

	String RtiName();

	String RtiVersion();

	long getMinExtent();

	long getMaxExtent();
}
