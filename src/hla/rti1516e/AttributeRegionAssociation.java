/*
 * The IEEE hereby grants a general, royalty-free license to copy, distribute,
 * display and make derivative works from this material, for all purposes,
 * provided that any use of the material contains the following
 * attribution: "Reprinted with permission from IEEE 1516.1(TM)-2010".
 * Should you require additional information, contact the Manager, Standards
 * Intellectual Property, IEEE Standards Association (stds-ipr@ieee.org).
 */

//File: AttributeRegionAssociation.java

/**
 * Record stored in AttributeSetRegionSetPairList
 */

package hla.rti1516e;

import java.io.Serializable;

public final class AttributeRegionAssociation implements Serializable {
	public AttributeRegionAssociation(AttributeHandleSet ahs, RegionHandleSet rhs) {
		ahset = ahs;
		rhset = rhs;
	}

	public final AttributeHandleSet ahset;
	public final RegionHandleSet rhset;
}

//end AttributeRegionAssociation
