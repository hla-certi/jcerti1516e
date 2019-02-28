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
import certi.rti.impl.CertiLogicalTime;
import certi.rti.impl.CertiLogicalTimeInterval;
import certi.rti.impl.CertiRtiAmbassador;
import hla.rti.*;
import hla.rti.jlc.*;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        
               if (((MyFederateAmbassador) mya).isCreator) 
        {
			System.out.println("     5 Press Enter to Launch Federation (Sync Point)");
			System.in.read();
			byte[] tagsync = EncodingHelpers.encodeString("InitSync");
           rtia.registerFederationSynchronizationPoint(((MyFederateAmbassador) mya).synchronizationPointName, tagsync);
            // Wait synchronization point callbacks.
            while (!(((MyFederateAmbassador) mya).synchronizationSuccess)
                    && !(((MyFederateAmbassador) mya).synchronizationFailed)) 
				{
                    ((CertiRtiAmbassador) rtia).tick2();
                }
		}
		else
		{
			System.out.println("     5 Wait for creator to Launch Federation (Sync Point) ");
		}
		
		 // Wait synchronization point announcement.
        while (!(((MyFederateAmbassador) mya).inPause)) {
                ((CertiRtiAmbassador) rtia).tick2();
        }

        // Satisfied synchronization point.
        rtia.synchronizationPointAchieved(((MyFederateAmbassador) mya).synchronizationPointName);

        // Wait federation synchronization.
        while (((MyFederateAmbassador) mya).inPause) 
        {
                ((CertiRtiAmbassador) rtia).tick2();
        }   

        System.out.println("     6 RAV Loop");

        int i = 20;
        while (i-- > 0) {
			System.out.println("     6.1 TAR with time=" + ((CertiLogicalTime) (((MyFederateAmbassador) mya).timeAdvance)).getTime());
			rtia.timeAdvanceRequest(((MyFederateAmbassador) mya).timeAdvance);
            while (!((MyFederateAmbassador) mya).timeAdvanceGranted)
            {
				((CertiRtiAmbassador) rtia).tick2();
			}
			((MyFederateAmbassador) mya).timeAdvanceGranted = false;
        }

        System.out.println("     7 Resign federation execution");
        rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES);

        System.out.println("     8 Destroy federation execution - nofail");
        try {
            rtia.destroyFederationExecution("uav");
        } catch (FederatesCurrentlyJoined ex) {
            LOGGER.warning("Federates currently joined - can't destroy the execution.");
        } catch (FederationExecutionDoesNotExist ex) {
            LOGGER.warning("Federation execution does not exists - can't destroy the execution.");
        }
    }

    public static void main(String[] args) throws Exception {
        new UavReceive().runFederate();
    }

    private class MyFederateAmbassador extends NullFederateAmbassador {
		
		public LogicalTime localHlaTime;
		public LogicalTimeInterval  lookahead;
		public LogicalTime  timeStep;
		public LogicalTime  timeAdvance;
		public boolean timeAdvanceGranted;
		public boolean timeRegulator;
		public boolean timeConstrained;
		public boolean synchronizationSuccess;
        public boolean synchronizationFailed;
		public boolean inPause;
		public boolean isCreator;
		private String synchronizationPointName = "InitSync";

        private RTIambassador rtia;

        public void initialize(RTIambassador rtia) 
			throws NameNotFound, ObjectClassNotDefined, FederateNotExecutionMember, RTIinternalError, AttributeNotDefined, 
			SaveInProgress, RestoreInProgress, ConcurrentAccessAttempted, AsynchronousDeliveryAlreadyEnabled,
			EnableTimeRegulationPending, EnableTimeConstrainedPending, TimeConstrainedAlreadyEnabled, 
			TimeRegulationAlreadyEnabled, TimeAdvanceAlreadyInProgress, InvalidFederationTime, InvalidLookahead { 
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
        public void discoverObjectInstance(int theObject, int theObjectClass, String objectName) throws CouldNotDiscover, ObjectClassNotKnown, FederateInternalError {
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
            //super.timeAdvanceGrant(theTime);
            
            localHlaTime = new CertiLogicalTime(((CertiLogicalTime) theTime).getTime());
            timeAdvance = new CertiLogicalTime(((CertiLogicalTime) localHlaTime).getTime() 
                                               + ((CertiLogicalTime) timeStep).getTime());
			System.out.println("     6.3 TAG with time=" + ((CertiLogicalTime) theTime).getTime());
			System.out.println("");
            timeAdvanceGranted = true;
        }
        

        @Override
        public void reflectAttributeValues(int theObject, ReflectedAttributes theAttributes, byte[] userSuppliedTag, LogicalTime theTime, EventRetractionHandle retractionHandle) 
			throws ObjectNotKnown, AttributeNotKnown, FederateOwnsAttributes, FederateInternalError {
            try {
				System.out.println("     6.2 RAV with time=" + ((CertiLogicalTime) theTime).getTime());
                for (int i = 0; i < theAttributes.size(); i++) {
                    if (theAttributes.getAttributeHandle(i) == textAttributeHandle) {
                        System.out.println("     --> Attribute: " + EncodingHelpers.decodeString(theAttributes.getValue(i)));
                    }
                    if (theAttributes.getAttributeHandle(i) == fomAttributeHandle) {
                        System.out.println("     --> Attribute: " + EncodingHelpers.decodeFloat(theAttributes.getValue(i)));
                    }
                }
            } catch (ArrayIndexOutOfBounds ex) {
                LOGGER.log(Level.SEVERE, "Exception:", ex);
            }
        }
        
        /** Callback delivered by the RTI (CERTI) to notify if the synchronization
         *  point registration has failed.
         */
        @Override
        public void synchronizationPointRegistrationFailed(
                String synchronizationPointLabel) throws FederateInternalError {
            synchronizationFailed = true;
        }

        /** Callback delivered by the RTI (CERTI) to notify if the synchronization
         *  point registration has succeed.
         */
        @Override
        public void synchronizationPointRegistrationSucceeded(
                String synchronizationPointLabel) throws FederateInternalError {
            synchronizationSuccess = true;
        }

        /** Callback delivered by the RTI (CERTI) to notify the announcement of
         *  a synchronization point in the HLA Federation.
         */
        @Override
        public void announceSynchronizationPoint(
                String synchronizationPointLabel, byte[] userSuppliedTag)
                throws FederateInternalError {
            inPause = true;
        }

        /** Callback delivered by the RTI (CERTI) to notify that the Federate is
         *  synchronized to others Federates using the same synchronization point
         *  in the HLA Federation.
         */
        @Override
        public void federationSynchronized(String synchronizationPointLabel)
                throws FederateInternalError {
            inPause = false;
		}
    }
}
