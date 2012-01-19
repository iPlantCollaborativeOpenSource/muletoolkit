package org.iplantc.muletoolkit.filters;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.mule.api.MuleMessage;
import org.mule.api.routing.filter.Filter;

/**
 *  This class implements filtering for a message whose payload is a JSON String.
 * 
 * 	It filters based off of the value of a given property
 * 
 * 
 * @author Juan Antonio Raygoza Garay
 */





public class JSONPropertyFilter  implements Filter{

	
	private String key;
	private String value;
	
	
	
	
	@Override
	public boolean accept(MuleMessage message) {
		
		try {
		JSONObject json = (JSONObject) JSONSerializer.toJSON(message.getPayloadAsString());
		
		if(json.has(key) && json.getString(key).equals(value)) {
			return true;
		
		}
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		return false;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
	
	
}
