package org.iplantc.muletoolkit.transformers;

import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class SetHttpStatusOnException extends AbstractMessageAwareTransformer {

	@Override
	public Object transform(MuleMessage arg0, String arg1)
			throws TransformerException {
		if (arg0.getExceptionPayload() != null) {
			arg0.setProperty("http.status", 500);
		}
		return arg0.getPayload();
	}

}
