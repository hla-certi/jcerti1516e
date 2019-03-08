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

public class UavSend  {

    private final static Logger LOGGER = Logger.getLogger(UavSend.class.getName());
    private int myObject;
    private int textAttributeHandle;
    private int fomAttributeHandle;

    public void runFederate() throws Exception {
		
        //////////////
        // UAV-SEND //
        //////////////
        System.out.println("        UAV-SEND");
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
            LOGGER.warning("Can't create federation. It already exists.");
			flagCreator = false;
        }

        System.out.println("     3. Join federation");
        FederateAmbassador mya = new MyFederateAmbassador();
        rtia.joinFederationExecution("uav-send", "uav", mya);
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
        
        int i = 3;
		System.out.println("     6 UAV Loop");
        while (i --> 0) 
        {
           
            SuppliedAttributes attributes = RtiFactoryFactory.getRtiFactory().createSuppliedAttributes();
            byte[] textAttribute = EncodingHelpers.encodeString("text " + i);
            byte[] fomAttribute = EncodingHelpers.encodeFloat((float) Math.PI);

            attributes.add(textAttributeHandle, textAttribute);
            attributes.add(fomAttributeHandle, fomAttribute);

            byte[] tag = EncodingHelpers.encodeString("update");

            System.out.println("     6.1 UAV with time=" + ((CertiLogicalTime) (((MyFederateAmbassador) mya).updateTime)).getTime());
            rtia.updateAttributeValues(myObject, attributes, tag, ((MyFederateAmbassador) mya).updateTime);

            System.out.println("     6.2 TAR with time=" + ((CertiLogicalTime) (((MyFederateAmbassador) mya).timeAdvance)).getTime());
            rtia.timeAdvanceRequest(((MyFederateAmbassador) mya).timeAdvance);
            while (!((MyFederateAmbassador) mya).timeAdvanceGranted)
            {
				((CertiRtiAmbassador) rtia).tick2();
			}
			((MyFederateAmbassador) mya).timeAdvanceGranted = false;
            Thread.sleep(1000);
        }
 
        System.out.println("     7 Resign federation execution");
        rtia.resignFederationExecution(ResignAction.DELETE_OBJECTS_AND_RELEASE_ATTRIBUTES);

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
                            +  "May be the Federation was destroyed by some other federate.");
                    federationIsActive = false;
                }      catch (RTIinternalError e) {
                    System.out.println("RTIinternalError: " + e.getMessage());
                } catch (ConcurrentAccessAttempted e) {
                    System.out.println("ConcurrentAccessAttempted: " + e.getMessage());
                }              
         }// while
        } finally { //always execute this block whether an exception is risen or not
             // Uses closeConnexion so the federate can stops running. 
             // No rtia alive with this code.
            ((CertiRtiAmbassador) rtia).closeConnexion();
           }     } // run federate
    public static void main(String[] args) throws Exception {
        new UavSend().runFederate();
    }

    private class MyFederateAmbassador extends NullFederateAmbassador {
		
		public LogicalTime localHlaTime;
		public LogicalTimeInterval  lookahead;
		public LogicalTime  timeStep;
		public LogicalTime  timeAdvance;
		public LogicalTime  updateTime;
		public boolean timeAdvanceGranted;
		public boolean timeRegulator;
		public boolean timeConstrained;
        public boolean synchronizationSuccess;
        public boolean synchronizationFailed;
		public boolean inPause;
		public boolean isCreator;
		private String synchronizationPointName = "InitSync";

        public void initialize(RTIambassador rtia) 
			throws NameNotFound, FederateNotExecutionMember, RTIinternalError, ObjectClassNotDefined, 
			       AttributeNotDefined, OwnershipAcquisitionPending, SaveInProgress, RestoreInProgress, AsynchronousDeliveryAlreadyEnabled,
			       EnableTimeRegulationPending, EnableTimeConstrainedPending, TimeConstrainedAlreadyEnabled, TimeRegulationAlreadyEnabled, 
			       ConcurrentAccessAttempted, ObjectClassNotPublished, ObjectAlreadyRegistered, TimeAdvanceAlreadyInProgress, 
			       InvalidFederationTime, InvalidLookahead {
            System.out.println("     4.1 Get object class handle");
            int classHandle = rtia.getObjectClassHandle("SampleClass");

            System.out.println("     4.2 Get atribute handles");
            textAttributeHandle = rtia.getAttributeHandle("TextAttribute", classHandle);
            fomAttributeHandle = rtia.getAttributeHandle("FOMAttribute", classHandle);

            AttributeHandleSet attributes = RtiFactoryFactory.getRtiFactory().createAttributeHandleSet();
            attributes.add(textAttributeHandle);
            attributes.add(fomAttributeHandle);
            
			rtia.enableAsynchronousDelivery();

            System.out.println("     4.3 Publish object");
            rtia.publishObjectClass(classHandle, attributes);

            System.out.println("     4.4 Register object instance");
            myObject = rtia.registerObjectInstance(classHandle, "HAF");
            
			System.out.println("     5. Set time management configuration (Regulator with lk=0.1 and Constrained)");
			localHlaTime = new CertiLogicalTime(0.0);
			lookahead = new CertiLogicalTimeInterval(0.1);
			timeStep = new CertiLogicalTime(1.0);
			timeAdvance = new CertiLogicalTime(1.0);
			updateTime = new CertiLogicalTime(0.2);
			timeAdvanceGranted = false;
			rtia.enableTimeRegulation(localHlaTime, lookahead);
			rtia.enableTimeConstrained();
        }

        @Override
        public void startRegistrationForObjectClass(int theClass) throws ObjectClassNotPublished, FederateInternalError {
            super.startRegistrationForObjectClass(theClass);

            System.out.println("Object class: " + theClass);
        }
        
        @Override
        public void timeAdvanceGrant(LogicalTime theTime) 
				throws InvalidFederationTime, FederateInternalError, TimeAdvanceWasNotInProgress {
            //super.timeAdvanceGrant(theTime);
            
            localHlaTime = new CertiLogicalTime(((CertiLogicalTime) theTime).getTime());
            timeAdvance = new CertiLogicalTime(((CertiLogicalTime) localHlaTime).getTime() 
                                               + ((CertiLogicalTime) timeStep).getTime());
			updateTime = new CertiLogicalTime(((CertiLogicalTime) localHlaTime).getTime() + 0.2);
			System.out.println("     6.3 TAG with time=" + ((CertiLogicalTime) theTime).getTime());
			System.out.println("");
            timeAdvanceGranted = true;
        }
        
        @Override
        public void provideAttributeValueUpdate(int theObject, AttributeHandleSet theAttributes) throws ObjectNotKnown, AttributeNotKnown, AttributeNotOwned, FederateInternalError {
            super.provideAttributeValueUpdate(theObject, theAttributes);

            System.out.println("Object handle: " + theObject);
            System.out.print("Attributes:");
            HandleIterator iterator = theAttributes.handles();
            for (int i = 1; i < theAttributes.size(); i++) {
                System.out.print(iterator.next());
            }
            System.out.println();
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
