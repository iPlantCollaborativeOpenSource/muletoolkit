package org.iplantc.muletoolkit.service;

import java.util.Map;

import org.apache.log4j.Logger;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

public class RetrieveTransientCacheService implements Callable {
	private static final Logger LOG = Logger.getLogger(RetrieveTransientCacheService.class);
	private Map<Integer, Object> cache;
	
	public void setCache(Map<Integer, Object> cache) {
		this.cache = cache;
	}

	public Map<Integer, Object> getCache() {
		return cache;
	}

	@Override
	public Object onCall(MuleEventContext arg0) throws Exception {
		LOG.debug("Getting: " + cache.get((Number)(arg0.getMessage().getPayload(Number.class))));
		return cache.remove((Number)(arg0.getMessage().getPayload(Number.class)));
	}
}
