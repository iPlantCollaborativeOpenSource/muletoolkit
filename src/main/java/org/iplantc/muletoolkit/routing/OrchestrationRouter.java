/*
 * This Router was copied from Mule's Chaining Router and modified.
 */

package org.iplantc.muletoolkit.routing;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.iplantc.muletoolkit.transformers.FormatError;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleException;
import org.mule.api.MuleMessage;
import org.mule.api.MuleSession;
import org.mule.api.config.MuleProperties;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.routing.CouldNotRouteOutboundMessageException;
import org.mule.api.routing.RoutePathNotFoundException;
import org.mule.api.routing.RoutingException;
import org.mule.config.i18n.CoreMessages;
import org.mule.routing.outbound.FilteringOutboundRouter;
import org.mule.transport.NullPayload;

/**
 * <code>OrchestrationRouter</code> is used to pass a Mule event through multiple
 * endpoints.  The arguments for each endpoint are configurable.
 */

public class OrchestrationRouter extends FilteringOutboundRouter
{
    /* (non-Javadoc)
     * @see org.mule.routing.outbound.FilteringOutboundRouter#initialise()
     */
    @Override
    public void initialise() throws InitialisationException
    {
        super.initialise();
        if (endpoints == null || endpoints.size() == 0)
        {
            throw new InitialisationException(CoreMessages.objectIsNull("endpoints"), this);
        }
    }

    /* (non-Javadoc)
     * @see org.mule.routing.outbound.FilteringOutboundRouter#route(org.mule.api.MuleMessage, org.mule.api.MuleSession)
     */
    public MuleMessage route(MuleMessage message, MuleSession session)
        throws RoutingException
    {
        MuleMessage resultToReturn = null;
        if (endpoints == null || endpoints.size() == 0)
        {
            throw new RoutePathNotFoundException(CoreMessages.noEndpointsForRouter(), message, null);
        }

        final int endpointsCount = endpoints.size();
        if (logger.isDebugEnabled())
        {
            logger.debug("About to chain " + endpointsCount + " endpoints.");
        }

        // Put the initial payload into the payload map.  The payload map
        // contains all of the initial payload and all of the results of returned
        // by the endpoints.  The configuration specifies which values from the
        // payload map are sent to the endpoints.
        Map<String, Object> payloadMap = new HashMap<String, Object>();
        initializePayloadMap(payloadMap, message);
        
        // need that ref for an error message
        OutboundEndpoint endpoint = null;
        try
        {
            MuleMessage intermediaryResult = message;

            for (int i = 0; i < endpointsCount; i++)
            {
                endpoint = getEndpoint(i, intermediaryResult);

                // Set the payload to be set to the this endpoint based
                // upon the configuration.
                String args = (String) endpoint.getProperty("args");
                if (args != null) {
                	setPayload(intermediaryResult, payloadMap, args);
                }

                // if it's not the last endpoint in the chain,
                // enforce the synchronous call, otherwise we lose response
                boolean lastEndpointInChain = (i == endpointsCount - 1);

                if (logger.isDebugEnabled())
                {
                    logger.debug("Sending Chained message '" + i + "': "
                                 + (intermediaryResult == null ? "null" : intermediaryResult.toString()));
                }

                // All endpoints registered on a chaining router need to use RemoteSync enabled. Setting this property now. MULE-3643
                intermediaryResult.setProperty(MuleProperties.MULE_REMOTE_SYNC_PROPERTY, true);

                if (!lastEndpointInChain)
                {
                    MuleMessage localResult = send(session, intermediaryResult, endpoint);
                    // Need to propagate correlation info and replyTo, because there
                    // is no guarantee that an external system will preserve headers
                    // (in fact most will not)
                    if (localResult != null &&
                        // null result can be wrapped in a NullPayload
                        localResult.getPayload() != NullPayload.getInstance() &&
                        intermediaryResult != null)
                    {
                        processIntermediaryResult(localResult, intermediaryResult);
                    }
                    intermediaryResult = localResult;
                    if (isException(localResult)) {
                    	return localResult;  // early exit due to an exception
                    }

                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Received Chain result '" + i + "': "
                                     + (intermediaryResult != null ? intermediaryResult.toString() : "null"));
                    }

                    // Store result from the endpoint
                    payloadMap.put("e" + i, intermediaryResult.getPayload());
                }
                else
                {
                    // for the last endpoint, specify the payload to return to the
                    // inbound endpoint
                	String returnArg = (String) endpoint.getProperty("returnArg");

                    // ok, the last call,
                    // use the 'sync/async' method parameter
                	if (endpoint.isSynchronous())
                    {
                        resultToReturn = send(session, intermediaryResult, endpoint);
                        if (logger.isDebugEnabled())
                        {
                            logger.debug("Received final Chain result '" + i + "': "
                                         + (resultToReturn == null ? "null" : resultToReturn.toString()));
                        }
                        payloadMap.put("e" + i, resultToReturn.getPayload());
                    }
                    else
                    {
                        // if we're going to return a result, copy over the intermediary
                    	// MuleMessage so we can do so
                        if (returnArg != null) {
                        	resultToReturn = intermediaryResult;
                        }
                        dispatch(session, intermediaryResult, endpoint);
                    }

                    if (returnArg != null && !isException(resultToReturn)) {
                    	setPayload(resultToReturn, payloadMap, returnArg);
                    	resultToReturn = new DefaultMuleMessage(resultToReturn.getPayload(), resultToReturn);
                    }
                }
            }

        }
        catch (MuleException e)
        {
            throw new CouldNotRouteOutboundMessageException(message, endpoint, e);
        }
        return resultToReturn;
    }

    /**
     * Is there an exception within the message.  There may be an exception payload
     * or the HTTP STATUS could have been set to INTERNAL_SERVER_ERROR.
     * @param message
     * @return
     */
    private boolean isException(MuleMessage message) {
    	if (message == null) return false;
    	if (message.getExceptionPayload() != null) return true;

    	Object code = message.getProperty(FormatError.HTTP_STATUS);
    	return code == null ? false : code.equals(FormatError.INTERNAL_SERVER_ERROR);
	}

	/**
     * Initialize the payload map with the initial values in the message's payload.
     * All initial payload entries are named "arg#", where "#" is a sequence number
     * starting from zero.
     * @param payloadMap
     * @param message
     */
    private void initializePayloadMap(Map<String, Object> payloadMap, MuleMessage message) {
		Object payload = message.getPayload();
		Object args[] = null;
		if (payload instanceof Object[]) {
			args = (Object[]) payload;
		}
		else {
			args = new Object[1];
			args[0] = payload;
		}
		for (int i = 0; i < args.length; i++) {
			payloadMap.put("arg" + i, args[i]);
		}
	}

	/**
	 * Set the payload for an endpoint.
	 * @param message
	 * @param payloadMap
	 * @param args the encoded list of args specifying the message's payload
	 */
	private void setPayload(MuleMessage message, Map<String, Object> payloadMap, String args) {
		String argNames[] = args.split(",");

		int i = 0;
		Object[] payloadArray = new Object[argNames.length];
		for (String argName : argNames) {
			if (argName.startsWith("Long:")) {
				String s[] = argName.split(":");
				payloadArray[i++]= Long.parseLong(s[1]);
			}
			else if (argName.startsWith("Boolean:")) {
				String s[] = argName.split(":");
				payloadArray[i++]= Boolean.parseBoolean(s[1]);
			}
			else if (argName.startsWith("String:")) {
				String s[] = argName.split(":", 2);
				payloadArray[i++]= s[1];
			}
			else if (argName.startsWith("Bean:")) {
				String s[] = argName.split(":", 2);
				String beanExpr = s[1];
				s = beanExpr.split("\\.", 2);
				logger.debug(s[0]);
				logger.debug(s[1]);
				Object value = payloadMap.get(s[0]);
				logger.debug(value);
				if (value != null) {
					try {
						value = PropertyUtils.getProperty(value, s[1]);
						if (value != null) {
							payloadArray[i++] = value;
						}
					} catch (IllegalAccessException e) {
						throw new IllegalArgumentException(e);
					} catch (InvocationTargetException e) {
						throw new IllegalArgumentException(e);
					} catch (NoSuchMethodException e) {
						throw new IllegalArgumentException(e);
					}
				}
			}
			else if (argName.equals("Message")) {
				payloadArray[i++] = new DefaultMuleMessage(message.getPayload(), message.getAdapter());
			}
			else {
				/*
				 * Apparently Mule is unable to handle null arguments. It fails to
				 * resolve to the necessary method, even though null should be OK.
				 * So, there had better be different methods on the service component
				 * to deal with some argument values not being there since they were null.
				 */
				Object value = payloadMap.get(argName);
				if (value != null) {
					payloadArray[i++] = value;
				}
			}
		}

		if (i < argNames.length) {
			Object[] tmpPayload = new Object[i];
			for (int j = 0; j < i; j++) {
				tmpPayload[j] = payloadArray[j];
			}
			payloadArray = tmpPayload;
		}

		Object payload;
		if (i == 1) {
			payload = payloadArray[0];
		} else {
			payload = payloadArray;
		}
		message.setPayload(payload);
	}

	/**
     * Process intermediary result of invocation. The method will be invoked
     * <strong>only</strong> if both local and intermediary results are available
     * (not null).
     * <p/>
     * Overriding methods must call <code>super(localResult, intermediaryResult)</code>,
     * unless they are modifying the correlation workflow (if you know what that means,
     * you know what you are doing and when to do it).
     * <p/>
     * Default implementation propagates
     * the following properties:
     * <ul>
     * <li>correlationId
     * <li>correlationSequence
     * <li>correlationGroupSize
     * <li>replyTo
     * </ul>
     * @param localResult result of the last endpoint invocation
     * @param intermediaryResult the message travelling across the endpoints
     */
    protected void processIntermediaryResult(MuleMessage localResult, MuleMessage intermediaryResult)
    {
        localResult.setCorrelationId(intermediaryResult.getCorrelationId());
        localResult.setCorrelationSequence(intermediaryResult.getCorrelationSequence());
        localResult.setCorrelationGroupSize(intermediaryResult.getCorrelationGroupSize());
        localResult.setReplyTo(intermediaryResult.getReplyTo());
    }
}
