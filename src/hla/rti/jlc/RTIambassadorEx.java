package hla.rti.jlc;

import hla.rti.*;

public interface RTIambassadorEx extends RTIambassador
{
   public boolean tick( final double min, final double max )
     throws hla.rti.RTIinternalError,
              hla.rti.ConcurrentAccessAttempted;
}
