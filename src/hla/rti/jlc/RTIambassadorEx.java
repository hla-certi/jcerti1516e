/*
 * This file comes from SISO STD-004-2004 for HLA 1.3
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti.jlc;

import hla.rti.RTIambassador;

public interface RTIambassadorEx extends RTIambassador {
	boolean tick(final double min, final double max) throws hla.rti.RTIinternalError, hla.rti.ConcurrentAccessAttempted;
}
