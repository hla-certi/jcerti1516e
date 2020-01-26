package InteractiveFederate1516e;

import hla.rti1516e.exceptions.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class interactiveTests {
    private final static Logger LOGGER = Logger.getLogger(FederateInteractive.class.getName());

    public static void main(String[] args) throws IOException, RTIinternalError, NameNotFound,
            FederateNotExecutionMember, ObjectClassNotDefined, AttributeNotDefined, OwnershipAcquisitionPending,
            SaveInProgress, RestoreInProgress, ObjectClassNotPublished, NotConnected, InvalidObjectClassHandle,
            ObjectInstanceNameInUse, ObjectInstanceNameNotReserved, ConnectionFailed, InvalidLocalSettingsDesignator,
            UnsupportedCallbackModel, AlreadyConnected, CallNotAllowedFromWithinCallback, InvalidResignAction, FederateAlreadyExecutionMember, CouldNotOpenFDD, FederationExecutionDoesNotExist, CouldNotCreateLogicalTimeFactory, FederateNameAlreadyInUse {
        FederateInteractive federate = new FederateInteractive("fed1", true);

        Scanner answer = new Scanner(System.in);
        char c = 0;
        String command = "";
        for (String s : args) {
            LOGGER.info(s);
        }
        if (args.length > 0) {
            String federate_name = String.valueOf(args[0]);
            System.out.println(federate_name);
        } else {
            System.out.println("You need to give the name of the federate.");
            System.exit(0); //exit(0) stops the Java Machine
        }

        // Check if the user wants to use the preamble
        boolean badChoice = true;
        while (badChoice) {
            System.out.println("Do you want an automatic preamble (create federation, etc) [y/n]?");
            c = answer.nextLine().charAt(0); // String command =  com.nextLine(); ou  int a=S.nextInt();
            if (c == 'y' || c == 'n') {
                badChoice = false;
            } else {
                System.out.println("You must enter y or n");
            }
        }
        if (c == 'y') {
            System.out.println("I said Yes.");
        } else {
            System.out.println("I said No.");
        }

        // FederativeInteractive = new FederativeInteractive
        //TODO: build your main loop for a federate instance
        while (true) {
            System.out.print("Choose a service (type 'h' for help)");
            command = answer.nextLine();
            System.out.println(command);
            if (command.equalsIgnoreCase("q") || command.equalsIgnoreCase("quit")) {
                break; //System.exit(0);  
            } else if (command.equalsIgnoreCase("h") || command.equalsIgnoreCase("help")) {
                federate.printMenu();
            } else if (command.equalsIgnoreCase("t")) {
                System.out.println("callTICK");

            } else if (command.equalsIgnoreCase("cfe")) {
                System.out.println("callCFE");
            } else if (command.equalsIgnoreCase("rfe")) {
                System.out.println("callRFE");
                //       federate.resignFederationExecution();
            } else if (command.equalsIgnoreCase("roi")) {
                System.out.println("callROI");
            } else if (command.equalsIgnoreCase("uav")) {
                System.out.println("callUAV");
                //federate.updateAttributeValues();
            } else if (command.equalsIgnoreCase("tar")) {
                System.out.println("callTAR");
                //federate.timeAdvanceRequest();
            } else if (command.equalsIgnoreCase("nmr")) {
                System.out.println("callNMR");
                //federate.nextMessageRequest();
                // insert all other HLA services
            } else {
                System.out.println("Unknown command");
            }
        }
        answer.close();
    }

}
