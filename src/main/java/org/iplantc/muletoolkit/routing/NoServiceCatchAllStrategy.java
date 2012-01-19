package org.iplantc.muletoolkit.routing;

import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.routing.RoutingException;
import org.mule.routing.AbstractCatchAllStrategy;

/**
 * If the given URL doesn't match against any of our RESTful services, we must
 * return an exception to the client.
 * @author Donald A. Barre
 */
public class NoServiceCatchAllStrategy extends AbstractCatchAllStrategy {

	private static final String HTTP_STATUS = "http.status";
	private static final int NOT_FOUND_ERROR = 404;

	/* (non-Javadoc)
	 * @see org.mule.routing.AbstractCatchAllStrategy#doCatchMessage(org.mule.api.MuleMessage, org.mule.api.MuleSession)
	 */
	@Override
	public MuleMessage doCatchMessage(MuleMessage message, MuleSession session) throws RoutingException {
		message.setProperty(HTTP_STATUS, NOT_FOUND_ERROR);
		message.setPayload(getJsonNoServiceException());
		return message;
	}

	/**
	 * Return the exception encoded as a JSON string.  Note that we don't actually generate an
	 * exception.  It just looks that way to the client.
	 * @return the JSON encoded exception indicating that no service was available
	 */
	private String getJsonNoServiceException() {
		return "{\"name\":\"org.iplantc.messages.exception.NoServiceException\"," +
                "\"message\":\"No service available for the given URL.\"}";
	}
}
