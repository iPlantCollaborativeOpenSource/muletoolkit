package org.iplantc.muletoolkit.transformers;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.NullPayload;

public class StringToLongTransformer extends AbstractTransformer {

	/*
	 * If a non-numeric string is provided, return a value of -1.  We use StringToLong
	 * to convert string values to an ID.  In the DE, IDs must be positive numbers.  By
	 * using -1, the error can be caught in the Java service code which allows us to throw
	 * a proper exception that will be returned to the client.
	 *
	 * (non-Javadoc)
	 * @see org.mule.transformer.AbstractTransformer#doTransform(java.lang.Object, java.lang.String)
	 */
	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		if (arg0 == null || arg0 instanceof NullPayload) {
			return arg0;
		}
		try {
		    return Long.valueOf((String) arg0);
		}
		catch (NumberFormatException e) {
			return new Long(-1L);
		}
	}
}
