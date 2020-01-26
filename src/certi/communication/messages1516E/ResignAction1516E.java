package certi.communication.messages1516E;

import hla.rti1516e.ResignAction;
import hla.rti1516e.exceptions.InvalidResignAction;

public class ResignAction1516E {
    
    public static short value(ResignAction resignAction) throws InvalidResignAction
    {
        switch( resignAction )
        {
            case UNCONDITIONALLY_DIVEST_ATTRIBUTES:
                return 1;
            case DELETE_OBJECTS:
                return 2;
            case CANCEL_PENDING_OWNERSHIP_ACQUISITIONS:
                return 3;
            case DELETE_OBJECTS_THEN_DIVEST:
                return 4;
            case CANCEL_THEN_DELETE_THEN_DIVEST:
                return 5;
            case NO_ACTION:
                return 6;
            default:
                throw new InvalidResignAction( "Invalid Resing Action: " + resignAction );
        }
    }
}

