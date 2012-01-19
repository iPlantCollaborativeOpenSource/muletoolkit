package org.iplantc.muletoolkit.transformers;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;
import org.mule.transport.NullPayload;

public class StringListToLongListTransformer extends AbstractTransformer {

	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		if (arg0 == null || arg0 instanceof NullPayload) {
			return arg0;
		}
		if (!(arg0 instanceof List<?>)) {
			throw new TransformerException(MessageFactory.createStaticMessage("Payload was not a list"));
		}
		List<Long> longList = new ArrayList<Long>();
		for (Object obj : (List<?>)arg0) {
			longList.add(Long.valueOf((String)obj));
		}
		return longList;
	}

}
