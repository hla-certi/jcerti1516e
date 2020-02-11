package IntegrationTestsJunit.NMRstringFDD;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.exceptions.RTIinternalError;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendandReceiveValues_NMR {

    //Set expected values in test
    private static final double BLOCKING_TIME = 0.1;
    private final float expectedFloat = 21;
    private final String expectedString = "Test";
    private final LogicalTime updateTimeNMRSender = new CertiLogicalTime1516E(0.2);

    public static AtomicBoolean syncroPoint = new AtomicBoolean(false);

    /**
     * NMR test
     * Create 2 thread, one to create a Sender and this other one a Receiver
     * Sender will send value
     * Receiver receive this value and check if time and value are corrects
     */
    @Test
    public void test() {

//        RTIExecutor rtiExecutor = new RTIExecutor();
//        try {
//            rtiExecutor.executeRTIG();
//        } catch (RTIinternalError rtIinternalError) {
//            rtIinternalError.printStackTrace();
//        }
        try {
            Sender sender = new Sender();
            Receiver receiver = new Receiver();

            //Creation of thread for the Sender
            Callable<Boolean> callableSender = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return sender.send(expectedFloat, expectedString, updateTimeNMRSender);
                }
            };
            FutureTask<Boolean> ftSender = new FutureTask<>(callableSender);

            //Creation of thread for the Receiver
            Callable<Boolean> callableReceiver = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    //Send updateTimeNMRSender value to receiver because it use for the UAV
                    //And we need to check RAV time to validate the test
                    return receiver.receive(expectedFloat, expectedString, updateTimeNMRSender);
                }
            };
            FutureTask<Boolean> ftReceiver = new FutureTask<>(callableReceiver);

            //Execution of the two threads in the same time
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(ftSender);
            executor.execute(ftReceiver);

            while (true) {
                if(ftSender.isDone() && ftReceiver.isDone()){
                    System.out.println("Done");
                    Assert.assertTrue(ftSender.get()||ftReceiver.get());
                    //shut down executor service
                    executor.shutdown();
                    return;
                }
                else if(ftReceiver.isDone() && !ftSender.isDone()) {
                    // If only receiver stopped (because of an error)
                    // We also kill Sender
                    if(ftReceiver.get() == false){
                        Assert.fail();
                        executor.shutdown();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            //Disconnect and destroy federation and RTIG
    //        rtiExecutor.killRTIG();
    }
}
}

