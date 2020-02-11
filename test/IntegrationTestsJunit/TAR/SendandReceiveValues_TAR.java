package IntegrationTestsJunit.TAR;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.*;
import hla.rti1516e.encoding.ByteWrapper;
import hla.rti1516e.encoding.HLAASCIIstring;
import hla.rti1516e.encoding.HLAfloat32BE;
import hla.rti1516e.exceptions.*;
import hla.rti1516e.jlc.BasicHLAfloat32BEImpl;
import hla.rti1516e.jlc.HLAASCIIstringImpl;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class SendandReceiveValues_TAR {

    //Set expected values in test
    private static final double BLOCKING_TIME = 0.1;
    private final float expectedFloat = 21;
    private final String expectedString = "Test";
    /**
     * updateTimeTARSender is used to set update time of the sender, but also to make the UAV,
     * so we have to set it here to check test result in the sender
     */
    private LogicalTime updateTimeTARSender = new CertiLogicalTime1516E(5.3);

    RTIambassador rtia_send;
    SenderFederateAmbassador mya_send;
    boolean senderIsCreator;

    public static AtomicBoolean syncroPoint = new AtomicBoolean(false);

    /**
     * TAR test
     * Create 2 thread, one to create a Sender and this other one a Receiver
     * Sender will send value
     * Receiver receive this value and check if time and value are corrects
     */
    @Test
    public void test() {
        RTIExecutor rtiExecutor = new RTIExecutor();
        try {
            rtiExecutor.executeRTIG();
        } catch (RTIinternalError rtIinternalError) {
            rtIinternalError.printStackTrace();
        }

        try {
            Sender sender = new Sender();
            Receiver receiver = new Receiver();

            Callable<Boolean> callableSender = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return sender.send(expectedFloat, expectedString, updateTimeTARSender);
                }
            };
            FutureTask<Boolean> ftSender = new FutureTask<>(callableSender);

            Callable<Boolean> callableReceiver = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return receiver.receive(expectedFloat, expectedString, updateTimeTARSender);
                }
            };
            FutureTask<Boolean> ftReceiver = new FutureTask<>(callableReceiver);

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
                else if(ftReceiver.isDone() && !ftSender.isDone()) {// If only receiver stopped
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
                rtiExecutor.killRTIG();
            
        }
    }

}
