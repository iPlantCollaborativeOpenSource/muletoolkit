package org.iplantc.muletoolkit.transformers;

import org.iplantc.muletoolkit.DownloadableFile;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;
import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transformer.AbstractMessageAwareTransformer;

/**
 * Transforms a message containing an instance of org.iplantc.muletoolkit.DownloadableFile into a file that can be
 * interpreted by the browser. Whether or not the file can be downloaded is determined by the Content-Disposition
 * header that is already in the message. The service that uses this transformer can either set the value of this
 * header explicitly or leave it unmodified, allowing whoever makes the initial request to determine whether or not
 * the file contents should be viewed inline. If the file contents are to be treated as an attachment then the file
 * name is added to the ContentDisposition header. In either case, the message body is replaced by just the file
 * contents.
 * 
 * @author Dennis Roberts
 */
public class DownloadableFileTransformer extends AbstractMessageAwareTransformer {

    /**
     * The name of the content type header.
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    /**
     * The name of the content disposition header.
     */
    public static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";

    /**
     * {@inheritDoc}
     */
    @Override
    public Object transform(final MuleMessage message, final String outputEncoding) throws TransformerException {
        try {
            DownloadableFile file = (DownloadableFile) message.getPayload();
            updateContentDisposition(message, file);
            updateContentType(message, file);
            return file.getContents();
        }
        catch (ClassCastException e) {
            String className = message.getPayload().getClass().getName();
            Message error = MessageFactory.createStaticMessage("unacceptable message payload type: " + className);
            throw new TransformerException(error);
        }
    }

    /**
     * Updates the content type header if the content type is specified in the DownloadableFile object.
     * 
     * @param message the Mule message.
     * @param file the downloadable file as extracted from the message payload.
     */
    private void updateContentType(MuleMessage message, DownloadableFile file) {
        String contentType = file.getContentType();
        if (file.getContentType() != null) {
            message.setStringProperty(CONTENT_TYPE_HEADER, contentType);
        }
    }

    /**
     * Updates the content disposition header if it is already defined and does not specifically request inline display
     * of the file contents.
     * 
     * @param message the Mule message.
     * @param file the downloadable file as extracted from the message payload.
     */
    public void updateContentDisposition(final MuleMessage message, DownloadableFile file) {
        String contentDisposition = message.getStringProperty(CONTENT_DISPOSITION_HEADER, null);
        if (contentDisposition != null && !contentDisposition.startsWith("inline")) {
            contentDisposition = "attachment; filename=" + file.getName();
            message.setStringProperty(CONTENT_DISPOSITION_HEADER, contentDisposition);
        }
    }
}
