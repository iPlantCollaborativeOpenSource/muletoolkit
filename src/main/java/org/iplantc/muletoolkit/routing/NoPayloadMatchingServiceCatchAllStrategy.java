package org.iplantc.muletoolkit.routing;


/**
 *  This strategy is used to return an error message when none of the filtering endpoints
 * match the parameters sent in the request.
 * 
 * 
 * @author Juan Antonio Raygoza Garay
 */

import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.routing.RoutingException;
import org.mule.routing.AbstractCatchAllStrategy;

public class NoPayloadMatchingServiceCatchAllStrategy  extends AbstractCatchAllStrategy{

	@Override
	public MuleMessage doCatchMessage(MuleMessage message, MuleSession session)
			throws RoutingException {
	
		message.setProperty("http.status", 500);
		String error = "{\"name\":\"exception\",\"message\":\"The payload sent does not match any of the available actions\"}";
		message.setPayload(error);
		
	
		
		return message;
	}
	
}
