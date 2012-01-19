package org.iplantc.muletoolkit.transformers;

import java.util.HashMap;
import java.util.Map;

public class ArgumentStorage {
	private Map<Thread, Object[]> savedArgs = new HashMap<Thread, Object[]>();

	public void save(Object[] args) {
		savedArgs.put(Thread.currentThread(), args);
	}
	
	public Object[] restore() {
		Object[] args;
		if ((args = savedArgs.remove(Thread.currentThread())) == null) {
			throw new IllegalArgumentException("No arguments were stored for this thread");
		}
		return args;
	}
}
