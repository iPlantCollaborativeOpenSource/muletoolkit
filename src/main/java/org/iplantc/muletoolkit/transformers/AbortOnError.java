package org.iplantc.muletoolkit.transformers;

import org.mule.RequestContext;
import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

/**
 * The AbortOnError transformer is used to stop the normal processing of response transformers
 * if an exception occurs within a service.  It is always used with the FormatError transformer.
 * For example, a mule configuration might resemble:
 *
 *     <vm:outbound-endpoint path="retrievetrees" responseTransformer-refs="FormatError AbortOnError ExtractTreeInfo SpitOutJsonTreeList" />
 *
 * The above mule statement can be read as "format any error that has occurred and then abort, else
 * do the tree info extraction and spit out the JSON tree list".
 *
 * @author Donald A. Barre
 */
public class AbortOnError extends AbstractTransformer {

    /* (non-Javadoc)
     * @see org.mule.transformer.AbstractTransformer#doTransform(java.lang.Object, java.lang.String)
     */
    @Override
    protected Object doTransform(Object payload, String encoding)
            throws TransformerException {
        return payload;
    }

    /* (non-Javadoc)
     * @see org.mule.transformer.AbstractTransformer#isSourceTypeSupported(java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean isSourceTypeSupported(Class srcCls) {
        return false;
    }

    /* (non-Javadoc)
     * @see org.mule.transformer.AbstractTransformer#isIgnoreBadInput()
     */
    @Override
    public boolean isIgnoreBadInput() {
        Object code = RequestContext.getEvent().getMessage().getProperty(FormatError.HTTP_STATUS);
        if (code != null) {
            return !code.equals(FormatError.INTERNAL_SERVER_ERROR);
        }
        return true;
    }
}

