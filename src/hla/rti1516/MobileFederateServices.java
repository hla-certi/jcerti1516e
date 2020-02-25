/*
 * This file comes from SISO STD-004.1-2004 for HLA1516
 * from http://www.sisostds.org/ProductsPublications/Standards/SISOStandards.aspx.
 * see also updated version from 2006:
 * http://discussions.sisostds.org/threadview.aspx?threadid=40014
 *
 * It is provided as-is by CERTI project.
 */

package hla.rti1516;

/**
 * Conveys the interfaces for all services that a federate must supply and which
 * may not execute in the federate's space.
 *
 */
public final class MobileFederateServices implements java.io.Serializable {
	public hla.rti1516.LogicalTimeFactory _timeFactory;
	public hla.rti1516.LogicalTimeIntervalFactory _intervalFactory;

	/**
	 * @param timeFactory     hla.rti1516.LogicalTimeFactory
	 * @param intervalFactory hla.rti1516.LogicalTimeIntervalFactory
	 */
	public MobileFederateServices(LogicalTimeFactory timeFactory, LogicalTimeIntervalFactory intervalFactory) {
		_timeFactory = timeFactory;
		_intervalFactory = intervalFactory;
	}
}

//end MobileFederateServices
