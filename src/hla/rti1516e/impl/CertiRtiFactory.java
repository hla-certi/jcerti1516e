package hla.rti1516e.impl;

import java.util.logging.Logger;

import certi.rti1516e.impl.CertiRtiAmbassador;
import hla.rti1516e.RTIambassador;
import hla.rti1516e.RtiFactory;
import hla.rti1516e.encoding.EncoderFactory;
import hla.rti1516e.exceptions.RTIinternalError;

public class CertiRtiFactory implements RtiFactory{
	private final static Logger LOGGER = Logger.getLogger(CertiRtiFactory.class.getName());
	
	@Override
	public RTIambassador getRtiAmbassador() throws RTIinternalError {
		LOGGER.info("        getRtiAmbassador");
		return new CertiRtiAmbassador();
	}

	@Override
	public EncoderFactory getEncoderFactory() throws RTIinternalError {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rtiName() {
		return "Certi";
	}

	@Override
	public String rtiVersion() {
		return "4.0.0";
	}

}
