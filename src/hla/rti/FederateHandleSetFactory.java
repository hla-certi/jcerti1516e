
package hla.rti;

/**
 * 
 * 
 */
public interface FederateHandleSetFactory {

/**
 * 
 * @return hla.rti.FederateHandleSet newly created
 * @exception hla.rti.FederationExecutionDoesNotExist if called before FED read
 */
public FederateHandleSet create()
throws FederationExecutionDoesNotExist;	
}