package org.iplantc.muletoolkit.service;

import java.util.Map;

import org.mule.DefaultMuleMessage;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

public class SetTransientCacheService implements Callable {
	private Map<Long, Object> cache;
	private long currentKey = 0;
	
	public void setCache(Map<Long, Object> cache) {
		this.cache = cache;
	}

	public Map<Long, Object> getCache() {
		return cache;
	}

	@Override
	public Object onCall(MuleEventContext arg0) throws Exception {
		cache.put(currentKey, arg0.getMessage());
		return new DefaultMuleMessage(currentKey++);
	}
}
