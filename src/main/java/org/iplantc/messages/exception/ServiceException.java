package org.iplantc.messages.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * All exceptions used by RESTful services are derived from ServiceException.
 * It allows properties to be added that will returned to client.  A client
 * can use the properties when creating an internationalized error message.
 * @author Donald A. Barre
 */
@SuppressWarnings("serial")
public abstract class ServiceException extends RuntimeException {

	private Map<String, Object> properties = new HashMap<String, Object>();

	protected ServiceException() {

	}

	protected ServiceException(String message) {
		super(message);
	}

	public final Map<String, Object> getProperties() {
		return properties;
	}

	protected final void addProperty(String key, Object value) {
		properties.put(key, value);
	}
}
