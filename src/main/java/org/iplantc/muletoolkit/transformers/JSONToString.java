package org.iplantc.muletoolkit.transformers;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;


/**
 * 
 * This tranformer  takes a JSON object and return its
 * String representation
 * 
 * @author Juan Antonio Raygoza Garay
 *
 */



public class JSONToString extends AbstractTransformer{

	 @Override
	protected Object doTransform(Object src, String encoding)
			throws TransformerException {
		 String json_string="";
		 try{
			 json_string = ((JSONObject)src).toString();
			 
		 }catch(JSONException jsex){
			 throw new TransformerException(this, jsex.getCause());
		 }
		return json_string;
	}
	
	
}
