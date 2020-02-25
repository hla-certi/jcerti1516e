
package certi.communication.messages1516E;

import java.net.MalformedURLException;
import java.net.URL;

import certi.communication.CertiException;
import certi.communication.CertiMessageType;
import certi.communication.MessageBuffer;

public class CreateFederationExecution1516E_V2 extends CertiMessage1516E {

	private String federationExecutionName;
	private String fomName;
	private URL mimModule;
	private String logicalTimeImplName;
	private Byte rtiVersion = 3; // 1 => HLA_1_3, 2 => IEEE_1516_2000, 3 => IEEE_1516_2010

	public CreateFederationExecution1516E_V2() {
		super(CertiMessageType.CREATE_FEDERATION_EXECUTION_V4);
		mimModule = null;
		logicalTimeImplName = null;
	}

	@Override
	public void writeMessage(MessageBuffer messageBuffer) {
		super.writeMessage(messageBuffer); // Header

		messageBuffer.write(federationExecutionName);
		messageBuffer.write(rtiVersion);
		messageBuffer.write(1); // fom number (Work only with one federate)
		messageBuffer.write(fomName);

		// Check is the federationExecution has mimModule
		// If not, standard mimModule will be load by the server
		if (this.mimModule != null) {
			messageBuffer.write(true);
			messageBuffer.write(this.mimModule.toString());
		} else {
			messageBuffer.write(false);
		}

		// Check is the federationExecution has logical time representation
		if (this.logicalTimeImplName != null) {
			messageBuffer.write(true);
			messageBuffer.write(this.logicalTimeImplName.toString());
		} else {
			messageBuffer.write(false);
		}
	}

	@Override
	public void readMessage(MessageBuffer messageBuffer) throws CertiException {
		super.readMessage(messageBuffer); // Header
		federationExecutionName = messageBuffer.readString();
		rtiVersion = messageBuffer.readByte();
		messageBuffer.readInt();
		fomName = messageBuffer.readString();

		if (messageBuffer.readBoolean()) {
			try {
				this.mimModule = new URL(messageBuffer.readString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		} else
			this.mimModule = null;

		if (messageBuffer.readBoolean())
			this.logicalTimeImplName = messageBuffer.readString();
		else
			this.logicalTimeImplName = null;
	}

	@Override
	public String toString() {
		String result = super.toString() + ", federationName : " + federationExecutionName + ",";
		result += "fom Name : " + fomName;

		if (this.mimModule != null) {
			result += ", hasMimoModule : true ";
			result += this.mimModule.toString() + ", ";
		} else {
			result += ", hasMimoModule : false ";
		}

		if (this.mimModule != null) {
			result += ", hasLogicalTimeRepresentation : true ";
			result += this.logicalTimeImplName.toString() + ", ";
		} else {
			result += ", hasLogicalTimeRepresentation : false ";
		}

		return result;
	}

	public String getFederationName() {
		return federationExecutionName;
	}

	public void setFederationName(String newFederationName) {
		this.federationExecutionName = newFederationName;
	}

	public String getFomName() {
		return fomName;
	}

	public void setFomName(String fomName) {
		this.fomName = fomName;
	}

	public URL setMimDesignator() {
		return mimModule;
	}

	public void setMimDesignator(URL mimModule) {
		this.mimModule = mimModule;
	}

	public String setLogicalTimeImplName() {
		return logicalTimeImplName;
	}

	public void setLogicalTimeImplName(String logicalTimeImplName) {
		this.logicalTimeImplName = logicalTimeImplName;
	}

}
