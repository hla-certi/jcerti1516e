package integrationTests.StringFddName.TAR;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import certi.rti1516e.impl.CertiLogicalTime1516E;
import certi.rti1516e.impl.CertiRtiAmbassador;
import certi.rti1516e.impl.RTIExecutor;
import hla.rti1516e.LogicalTime;
import hla.rti1516e.exceptions.RTIinternalError;

public class SendandReceiveValues_TAR {

	public final static Logger LOGGER = Logger.getLogger(SendandReceiveValues_TAR.class.getName());

	// Set expected values in test
	private static final float expectedFloat = 21;
	private static final String expectedString = "Test";
	/**
	 * updateTimeTARSender is used to set update time of the sender, but also to
	 * make the UAV, so we have to set it here to check test result in the sender
	 */
	private static final LogicalTime updateTimeTARSender = new CertiLogicalTime1516E(5.3);

	CertiRtiAmbassador rtia_send;
	SenderFederateAmbassador mya_send;
	boolean senderIsCreator;

	public static AtomicBoolean syncroPoint = new AtomicBoolean(false);

	/**
	 * TAR test Create 2 thread, one to create a Sender and this other one a
	 * Receiver Sender will send value Receiver receive this value and check if time
	 * and value are corrects
	 */
	public static void main(String[] args) {
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
				if (ftSender.isDone() && ftReceiver.isDone()) {
					if (ftSender.get() && ftReceiver.get()) {
						// shut down executor service
						executor.shutdown();
						LOGGER.info("*********** TEST SUCCEED ************");
						return;
					}
				} else if (ftReceiver.isDone() && !ftSender.isDone()) {// If only receiver stopped
					if (ftReceiver.get() == false) {
						LOGGER.severe("*********** TEST FAILED ************");
						executor.shutdown();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.severe("*********** TEST FAILED ************");
		} finally {
			rtiExecutor.killRTIG();
		}
	}

}
