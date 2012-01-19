package org.iplantc.muletoolkit.transport;

import java.io.Serializable;

public class ErrorPayload implements Serializable {
	private String errorType;
	private String errorMessage;

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	
}
