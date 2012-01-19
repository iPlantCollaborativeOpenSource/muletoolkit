package org.iplantc.muletoolkit.transformers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import edu.emory.mathcs.backport.java.util.Arrays;

public class SaveArguments extends AbstractTransformer {

	private List<String> saveTemplate;
	private ArgumentStorage argumentStorage;
	
	@Override
	protected Object doTransform(Object arg0, String arg1)
			throws TransformerException {
		Object[] args = (Object[])arg0;
		List<Object> argList = new ArrayList<Object>(Arrays.asList(args));
		List<Object> savedList = new LinkedList<Object>();
		int argIndex = 0;
		
		for (String template : saveTemplate) {
			if ("SAVE".equalsIgnoreCase(template)) {
				savedList.add(argList.remove(argIndex));
			}
			argIndex++;
		}
		argumentStorage.save(savedList.toArray());
		return argList.toArray();
	}

	public void setArgumentStorage(ArgumentStorage argumentStorage) {
		this.argumentStorage = argumentStorage;
	}

	public ArgumentStorage getArgumentStorage() {
		return argumentStorage;
	}

	public void setSaveTemplate(List<String> saveTemplate) {
		this.saveTemplate = saveTemplate;
	}

	public List<String> getSaveTemplate() {
		return saveTemplate;
	}
}
