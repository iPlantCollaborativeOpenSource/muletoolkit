package org.iplantc.muletoolkit.transformers;


/**
 * This transformer takes a String payload and removes the "%20" string that the
 * http client adds when any item in the request address contains an space, and  
 * re-sets it to an actual sapce.
 * 
 * @author Juan Antonio Raygoza Garay
 * 
 */

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

public class FormatStringWithSpaces extends  AbstractTransformer {
	
	@Override
	protected Object doTransform(Object src, String encoding)
			throws TransformerException {
		String withSpaces = (String) src;
	
		
		return withSpaces.replaceAll("%20", " ");
	}

}
