package org.iplantc.muletoolkit.service;

/**
 * Implements a placeholder service that does absolutely nothing. This service should be used in conjunction with the
 * orchestration router in cases where the information that is to be returned may or may not be available already. If
 * the information is already available then the do-nothing service can be used as a placeholder so that the method
 * being called does nothing and the endpoint configuration extracts the information to return.
 * 
 * @author Dennis Roberts
 */
public class DoNothingService {

    /**
     * A placeholder method that does nothing.
     */
    public void doNothing() {
    }
}
