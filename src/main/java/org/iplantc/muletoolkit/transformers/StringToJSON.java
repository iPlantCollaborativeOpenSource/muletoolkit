package org.iplantc.muletoolkit.transformers;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;


import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class StringToJSON extends AbstractTransformer{

	
	@Override
	protected Object doTransform(Object src, String encoding)
			throws TransformerException {
	
		JSONObject result=null;
		try{
			result =  (JSONObject)JSONSerializer.toJSON(src);
			
			
		}catch(JSONException ex){
			throw new TransformerException(this, ex);
		}
		
		
		return result;
	}
	
	
}
