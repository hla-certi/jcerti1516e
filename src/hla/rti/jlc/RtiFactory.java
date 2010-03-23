package hla.rti.jlc;

import hla.rti.*;

public interface RtiFactory
{
   RTIambassadorEx createRtiAmbassador()
     throws hla.rti.RTIinternalError;

   AttributeHandleSet createAttributeHandleSet();
   FederateHandleSet createFederateHandleSet();
   SuppliedAttributes createSuppliedAttributes();
   SuppliedParameters createSuppliedParameters();

   String RtiName();
   String RtiVersion();

   long getMinExtent();
   long getMaxExtent();
}
