package certi.communication.messages1516E;

import certi.communication.MessageBuffer;
import hla.rti1516e.FederateHandle;
import hla.rti1516e.FederateHandleSet;
import hla.rti1516e.ParameterHandle;
import hla.rti1516e.ParameterHandleValueMap;
import hla.rti1516e.impl.CertiFederateHandleSet;
import hla.rti1516e.impl.CertiObjectHandle;
import hla.rti1516e.impl.CertiParameterHandleValueMap;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class MessageBuffer1516E extends MessageBuffer {
    
    public MessageBuffer1516E(InputStream inputStream, OutputStream outputStream) {
        super(inputStream, outputStream);
    }
    
    

   
   /**
    * 
    */
   public void write(FederateHandleSet federates) {
	   this.write(federates.size());
       for (FederateHandle f: federates) {
    	   f.hashCode();
           this.write(f.hashCode());
       }
   }
   
   /**
    * 
    * @return
    */
   public FederateHandleSet readFederateHandleSet1516E() {
	   int size = this.readInt();

	   CertiFederateHandleSet federateHandleSet = new CertiFederateHandleSet();
       for (int i = 0; i < size; i++) {
    	   federateHandleSet.add(new CertiObjectHandle(this.readInt()));
       }
       return federateHandleSet;
   }
   
   /**
    * 
    * @param parameters
    */
   public void write(ParameterHandleValueMap parameters) {
	   this.write(parameters.size());
	   Set<ParameterHandle> keys = parameters.keySet();
       for (ParameterHandle k: keys) {
           this.write(k.hashCode());
           this.write(parameters.get(k));
       }
   }
   
   public ParameterHandleValueMap readParameterHandleValueMap1516E() {
	   int size = this.readInt();

	   CertiParameterHandleValueMap parameterHandleValueMap = new CertiParameterHandleValueMap();
       for (int i = 0; i < size; i++) {
    	  // ParameterHandle hashCode = 0;//new ParameterHandle(this.readInt());
    	   byte[] value = this.readBytes();
    	   //parameterHandleValueMap.put(hashCode, value);
       }
       return parameterHandleValueMap;
   }
}
