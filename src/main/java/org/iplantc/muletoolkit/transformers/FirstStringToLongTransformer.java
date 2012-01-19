package org.iplantc.muletoolkit.transformers;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.NullPayload;

public class FirstStringToLongTransformer extends AbstractTransformer {

	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		if (arg0 == null || arg0 instanceof NullPayload) {
			return arg0;
		}
		Object[] argArray = (Object[])arg0;
		argArray[0] = Long.valueOf((String)argArray[0]);
		return argArray;
	}

}
