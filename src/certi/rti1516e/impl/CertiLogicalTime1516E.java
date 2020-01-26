package certi.rti1516e.impl;


import hla.rti1516e.LogicalTime;
import hla.rti1516e.LogicalTimeInterval;
import hla.rti1516e.exceptions.CouldNotEncode;
import hla.rti1516e.exceptions.IllegalTimeArithmetic;
import hla.rti1516e.exceptions.InvalidLogicalTime;
import hla.rti1516e.exceptions.InvalidLogicalTimeInterval;

public class CertiLogicalTime1516E implements LogicalTime {

     private double time;
     public static final CertiLogicalTime1516E INITIAL = new CertiLogicalTime1516E(0);
     public static final CertiLogicalTime1516E FINAL = new CertiLogicalTime1516E(Double.MAX_VALUE);


     public CertiLogicalTime1516E(double time) {
         this.time = time;
     }

    @Override
    public boolean equals(Object logicalTime){
         if(logicalTime == null)
             return false;
         if(this.getClass() != logicalTime.getClass())
             return false;

         CertiLogicalTime1516E t = (CertiLogicalTime1516E) logicalTime;
         return this.getTime() == t.getTime();
    }

    /**
     * Getter
     * @return time value in double
     */
    public double getTime(){
         return this.time;
    }

    /**
     * Setter
     * @param time : time value to set, in double
     */
    public void setTime(double time){
         this.time = time;
    }

    /**
     * Returns true is this time is equal to the initial time.
     * @return true if initial value.
     */
    @Override
    public boolean isInitial() {
        return this.equals(INITIAL);
    }

    /**
     * Returns true is this time is equal to the final time.
     * @return true if final value.
     */
    @Override
    public boolean isFinal() {
        return this.equals(FINAL);
    }

    /**
     * Returns a LogicalTime whose value is (this + val). The returned value shall
     * be different from this value if the specified interval != 0.
     * @param val interval to add.
     * @return new time value.
     * @throws IllegalTimeArithmetic
     * @throws InvalidLogicalTimeInterval
     */
    @Override
    public LogicalTime add(LogicalTimeInterval val) throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
        if(val instanceof CertiLogicalTimeInterval1516E){
            double i = ((CertiLogicalTimeInterval1516E) val).getInterval();
            return new CertiLogicalTime1516E(this.time+i) ;
        } else {
            throw new InvalidLogicalTimeInterval("Value is not a CertiLogicalTimeInterval1516E");
        }
    }

    /**
     * Returns a LogicalTime whose value is (this - val). The returned value shall
     * be different from this value if the specified interval != 0.
     * @param val interval to subtract.
     * @return new time value.
     * @throws IllegalTimeArithmetic
     * @throws InvalidLogicalTimeInterval
     */
    @Override
    public LogicalTime subtract(LogicalTimeInterval val) throws IllegalTimeArithmetic, InvalidLogicalTimeInterval {
        if(val instanceof CertiLogicalTimeInterval1516E){
            double i = ((CertiLogicalTimeInterval1516E) val).getInterval();
            return new CertiLogicalTime1516E(this.time - i) ;
        } else {
            throw new InvalidLogicalTimeInterval("Value is not a CertiLogicalTimeInterval1516E");
        }
    }

    /**
     * Returns a LogicalTimeInterval whose value is the time interval between
     * this and val.
     * @param val other time.
     * @return distance between times.
     * @throws InvalidLogicalTime
     */
    @Override
    public LogicalTimeInterval distance(LogicalTime val) throws InvalidLogicalTime {
        if(val instanceof CertiLogicalTime1516E){
            double t = ((CertiLogicalTime1516E) val).getTime();
            return new CertiLogicalTimeInterval1516E(Math.abs(this.time - t)) ;
        } else {
            throw new InvalidLogicalTime("Value is not a CertiLogicalTime1516E");
        }
    }



    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.<p>
     * @param other the Object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    public int compareTo(LogicalTime other) {
        return Double.compare(time, ((CertiLogicalTime1516E)other).getTime());
    }

    public int compareTo(Object other) {
        return Double.compare(time, ((CertiLogicalTime1516E)other).getTime());
    }

    @Override
    public int encodedLength() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void encode(byte[] buffer, int offset) throws CouldNotEncode {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
