package org.iplantc.muletoolkit.transformers;

import org.iplantc.muletoolkit.transformers.FormatStringWithSpaces;
import org.mule.api.transformer.TransformerException;

import junit.framework.TestCase;

public class TestFormatStringWithSpaces extends TestCase{

	public void testTransformStringPayload() throws TransformerException{
		
		String payload ="some%20String%20with%20spaces";
		
		FormatStringWithSpaces transformer = new FormatStringWithSpaces();
		
		String result = (String)transformer.transform(payload);
		
		assertEquals("some String with spaces", result);
		
	}
	
}
