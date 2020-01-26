package InteractiveFederate1516e;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import hla.rti1516e.*;
import hla.rti1516e.exceptions.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * This program allows the user to interact with the CERTI RTIG by calling HLA
 * services and waiting the callbacks. The user provides the name of the
 * federate 'nameFederate'. The user can choose to use an 'automatic preamble'
 * that executes the following HLA services: - create (and connect to) a RTI
 * Ambassador; create (and join) the Federation 'nameFederation', -
 * getInteractionClassHandle, getParameterHandle, Get Object Class Handle(s),
 * getAttributeHandle(s), - publishObjectClassAttributes,
 * subscribeObjectClassAttributes, - publishInteractionClass,
 * subscribeInteractionClass, - enableTimeRegulation, enableTimeConstrained When
 * using this preamble, the user can send messages through the RTIG and receive
 * messages if the time advancement is made (using NMR or TAR). FIXME: create an
 * automatic preambule for resigning the federation ?
 * 
 * @author J. Cardoso (based on C++ Interactive_Federate1516-2010.cc from
 *         CERTI).
 *
 */

public class FederateInteractive {

	// private final static Logger LOGGER = Logger.getLogger(FederateInteractive.class.getName());

	private RTIambassador rtia;
	private RtiFactory factory;
	//private String FOMname;  // see loadFedFile, FederateName was changed to FOMname
	private File fom;
	private Scanner keyword = new Scanner(System.in);
	private CertiLogicalTime1516E fedTime;

	private ObjectInstanceHandle TestObjectClassInstanceHandle;

	public FederateInteractive(String name, boolean preamble) 
	        throws RTIinternalError, 
	        CouldNotOpenFDD,
	        IOException,
	            CouldNotCreateLogicalTimeFactory,
	            FederateNameAlreadyInUse,
	            FederationExecutionDoesNotExist,
	            NameNotFound,
	            FederateAlreadyExecutionMember,
	            NameNotFound,
	            FederateNotExecutionMember,
	            ObjectClassNotDefined,
	            AttributeNotDefined,
	        FederateNotExecutionMember, 
	        ObjectClassNotDefined, 
	        AttributeNotDefined, 
	        OwnershipAcquisitionPending,
		SaveInProgress, 
		RestoreInProgress, 
		ObjectClassNotPublished, 
		NotConnected, 
		InvalidObjectClassHandle,
		InvalidResignAction,
		ObjectInstanceNameInUse, 
		ObjectInstanceNameNotReserved, 
		ConnectionFailed, 
		InvalidLocalSettingsDesignator,
		UnsupportedCallbackModel, 
		AlreadyConnected, 
		CallNotAllowedFromWithinCallback {

	        //RtiFactory factory = RtiFactoryFactory.getRtiFactory();
	        factory = RtiFactoryFactory.getRtiFactory();
		rtia = factory.getRtiAmbassador();
		String FOMname = null;
		fom = null;

	}

	    // This method is not clear: is for create a new federation, a new FOM file, or a new federate?
	// after discussion it seems that is a new fom file, but then this method must be changed.
//	public void loadFedFile() throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD,
//			hla.rti1516e.exceptions.FederationExecutionAlreadyExists, NotConnected, RTIinternalError,
//			MalformedURLException {
//		System.out.print("Introduce a new FOM file for the Federation or type 'n' for using "+
	        //Certi-Test-02.xml: ");
//		FOMname = keyword.nextLine();
//		try {
//			fom = new File("Certi-Test-02.xml"); //TODO: change to FederateName ie FOMname
	        // Must be FederationName! Same name as in the C++ code
//			rtia.createFederationExecution("Federation_Interactive", fom.toURI().toURL());
//		} catch (MalformedURLException url) {
//			throw new MalformedURLException();
//		}
//	}

	/*************************************************************************************************
	********************************* FEDERATION MANAGEMENT ******************************************
	*************************************************************************************************/
	
	public void createAmbassador() {
		
	}
	
	/**
	 * RFE
	 * 
	 * @throws InvalidResignAction
	 * @throws OwnershipAcquisitionPending
	 * @throws FederateOwnsAttributes
	 * @throws FederateNotExecutionMember
	 * @throws NotConnected
	 * @throws CallNotAllowedFromWithinCallback
	 * @throws RTIinternalError
	 */
	public void resignFederationExecution()
			throws InvalidResignAction, OwnershipAcquisitionPending, FederateOwnsAttributes, FederateNotExecutionMember,
			NotConnected, CallNotAllowedFromWithinCallback, RTIinternalError {
		rtia.resignFederationExecution(ResignAction.CANCEL_THEN_DELETE_THEN_DIVEST);
	}

	/**
	 * DFE
	 * 
	 * @throws FederatesCurrentlyJoined
	 * @throws FederationExecutionDoesNotExist
	 * @throws NotConnected
	 * @throws RTIinternalError
	 */
	public void deleteFederationExecution()
			throws FederatesCurrentlyJoined, FederationExecutionDoesNotExist, NotConnected, RTIinternalError {
		rtia.destroyFederationExecution("FederateName");
	}

	/**
	 * CFE
	 * 
	 * @throws InconsistentFDD
	 * @throws ErrorReadingFDD
	 * @throws CouldNotOpenFDD
	 * @throws FederationExecutionAlreadyExists
	 * @throws NotConnected
	 * @throws RTIinternalError
	 * @throws MalformedURLException
	 */
	public void createFederationExecution() throws InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD,
			FederationExecutionAlreadyExists, NotConnected, RTIinternalError, MalformedURLException {
		rtia.createFederationExecution("FederateName", fom.toURI().toURL());
	}

	/**
	 * JFE
	 * 
	 * @throws CouldNotCreateLogicalTimeFactory
	 * @throws FederateNameAlreadyInUse
	 * @throws FederationExecutionDoesNotExist
	 * @throws InconsistentFDD
	 * @throws ErrorReadingFDD
	 * @throws CouldNotOpenFDD
	 * @throws SaveInProgress
	 * @throws RestoreInProgress
	 * @throws FederateAlreadyExecutionMember
	 * @throws NotConnected
	 * @throws CallNotAllowedFromWithinCallback
	 * @throws RTIinternalError
	 * @throws MalformedURLException
	 */
	public void joinFederationExecution() throws CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse,
			FederationExecutionDoesNotExist, InconsistentFDD, ErrorReadingFDD, CouldNotOpenFDD, SaveInProgress,
			RestoreInProgress, FederateAlreadyExecutionMember, NotConnected, CallNotAllowedFromWithinCallback,
			RTIinternalError, MalformedURLException {
		URL[] joinModules = new URL[] { fom.toURI().toURL() };
		rtia.joinFederationExecution("FederateName", "FederateName", "FederateName", joinModules);
	}

	/*************************************************************************************************
	********************************* DECLARATION MANAGEMENT *****************************************
	*************************************************************************************************/

	public void publishObjectClassAttribute() {
		
	}
	
	public void unpublishObjectClass() {
		
	}
	
	public void unpublishObjectClassAttribute() {
		
	}
	
	public void publishInteractClass() {
		
	}

	public void unpublishInteractClass() {
		
	}
	
	public void subscribeObjectClassAttributes() {
		
	}
	
	public void unsubscribeObjectClass() {
		
	}
	
	public void unsubscribeObjectClassAttributes() {
		
	}
	
	public void subscribeInteractionClass() {
		
	}
	
	public void unsubscribeInteractionClass() {
		
	}
	
	/*************************************************************************************************
	************************************ OBJECT MANAGEMENT *******************************************
	*************************************************************************************************/
	
	public void registerObjectInstance() {
		
	}
	
	public void registerObjectInstance(String name) {
		
	}
	
	/**
	 * UAV
	 * 
	 * @throws LogicalTimeAlreadyPassed
	 * @throws InvalidLogicalTime
	 * @throws InTimeAdvancingState
	 * @throws RequestForTimeRegulationPending
	 * @throws RequestForTimeConstrainedPending
	 * @throws SaveInProgress
	 * @throws RestoreInProgress
	 * @throws FederateNotExecutionMember
	 * @throws NotConnected
	 * @throws RTIinternalError
	 * @throws AttributeNotOwned
	 * @throws AttributeNotDefined
	 * @throws ObjectInstanceNotKnown
	 */
	public void updateAttributeValues()
			throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState, RequestForTimeRegulationPending,
			RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress, FederateNotExecutionMember,
			NotConnected, RTIinternalError, AttributeNotOwned, AttributeNotDefined, ObjectInstanceNotKnown {
		double d;

		AttributeHandleValueMap attributeValues = null;
//		  AttributeHandleValueMap attributeRValues = null;
		byte[] theUserSuppliedTag;

		System.out.print("Donner la valeur de la donnee (double): ");

		try {
			d = keyword.nextDouble();
			fedTime = new CertiLogicalTime1516E(d);
			rtia.nextMessageRequest(fedTime);
		} catch (InputMismatchException ex) {
			updateAttributeValues();
		}

		theUserSuppliedTag = new byte[] { Byte.parseByte("uav"), Byte.parseByte("4") };
		fedTime = null;
		while (true) {
			System.out.print("Avec gestion du temps [y/n]? ");
			try {
				String input = keyword.nextLine();
				if (!input.equals("y") || !input.equals("n")) {
					throw new InputMismatchException();
				}

				if (input.equals("y")) {
					System.out.print("Donner la valeur de l'estampille voulue: ");
					try {
						d = keyword.nextDouble();
						fedTime = new CertiLogicalTime1516E(d);

					} catch (InputMismatchException ex) {// put something to catch
					}
				}

				rtia.updateAttributeValues(TestObjectClassInstanceHandle, attributeValues, theUserSuppliedTag,
						fedTime == null ? fedTime : new CertiLogicalTime1516E(0.0));
			} catch (InputMismatchException ex) {
				System.out.println("Vous devez entrer 'y' ou 'n'");
			}
		}
	}
	
	
	/*************************************************************************************************
	************************************* TIME MANAGEMENT ********************************************
	*************************************************************************************************/
	
	/**
	 * TAG
	 * 
	 * @throws CallNotAllowedFromWithinCallback
	 * @throws RTIinternalError
	 */
	public void timeAdvanceGrant() throws CallNotAllowedFromWithinCallback, RTIinternalError {
		rtia.evokeCallback(0.1);
	}

	/**
	 * TAR
	 * 
	 * @throws LogicalTimeAlreadyPassed
	 * @throws InvalidLogicalTime
	 * @throws InTimeAdvancingState
	 * @throws RequestForTimeRegulationPending
	 * @throws RequestForTimeConstrainedPending
	 * @throws SaveInProgress
	 * @throws RestoreInProgress
	 * @throws FederateNotExecutionMember
	 * @throws NotConnected
	 * @throws RTIinternalError
	 */
	public void timeAdvanceRequest() throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState,
			RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress,
			FederateNotExecutionMember, NotConnected, RTIinternalError {
		double d;

		System.out.print("Donner la date a laquelle vous souhaitez avancer: ");
		try {
			d = keyword.nextDouble();
			fedTime = new CertiLogicalTime1516E(d);
			rtia.timeAdvanceRequest(fedTime);
		} catch (InputMismatchException ex) {
			timeAdvanceRequest();
		}
	}

	/**
	 * NMR
	 * 
	 * @throws LogicalTimeAlreadyPassed
	 * @throws InvalidLogicalTime
	 * @throws InTimeAdvancingState
	 * @throws RequestForTimeRegulationPending
	 * @throws RequestForTimeConstrainedPending
	 * @throws SaveInProgress
	 * @throws RestoreInProgress
	 * @throws FederateNotExecutionMember
	 * @throws NotConnected
	 * @throws RTIinternalError
	 */
	public void nextMessageRequest() throws LogicalTimeAlreadyPassed, InvalidLogicalTime, InTimeAdvancingState,
			RequestForTimeRegulationPending, RequestForTimeConstrainedPending, SaveInProgress, RestoreInProgress,
			FederateNotExecutionMember, NotConnected, RTIinternalError {
		double d;

		System.out.print("Donner la date a laquelle vous souhaitez avancer: ");
		try {
			d = keyword.nextDouble();
			fedTime = new CertiLogicalTime1516E(d);
			rtia.nextMessageRequest(fedTime);
		} catch (InputMismatchException ex) {
		    nextMessageRequest();
		}
	}

	/*************************************************************************************************
	********************************** COMMANDES UTILITAIRE ******************************************
	*************************************************************************************************/
	
	/*TICK --> timeAdvanceGrant() */
	
	public void getObjectClassHandle() {
		
	}
	
	public void getObjectClassName() {
		
	}
	
	public void getAttributeHandle() {
		
	}
	
	public void getAttributeHandleName() {
		
	}
	
	public void getInteractionClassHandle() {
		
	}
	
	public void getInteractionClassName() {
		
	}
	
	public void getParameterHandle() {
		
	}
	
	public void getParameterHandleName() {
		
	}
	
	public void getObjectInstanceHandle() {
		
	}
	
	public void getObjectInstanceName() {
		
	}
	
	/* PRINT MENU */
	
	public void printMenu() {
//            System.out.println("1 : Different services \n"
//                    + "2 : Federation Management \n"
//                    + "3 : Declaration Management\n"
//                    + "4 : Object Management\n"
//                    + "5 : Time Management\n"
//                    + "Type your choice:"
//                    );
		System.out.println("1 : Different services\n" + "t       => Evoke Callback (tick)\n"
				+ "getoch  => Get Object Class Handle\n" + "getocn  => Get Object Class Name\n"
				+ "getah   => Get Attribute Handle\n" + "getahn  => Get Attribute Handle Name\n"
				+ "getoih  => Get Object Instance Handle\n"
		// Add remaining cases
		);
		System.out.println("2- Federation Management\n" + "amb => create ambassador \n"
				+ "cfe => create federation execution \n" + "dfe => destroy federation execution \n"
				+ "jfe => join federation execution \n" + "rfe => resign federation execution \n");
		// Add remaining cases
		System.out.println("3- Declaration Management\n" + "poca    => Publish Object Class Attributes\n"
				+ "uoc     => Unpublish Object Class\n" + "uoca    => Unpublish Object Class Attributes\n"
				+ "pic     => Publish Interaction Class\n" + "usuboc  => Unsubscribe Object Class\n"
				+ "usuboca => Unsubscribe Object Class Attributes\n" + "usubic  => Unsubscribe Interaction Class\n"
				+ "sic     => Suscribe Interaction Class\n");
		// Add remaining cases
		System.out.println("4- Object Management\n" + "si  => Send Interaction\n" + "roi => Register Object Instance\n"
				+ "roiwn => Register Object Instance with a name\n" + "uav => Update Attribute Values\n\n");
		System.out
				.println("5- Time Management\n" + "etc => Enable Time Constrained\n" + "etr => Enable Time Regulator\n"
						+ "tar => Time Advance Request\n" + "nmr => Next Message Request\n" + ".. => ...");
	}

}