package org.iplantc.muletoolkit.transformers;

import org.iplantc.muletoolkit.transformers.JSONToString;
import org.mule.api.transformer.TransformerException;

import net.sf.json.JSONObject;
import junit.framework.TestCase;

public class TestJSONToString extends TestCase{

	public void testJSONTToStringTransformer() throws TransformerException{
		
		JSONObject obj = new JSONObject();
		
		obj.put("id", "3");
		
		JSONToString transformer = new JSONToString();
		
		
		String res = (String) transformer.transform(obj); 
		
		assertEquals("{\"id\":\"3\"}", res);
		
	}
	
}
