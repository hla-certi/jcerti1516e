package integrationTests.TAR;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiLogicalTimeInterval1516E;
import hla.rti1516e.*;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.DecoderException;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.impl.CertiAttributeHandleSet;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.HLAASCIIstringImpl;
import hla.rti1516e.jlc.NullFederateAmbassador;

import java.util.Set;

/**
 * FederateAmbassador specific to receive values to a federation
 * and check if the values are expected values
 */
public class ReceiverFederateAmbassador extends NullFederateAmbassador {

        public LogicalTime localHlaTime;
        public LogicalTimeInterval lookahead;
        public LogicalTime  timeStep;
        public LogicalTime  timeAdvance;
        public LogicalTime updateTime;
        public double updateTime1;
        public boolean timeConstrained;
        public boolean timeAdvanceGranted;
        public boolean timeRegulator;
        public boolean synchronizationSuccess;
        public boolean synchronizationFailed;
        public boolean inPause;
        public boolean isCreator;
        public String synchronizationPointName = "InitSync";
        public RTIambassador rtia;
        public AttributeHandleSet attributes;

        public ObjectInstanceHandle myObject;
        public AttributeHandle textAttributeHandle;
        public AttributeHandle fomAttributeHandle;

        private double expectedTime;
        private double expectedFloat;
        private String expectedString;

        /**
         * Initialization of all attributs, publish and register objects
         * Enable time regulation
         * @param rtia : RTI ambassador
         */
        public void initialize(RTIambassador rtia, double timeStepArg, double updateTimeArg, double lookaheadArg)
                throws NameNotFound,
                FederateNotExecutionMember,
                RTIinternalError,
                ObjectClassNotDefined,
                AttributeNotDefined,
                OwnershipAcquisitionPending,
                SaveInProgress,
                RestoreInProgress,
                //ConcurrentAccessAttempted, 
                ObjectClassNotPublished,
                NotConnected,
                InvalidObjectClassHandle,
                ObjectInstanceNameInUse,
                ObjectInstanceNameNotReserved
        //ObjectAlreadyRegistered 
        {
            this.rtia = rtia;
            try {
                rtia.enableAsynchronousDelivery();
            } catch (AsynchronousDeliveryAlreadyEnabled asynchronousDeliveryAlreadyEnabled) {
                asynchronousDeliveryAlreadyEnabled.printStackTrace();
            }

            ObjectClassHandle classHandle = rtia.getObjectClassHandle("SampleClass");

            textAttributeHandle = rtia.getAttributeHandle(classHandle, "TextAttribute");
            fomAttributeHandle = rtia.getAttributeHandle(classHandle, "FOMAttribute");

            attributes = new CertiAttributeHandleSet();
            attributes.add(textAttributeHandle);
            attributes.add(fomAttributeHandle);

            rtia.subscribeObjectClassAttributes(classHandle, attributes);

            localHlaTime = new CertiLogicalTime1516E(0.0);
            lookahead = new CertiLogicalTimeInterval1516E(lookaheadArg);

            updateTime1 = updateTimeArg;
            timeStep = new CertiLogicalTime1516E(timeStepArg);
            updateTime = new CertiLogicalTime1516E(updateTimeArg);


            // The time is advanced by adding localHlaTime + timeStep; starts with (0.0+timeStepArg)            
            //timeAdvance = new CertiLogicalTime1516E(timeStepArg);
            timeAdvance = new CertiLogicalTime1516E(((CertiLogicalTime1516E) timeStep).getTime());

            timeAdvanceGranted = false;

            try {
                rtia.enableTimeRegulation(lookahead);
            }  catch (Exception e) {
                e.printStackTrace();
            }
            try {
                rtia.enableTimeConstrained();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        /**
         * The Announce Synchronization Point † service shall inform a joined federate of the existence of a new
         * synchronization point label. When a synchronization point label has been registered with the Register
         * Federation Synchronization Point service, the RTI shall invoke the Announce Synchronization Point †
         * service, either at all the joined federates in the federation execution or at the specified set of joined federates,
         * to inform them of the label existence. The joined federates informed of the existence of a given
         * synchronization point label via the Announce Synchronization Point † service shall form the synchronization
         * set for that point. If the optional set of joined federate designators was null or not provided when the
         * synchronization point label was registered, the RTI shall also invoke the Announce Synchronization Point †
         * service at all federates that join the federation execution after the synchronization label was registered but
         * before the RTI has ascertained that all joined federates that were informed of the synchronization label
         * existence have invoked the Synchronization Point Achieved service. These newly joining federates shall also
         * become part of the synchronization set for that point. Joined federates that resign from the federation
         * execution after the announcement of a synchronization point but before the federation synchronizes at that
         * point shall be removed from the synchronization set. The user-supplied tag supplied by the Announce
         * Synchronization Point † service shall be the tag that was supplied to the corresponding Register Federation
         * Synchronization Point service invocation.
         * @param synchronizationPointLabel : Synchronization point label.
         * @param userSuppliedTag : User-supplied tag.
         */
        @Override
        public void announceSynchronizationPoint(
                String synchronizationPointLabel, byte[] userSuppliedTag)
                throws FederateInternalError {
            inPause = true;
        }

        /**
         * The Federation Synchronized † service shall inform the joined federate that all joined federates in the
         * synchronization set of the specified synchronization point have invoked the Synchronization Point Achieved
         * service for that point. This service shall be invoked at all joined federates that are in the synchronization set
         * for that point to indicate that the joined federates in the synchronization set have synchronized at that point.
         * Once the synchronization set for a point synchronizes (i.e., the Federation Synchronized † service has been
         * invoked at all joined federates in the set), that point shall no longer be registered, and the synchronization set
         * for that point shall no longer exist. The set of joined federate designators indicates which federates in the
         * synchronization set invoked the Synchronization Point Achieved service with an indication of an
         * unsuccessful synchronization (via the synchronization-success indicator).
         * @param synchronizationPointLabel : Synchronization point label.
         * @param failedToSyncSet : Set of joined federate designators.
         */
        @Override
        public void federationSynchronized(String synchronizationPointLabel, FederateHandleSet failedToSyncSet)
                throws FederateInternalError {
            inPause = false;
        }

        /**
         * The Confirm Synchronization Point Registration † service shall indicate to the requesting joined federate the
         * status of a requested federation synchronization point registration. This service shall be invoked in response
         * to a Register Federation Synchronization Point service invocation. A registration-success indicator
         * argument indicating success shall mean the label has been successfully registered.
         * If the registration-success indicator argument indicates failure, the failure reason argument shall be provided
         * to identify the reason that the synchronization point registration failed. Possible reasons for the
         * synchronization point registration failure are the following:
         * — The specified label is already in use.
         * — A synchronization set member is not a joined federate.
         * A registration attempt that ends with a negative success indicator shall have no other effect on the federation
         * execution.
         * @param synchronizationPointLabel : Synchronization point label.
         */
        @Override
        public void synchronizationPointRegistrationSucceeded(String synchronizationPointLabel)
                throws FederateInternalError {
            synchronizationSuccess = true;
        }

        /**
         * The Confirm Synchronization Point Registration † service shall indicate to the requesting joined federate the
         * status of a requested federation synchronization point registration. This service shall be invoked in response
         * to a Register Federation Synchronization Point service invocation. A registration-success indicator
         * argument indicating success shall mean the label has been successfully registered.
         * If the registration-success indicator argument indicates failure, the failure reason argument shall be provided
         * to identify the reason that the synchronization point registration failed. Possible reasons for the
         * synchronization point registration failure are the following:
         * — The specified label is already in use.
         * — A synchronization set member is not a joined federate.
         * A registration attempt that ends with a negative success indicator shall have no other effect on the federation
         * execution.
         * @param synchronizationPointLabel : Synchronization point label.
         * @param reason : Optional failure reason.
         */
        @Override
        public void synchronizationPointRegistrationFailed(String synchronizationPointLabel,
                                                           SynchronizationPointFailureReason reason) throws FederateInternalError {
            synchronizationFailed = true;
        }

        /**
         * The Discover Object Instance † service shall inform the joined federate to discover an object instance.
         * Object instance discovery is described in 6.1. The object instance handle shall be unique to the federation
         * execution and shall be uniform (see 6.8) throughout the federation execution.
         * If the Convey Producing Federate Switch for this joined federate is enabled, the producing joined federate
         * argument shall contain the designator for the joined federate that registered the object instance. This
         * producing joined federate may not own instance attributes that caused the discovery, and, in fact, it may be
         * no longer joined to the federation execution.
         * @param theObject : Object instance handle.
         * @param theObjectClass : Object class designator.
         * @param objectName : Object instance name.
         * @throws FederateInternalError
         */
        @Override
        public void discoverObjectInstance(ObjectInstanceHandle theObject, ObjectClassHandle theObjectClass, String objectName)
                throws FederateInternalError
        {
            try {
                SendandReceiveValues_TAR.LOGGER.info("Discover: " + objectName);
                rtia.requestAttributeValueUpdate(theObject, attributes, null);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        @Override
        // FIXME: use a reflectAttributeValues with timestamp, and print the value of the timestamp
        public void reflectAttributeValues(ObjectInstanceHandle theObject,
                                           AttributeHandleValueMap theAttributes,
                                           byte[] userSuppliedTag,
                                           OrderType sentOrdering,
                                           TransportationTypeHandle theTransport,
                                           LogicalTime theTime,
                                           OrderType receivedOrdering,
                                           SupplementalReflectInfo reflectInfo)
                throws FederateInternalError
        {
            try {
                Set<AttributeHandle> attributeHandleSet = theAttributes.keySet();
                SendandReceiveValues_TAR.LOGGER.info(" RAV with time= " + ((CertiLogicalTime1516E)theTime).getTime());
                for(AttributeHandle attributeHandle : attributeHandleSet){
                    if(attributeHandle.hashCode() == textAttributeHandle.hashCode()){
                        HLAASCIIstring value = new HLAASCIIstringImpl();
                        ByteWrapper bw = theAttributes.getValueReference(attributeHandle);
                        value.decode(bw);
                        SendandReceiveValues_TAR.LOGGER.info("     --> Attribute text : " + value.getValue());
                        if(!value.getValue().equals(expectedString)){
                            SendandReceiveValues_TAR.LOGGER.severe("******* TEST FAILED - String value is not expected value: received value = "
                                    + value.getValue() + " but expected value was: " + expectedString);
                            throw new FederateInternalError("TEST FAILED, string value is not expected value");
                        }
                        else if(((CertiLogicalTime1516E)theTime).getTime() != expectedTime){
                            SendandReceiveValues_TAR.LOGGER.severe("******* TEST FAILED - RAV Receive at wrong time, expected at  "
                                    + expectedTime + " but received at: " + ((CertiLogicalTime1516E)theTime).getTime());
                            throw new FederateInternalError("TEST FAILED, RAV time wasn't time expected");
                        } else {
                            SendandReceiveValues_TAR.LOGGER.info("******* TEST String PASSED");
                        }
                    }
                    if(attributeHandle.hashCode() == fomAttributeHandle.hashCode()){
                        HLAfloat32BE value = new BasicHLAfloat32BEImpl();
                        ByteWrapper bw = theAttributes.getValueReference(attributeHandle);
                        value.decode(bw);
                        SendandReceiveValues_TAR.LOGGER.info("     --> Attribute fom : " + value.getValue());
                        if(value.getValue() != expectedFloat){
                            SendandReceiveValues_TAR.LOGGER.severe("******* TEST FAILED - Float value is not expected value: received value = "
                                    + value.getValue() + " but expected value was: " + expectedFloat);
                            throw new FederateInternalError("TEST FAILED, float value is not expected value");
                        }
                        else if(((CertiLogicalTime1516E)theTime).getTime() != expectedTime){
                            SendandReceiveValues_TAR.LOGGER.severe("******* TEST FAILED - RAV Receive at wrong time, expected at  "
                                    + expectedTime + " but received at: " + ((CertiLogicalTime1516E)theTime).getTime());
                            throw new FederateInternalError("TEST FAILED, RAV time wasn't time expected");
                        } else {
                            SendandReceiveValues_TAR.LOGGER.info("******* TEST Float PASSED");
                        }

                    }
                }
            } catch (DecoderException e) {
                e.printStackTrace();
            }
        }

        /**
         * Invocation of the Time Advance Grant † service shall indicate that a prior request to advance the joined
         * federate’s logical time has been honored. The argument of this service shall indicate that the logical time for
         * the joined federate has been advanced to this value.
         * If the grant is issued in response to invocation of a Next Message Request or Time Advance Request service,
         * the RTI shall guarantee that no additional TSO messages shall be delivered in the future with timestamps
         * less than or equal to this value.
         * If the grant is in response to an invocation of a Time Advance Request Available, Next Message Request
         * Available, or Flush Queue Request service, the RTI shall guarantee that no additional TSO messages shall be
         * delivered in the future with timestamps less than the value of the grant.
         * @param theTime : Logical time.
         */
        @Override
        public void timeAdvanceGrant(LogicalTime theTime) throws FederateInternalError {
            localHlaTime = new CertiLogicalTime1516E(((CertiLogicalTime1516E) theTime).getTime());
            timeAdvance = new CertiLogicalTime1516E(((CertiLogicalTime1516E) localHlaTime).getTime()
                    + ((CertiLogicalTime1516E) timeStep).getTime());
            updateTime = new CertiLogicalTime1516E(((CertiLogicalTime1516E) localHlaTime).getTime() + updateTime1);
            SendandReceiveValues_TAR.LOGGER.info("Receiver .........6.2 TAG with time=" + ((CertiLogicalTime1516E) theTime).getTime());
            timeAdvanceGranted = true;
        }

        /**
         * The Provide Attribute Value Update † service requests the current values for attributes owned by the joined
         * federate for a given object instance. The owning joined federate responds to the Provide Attribute Value
         * Update † service with an invocation of the Update Attribute Values service to provide the requested instance
         * attribute values to the federation. The owning joined federate is not required to provide the values.
         * The user-supplied tag argument supplied to the corresponding Request Attribute Value Update service
         * invocation shall be provided with all corresponding Provide Attribute Value Update † service invocations.
         * @param theObject : Object instance designator.
         * @param theAttributes : Set of attribute designators.
         * @param userSuppliedTag : User-supplied tag.
         */
        @Override
        public void provideAttributeValueUpdate(ObjectInstanceHandle theObject, AttributeHandleSet theAttributes,
                                                byte[] userSuppliedTag) throws FederateInternalError {
            SendandReceiveValues_TAR.LOGGER.info("Object handle : " + theObject);
            SendandReceiveValues_TAR.LOGGER.info("Attributes : ");
            for (AttributeHandle attributeHandle : theAttributes) {
                SendandReceiveValues_TAR.LOGGER.info(attributeHandle.toString());
            }
        }

    /**
     * Function to set the expected values, called by the Receiver
     * @param expectedFloat : float value expected to received by the RAV
     * @param expectedString : String value expected to received by the RAV
     * @param expectedTime : time we expect for the RAV
     */
        public void setExpectedValues(double expectedTime, float expectedFloat, String expectedString) {
            this.expectedTime = expectedTime;
            this.expectedFloat = expectedFloat;
            this.expectedString = expectedString;
    }
}
