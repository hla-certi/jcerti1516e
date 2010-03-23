
package hla.rti;

/**
 * Conveys the interfaces for all services that a federate
 * must supply and which may not execute in the federate's
 * space.
 * 
 */
public final class MobileFederateServices {
	public hla.rti.LogicalTimeFactory _timeFactory;
	public hla.rti.LogicalTimeIntervalFactory _intervalFactory;

/**
 * This method was created by a SmartGuide.
 * @param timeFactory hla.rti.LogicalTimeFactory
 * @param intervalFactory hla.rti.LogicalTimeIntervalFactory
 */
public MobileFederateServices (LogicalTimeFactory timeFactory, LogicalTimeIntervalFactory intervalFactory) {
	_timeFactory = timeFactory;
	_intervalFactory = intervalFactory;
}
}