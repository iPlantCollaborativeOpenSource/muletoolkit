package org.iplantc.muletoolkit.transformers;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.iplantc.security.Saml2AuthenticationToken;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractTransformer;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;

/**
 * Adds the username from the message authentication context to the message payload.
 * 
 * @author Dennis Roberts
 */
public class AddUsernameToJson extends AbstractTransformer {

    /**
     * The default username to use when security is disabled.
     */
    private String defaultUsername;

    /**
     * @param defaultUsername the default username.
     */
    public void setDefaultUsername(String defaultUsername) {
        this.defaultUsername = defaultUsername;
    }

    /**
     * @return the default username.
     */
    public String getDefaultUsername() {
        return defaultUsername;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object doTransform(Object payload, String encoding) throws TransformerException {
        try {
            return addUsernameToPayload(payload, getUsername());
        }
        catch (Exception e) {
            Message msg = MessageFactory.createStaticMessage("unable to add the username to the message body");
            throw new TransformerException(msg, e);
        }
    }

    /**
     * Adds the username to the message payload.
     * 
     * @param payload the message payload.
     * @param username the username.
     * @return the payload.
     */
    protected Object addUsernameToPayload(Object payload, String username) {
        JSONObject json = (JSONObject) JSONSerializer.toJSON(payload);
        json.put("user", username);
        return json.toString();
    }

    /**
     * Gets the name of the authenticated user from the security context.
     * 
     * @return the username.
     */
    protected String getUsername() {
        String username = null;
        Saml2AuthenticationToken authn = getAuthentiationToken();
        if (authn != null) {
            Object principal = authn.getPrincipal();
            if (principal instanceof UserDetails) {
                UserDetails userDetails = (UserDetails) principal;
                username = userDetails.getUsername().split("@")[0];
            }
        }
        if (username == null) {
            username = defaultUsername;
        }
        return username;
    }

    /**
     * Gets the authentication token from the security context.
     * 
     * @return the authentication token or null if the user isn't authenticated.
     */
    private Saml2AuthenticationToken getAuthentiationToken() {
        Saml2AuthenticationToken authn = null;
        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            authn = (Saml2AuthenticationToken) securityContext.getAuthentication();
        }
        return authn;
    }
}
