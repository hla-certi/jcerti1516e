// ----------------------------------------------------------------------------
// CERTI - HLA Run Time Infrastructure
// Copyright (C) 2009-2010 Andrej Pancik
//
// This program is free software ; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public License
// as published by the Free Software Foundation ; either version 2 of
// the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful, but
// WITHOUT ANY WARRANTY ; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program ; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
//
// ----------------------------------------------------------------------------
package certi;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import certi.rti.impl.CertiLogicalTime;
import certi.rti.impl.CertiLogicalTimeInterval;
import certi.rti.impl.CertiRtiAmbassador;
import hla.rti.ArrayIndexOutOfBounds;
import hla.rti.AsynchronousDeliveryAlreadyEnabled;
import hla.rti.AttributeHandleSet;
import hla.rti.AttributeNotDefined;
import hla.rti.AttributeNotKnown;
import hla.rti.ConcurrentAccessAttempted;
import hla.rti.CouldNotDiscover;
import hla.rti.EnableTimeConstrainedPending;
import hla.rti.EnableTimeRegulationPending;
import hla.rti.EventRetractionHandle;
import hla.rti.FederateAmbassador;
import hla.rti.FederateInternalError;
import hla.rti.FederateNotExecutionMember;
import hla.rti.FederateOwnsAttributes;
import hla.rti.FederatesCurrentlyJoined;
import hla.rti.FederationExecutionAlreadyExists;
import hla.rti.FederationExecutionDoesNotExist;
import hla.rti.InvalidFederationTime;
import hla.rti.InvalidLookahead;
import hla.rti.LogicalTime;
import hla.rti.LogicalTimeInterval;
import hla.rti.NameNotFound;
import hla.rti.ObjectClassNotDefined;
import hla.rti.ObjectClassNotKnown;
import hla.rti.ObjectNotKnown;
import hla.rti.RTIambassador;
import hla.rti.RTIexception;
import hla.rti.RTIinternalError;
import hla.rti.ReflectedAttributes;
import hla.rti.ResignAction;
import hla.rti.RestoreInProgress;
import hla.rti.SaveInProgress;
import hla.rti.TimeAdvanceAlreadyInProgress;
import hla.rti.TimeAdvanceWasNotInProgress;
import hla.rti.TimeConstrainedAlreadyEnabled;
import hla.rti.TimeRegulationAlreadyEnabled;
import hla.rti.jlc.EncodingHelpers;
import hla.rti.jlc.NullFederateAmbassador;
import hla.rti.jlc.RtiFactory;
import hla.rti.jlc.RtiFactoryFactory;

public class UavReceive {

	private final static Logger LOGGER = Logger.getLogger(UavReceive.class.getName());
	private int textAttributeHandle;
	private int fomAttributeHandle;
	private AttributeHandleSet attributes;

	public void runFederate() throws Exception {
		/////////////////
		// UAV-RECEIVE //
		/////////////////
		System.out.println("        UAV-RECEIVE");
		System.out.println("     1. Get a link to the RTI");
		RtiFactory factory = RtiFactoryFactory.getRtiFactory();
		RTIambassador rtia = factory.createRtiAmbassador();
		boolean flagCreator;

		System.out.println("     2. Create federation - nofail");
		try {
			File fom = new File("uav.fed");
			rtia.createFederationExecution("uav", fom.toURI().toURL());
			flagCreator = true;
		} catch (FederationExecutionAlreadyExists ex) {
			LOGGER.warning("Federation already exists.");
			flagCreator = false;
		}

		System.out.println("     3. Join federation");
		FederateAmbassador mya = new MyFederateAmbassador();
		rtia.joinFederationExecution("uav-receive", "uav", mya);
		((MyFederateAmbassador) mya).isCreator = flagCreator;

		System.out.println("     4. Initialize Federate Ambassador");
		((MyFederateAmbassador) mya).initialize(rtia);

		if (((MyFederateAmbassador) mya).isCreator) {
			System.out.println("     5 Press Enter to Launch Federation (Sync Point)");
			System.in.read();
			byte[] tagsync = EncodingHelpers.encodeString("InitSync");
			rtia.registerFederationSynchronizationPoint(((MyFederateAmbassador) mya).synchronizationPointName, tagsync);
			// Wait synchronization point callbacks.
			while (!(((MyFederateAmbassador) mya).synchronizationSuccess)
					&& !(((MyFederateAmbassador) mya).synchronizationFailed)) {
				((CertiRtiAmbassador) rtia).tick2();
			}
		} else {
			System.out.println("     5 Wait for creator to Launch Federation (Sync Point) ");
		}

		// Wait synchronization point announcement.
		while (!(((MyFederateAmbassador) mya).inPause)) {
			((CertiRtiAmbassador) rtia).tick2();
		}

		// Satisfied synchronization point.
		rtia.synchronizationPointAchieved(((MyFederateAmbassador) mya).synchronizationPointName);

		// Wait federation synchronization.
		while (((MyFederateAmbassador) mya).inPause) {
			((CertiRtiAmbassador) rtia).tick2();
		}

		System.out.println("     6 RAV Loop");

		int i = 3;
		while (i-- > 0) {
			System.out.println("     6.1 TAR with time="
					+ ((CertiLogicalTime) (((MyFederateAmbassador) mya).timeAdvance)).getTime());
			rtia.timeAdvanceRequest(((MyFederateAmbassador) mya).timeAdvance);
			while (!((MyFederateAmbassador) mya).timeAdvanceGranted) {
				((CertiRtiAmbassador) rtia).tick2();
			}
			((MyFederateAmbassador) mya).timeAdvanceGranted = false;
		}

		System.out.println("     7 Resign federation execution");
		try {
			rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES);
		} catch (RTIexception e) {
			LOGGER.warning("Exception when resigning.");
			// What happens with the federate if the exception is risen?
			// Add a "finally" with the block related to destroyFederationExecution?
		}
		System.out.println("     8 Try to destroy federation execution - nofail");

		// Uses a loop with for destroying the federation (with a delay if
		// there are other federates that did not resign yet).
		boolean federationIsActive = true;
		try {
			while (federationIsActive) {
				try {
					rtia.destroyFederationExecution("uav");
					federationIsActive = false;
					LOGGER.warning("Federation destroyed by this federate.");
				} catch (FederatesCurrentlyJoined ex) {
					LOGGER.warning("Federates currently joined - can't destroy the execution."
							+ " Wait some time and try again to destroy the federation.");
					try {
						// Give the other federates a chance to finish.
						Thread.sleep(2000l);
					} catch (InterruptedException e1) {
						// Ignore.
					}
				} catch (FederationExecutionDoesNotExist ex) {
					LOGGER.warning("Federation execution does not exists;"
							+ "May be the Federation was destroyed by some other federate.");
					federationIsActive = false;
				} catch (RTIinternalError e) {
					System.out.println("RTIinternalError: " + e.getMessage());
				} catch (ConcurrentAccessAttempted e) {
					System.out.println("ConcurrentAccessAttempted: " + e.getMessage());
				}
			} // while
		} finally { // always execute this block whether an exception is risen or not
			// Uses closeConnexion so the federate can stops running.
			// No rtia alive with this code.
			((CertiRtiAmbassador) rtia).closeConnexion();
		}
	} // run federate

	public static void main(String[] args) throws Exception {
		new UavReceive().runFederate();
	}

	private class MyFederateAmbassador extends NullFederateAmbassador {

		public LogicalTime localHlaTime;
		public LogicalTimeInterval lookahead;
		public LogicalTime timeStep;
		public LogicalTime timeAdvance;
		public boolean timeAdvanceGranted;
		public boolean synchronizationSuccess;
		public boolean synchronizationFailed;
		public boolean inPause;
		public boolean isCreator;
		private String synchronizationPointName = "InitSync";

		private RTIambassador rtia;

		public void initialize(RTIambassador rtia) throws NameNotFound, ObjectClassNotDefined,
				FederateNotExecutionMember, RTIinternalError, AttributeNotDefined, SaveInProgress, RestoreInProgress,
				ConcurrentAccessAttempted, AsynchronousDeliveryAlreadyEnabled, EnableTimeRegulationPending,
				EnableTimeConstrainedPending, TimeConstrainedAlreadyEnabled, TimeRegulationAlreadyEnabled,
				TimeAdvanceAlreadyInProgress, InvalidFederationTime, InvalidLookahead {
			this.rtia = rtia;

			rtia.enableAsynchronousDelivery();
			int classHandle = rtia.getObjectClassHandle("SampleClass");

			textAttributeHandle = rtia.getAttributeHandle("TextAttribute", classHandle);
			fomAttributeHandle = rtia.getAttributeHandle("FOMAttribute", classHandle);

			attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
			attributes.add(textAttributeHandle);
			attributes.add(fomAttributeHandle);

			rtia.subscribeObjectClassAttributes(classHandle, attributes);

			localHlaTime = new CertiLogicalTime(0.0);
			lookahead = new CertiLogicalTimeInterval(0.1);
			timeStep = new CertiLogicalTime(1.0);
			timeAdvance = new CertiLogicalTime(1.0);
			timeAdvanceGranted = false;
			rtia.enableTimeRegulation(localHlaTime, lookahead);
			rtia.enableTimeConstrained();
		}

		@Override
		public void discoverObjectInstance(int theObject, int theObjectClass, String objectName)
				throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError {
			try {
				System.out.println("Discover: " + objectName);
				rtia.requestObjectAttributeValueUpdate(theObject, attributes);
			} catch (ObjectNotKnown ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (AttributeNotDefined ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (FederateNotExecutionMember ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (SaveInProgress ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (RestoreInProgress ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (RTIinternalError ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			} catch (ConcurrentAccessAttempted ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			}
		}

		@Override
		public void timeAdvanceGrant(LogicalTime theTime)
				throws InvalidFederationTime, FederateInternalError, TimeAdvanceWasNotInProgress {
			// super.timeAdvanceGrant(theTime);

			localHlaTime = new CertiLogicalTime(((CertiLogicalTime) theTime).getTime());
			timeAdvance = new CertiLogicalTime(
					((CertiLogicalTime) localHlaTime).getTime() + ((CertiLogicalTime) timeStep).getTime());
			System.out.println("     6.3 TAG with time=" + ((CertiLogicalTime) theTime).getTime());
			System.out.println("");
			timeAdvanceGranted = true;
		}

		@Override
		public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag,
				LogicalTime theTime, EventRetractionHandle retractionHandle)
				throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, FederateInternalError {
			try {
				System.out.println("     6.2 RAV with time=" + ((CertiLogicalTime) theTime).getTime());
				for (int i = 0; i < theAttributes.size(); i++) {
					if (theAttributes.getAttributeHandle(i) == textAttributeHandle) {
						System.out.println(
								"     --> Attribute: " + EncodingHelpers.decodeString(theAttributes.getValue(i)));
					}
					if (theAttributes.getAttributeHandle(i) == fomAttributeHandle) {
						System.out.println(
								"     --> Attribute: " + EncodingHelpers.decodeFloat(theAttributes.getValue(i)));
					}
				}
			} catch (ArrayIndexOutOfBounds ex) {
				LOGGER.log(Level.SEVERE, "Exception:", ex);
			}
		}

		/**
		 * Callback delivered by the RTI (CERTI) to notify if the synchronization point
		 * registration has failed.
		 */
		@Override
		public void synchronizationPointRegistrationFailed(String synchronizationPointLabel)
				throws FederateInternalError {
			synchronizationFailed = true;
		}

		/**
		 * Callback delivered by the RTI (CERTI) to notify if the synchronization point
		 * registration has succeed.
		 */
		@Override
		public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
				throws FederateInternalError {
			synchronizationSuccess = true;
		}

		/**
		 * Callback delivered by the RTI (CERTI) to notify the announcement of a
		 * synchronization point in the HLA Federation.
		 */
		@Override
		public void announceSynchronizationPoint(String synchronizationPointLabel, byte[] userSuppliedTag)
				throws FederateInternalError {
			inPause = true;
		}

		/**
		 * Callback delivered by the RTI (CERTI) to notify that the Federate is
		 * synchronized to others Federates using the same synchronization point in the
		 * HLA Federation.
		 */
		@Override
		public void federationSynchronized(String synchronizationPointLabel) throws FederateInternalError {
			inPause = false;
		}
	}
}
