package org.iplantc.muletoolkit.transformers;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.iplantc.messages.exception.ServiceException;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * The FormatError transformer transforms an exception into a JSON string for return to
 * a client and it sets the HTTP Status Code to 500 (Internal Server Error). It is to be used
 * with RESTful services when a service throws an exception. For example, a mule configuration
 * might resemble:
 * 
 * <vm:outbound-endpoint path="retrievetrees" responseTransformer-refs="FormatError" />
 * 
 * If other response transformations are needed for normal processing, the AbortOnError transformer
 * must come immediately after FormatError. For example:
 * 
 * <vm:outbound-endpoint path="retrievetrees"
 * responseTransformer-refs="FormatError AbortOnError ExtractTreeInfo SpitOutJsonTreeList" />
 * 
 * The above mule statement can be read as "format any error that has occurred and then abort, else
 * do the tree info extraction and spit out the JSON tree list".
 * 
 * @author Donald A. Barre
 */
public class FormatError extends AbstractMessageAwareTransformer {

    public static final String HTTP_STATUS = "http.status";
    public static final int INTERNAL_SERVER_ERROR = 500;

    /*
     * (non-Javadoc)
     * 
     * @see org.mule.transformer.AbstractMessageAwareTransformer#transform(org.mule.api.MuleMessage, java.lang.String)
     */
    @Override
    public Object transform(MuleMessage message, String encoding) throws TransformerException {
        if (message.getExceptionPayload() != null) {
            Throwable exception = message.getExceptionPayload().getRootException();
            message.setProperty(HTTP_STATUS, INTERNAL_SERVER_ERROR);
            message.setPayload(exceptionToJson(exception));
            message.setExceptionPayload(null);
        }
        return message.getPayload();
    }

    /**
     * Convert an exception to a JSON string.  If the exception happens to be a ServiceException then the service
     * properties are included in the JSON string.
     * 
     * @param exception
     * @return a JSON string encoded with exception information
     */
    private String exceptionToJson(Throwable exception) {
        JSONObject json = new JSONObject();
        json.put("name", exception.getClass().getName());
        json.put("message", exception.getMessage());
        if (exception instanceof ServiceException) {
            json.put("properties", formatServiceProperties((ServiceException) exception));
        }
        return json.toString();
    }

    /**
     * Formats the properties obtained form a service exception.
     * 
     * @param exception the exception.
     * @return the array of properties.
     */
    private Object formatServiceProperties(ServiceException exception) {
        JSONArray array = new JSONArray();
        for (String key : exception.getProperties().keySet()) {
            array.add(formatServiceProperty(key, exception.getProperties().get(key).toString()));
        }
        return array;
    }

    /**
     * Formats a single property obtained from a service exception.
     * 
     * @param key the property name.
     * @param value the property value.
     * @return the formatted property.
     */
    private JSONObject formatServiceProperty(String key, String value) {
        JSONObject json = new JSONObject();
        json.put("key", key);
        json.put("value", value);
        return json;
    }
}
