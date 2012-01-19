package org.iplantc.muletoolkit.transformers;

import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;

/**
 * Converts a collection of strings to a JSON object containing a single array property with a configurable name.
 * 
 * @author Dennis Roberts
 */
public class CollectionToJSONArray extends AbstractTransformer {

    /**
     * The name to assign to JSON arrays that we generate.
     */
    private String arrayName;
    
    /**
     * Sets the name to assign to JSON arrays that we generate.
     * 
     * @param arrayName the name.
     */
    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doTransform(Object src, String encoding) throws TransformerException {
        try {
            Collection<?> collection = (Collection<?>) src;
            JSONArray jsonArray = new JSONArray();
            jsonArray.addAll(collection);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(arrayName, jsonArray);
            return jsonObject.toString();
        }
        catch (ClassCastException e) {
            Message msg = MessageFactory.createStaticMessage("unsupporrted source object type");
            throw new TransformerException(msg, e);
        }
        catch (JSONException e) {
            Message msg = MessageFactory.createStaticMessage("unable to build JSON array or object");
            throw new TransformerException(msg, e);
        }
    }
}
