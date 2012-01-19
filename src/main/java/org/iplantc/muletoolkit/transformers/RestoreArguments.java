package org.iplantc.muletoolkit.transformers;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import edu.emory.mathcs.backport.java.util.Arrays;

public class RestoreArguments extends AbstractTransformer {

	private List<String> restoreTemplate;
	private ArgumentStorage argumentStorage;
	
	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		if (!(arg0 instanceof Object[])) {
			arg0 = new Object[] {arg0};
		}
		Object[] args = (Object[])arg0;
		List<Object> argList = new ArrayList<Object>(Arrays.asList(args));
		List<Object> savedList = new ArrayList<Object>(Arrays.asList(argumentStorage.restore()));
		List<Object> retList = new ArrayList<Object>();
		
		for (String template : restoreTemplate) {
			int index = Integer.valueOf(template.substring(1));
			if (template.charAt(0) == 'O') {
				retList.add(argList.get(index));
			} else if (template.charAt(0) == 'S') {
				retList.add(savedList.get(index));
			}
		}
		return retList.toArray();
	}

	public void setRestoreTemplate(List<String> restoreTemplate) {
		this.restoreTemplate = restoreTemplate;
	}

	public List<String> getRestoreTemplate() {
		return restoreTemplate;
	}

	public void setArgumentStorage(ArgumentStorage argumentStorage) {
		this.argumentStorage = argumentStorage;
	}

	public ArgumentStorage getArgumentStorage() {
		return argumentStorage;
	}

}
