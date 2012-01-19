package org.iplantc.muletoolkit.transformers;

import org.iplantc.muletoolkit.transport.ErrorPayload;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractMessageAwareTransformer;

public class CaptureException extends AbstractMessageAwareTransformer {

	@Override
	public Object transform(MuleMessage arg0, String arg1)
			throws TransformerException {
		if (arg0.getExceptionPayload() != null) {
			ErrorPayload ep = new ErrorPayload();
			ep.setErrorMessage(arg0.getExceptionPayload().getMessage());
			ep.setErrorType(arg0.getExceptionPayload().getException().getClass().getName());
			return ep;
		}
		return arg0.getPayload();
	}

}
