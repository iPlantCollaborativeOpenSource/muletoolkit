package org.iplantc.muletoolkit.transformers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.iplantc.muletoolkit.DownloadableFile;
import org.junit.Before;
import org.junit.Test;
import org.mule.DefaultMuleMessage;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.TransformerException;

/**
 * Unit tests for org.iplantc.muletoolkit.transformers.DownloadableFileTransformer.
 * 
 * @author Dennis Roberts
 */
public class DownloadableFileTransformerTest {

    /**
     * The content disposition header name.
     */
    private static final String CONTENT_DISPOSITION_HEADER = DownloadableFileTransformer.CONTENT_DISPOSITION_HEADER;

    /**
     * The content type header name.
     */
    private static final String CONTENT_TYPE_HEADER = DownloadableFileTransformer.CONTENT_TYPE_HEADER;

    /**
     * The downloadable file transformer to use in each of the unit tests.
     */
    private DownloadableFileTransformer transformer;

    /**
     * The downloadable file to use in each of the tests.
     */
    private DownloadableFile file;

    /**
     * The Mule message to use in each of the tests.
     */
    private MuleMessage message;

    /**
     * Initializes each of the tests.
     */
    @Before
    public void initialize() {
        transformer = new DownloadableFileTransformer();
        file = new DownloadableFile("foo.txt", "text/plain", "bar".getBytes());
        message = new DefaultMuleMessage(file);
    }

    /**
     * Verifies that the file contents are extracted into the message payload.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldExtractFileContents() throws Exception {
        byte[] output = (byte[]) transformer.transform(message, "UTF-8");
        assertEquals("bar", new String(output));
    }

    /**
     * Verifies that the content type header is updated if the content type is specified in the file object.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldUpdateContentTypeIfSpecified() throws Exception {
        transformer.transform(message, "UTF-8");
        assertEquals("text/plain", message.getStringProperty(CONTENT_TYPE_HEADER, null));
    }

    /**
     * Verifies that the content type header is not updated if the content type is not specified in the file object.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldNotUpdateContentTypeIfNotSpecified() throws Exception {
        message = new DefaultMuleMessage(new DownloadableFile("foo.txt", null, "bar".getBytes()));
        transformer.transform(message, "UTF-8");
        assertNull(message.getStringProperty(CONTENT_TYPE_HEADER, null));
    }

    /**
     * Verifies that the content disposition is ignored if it's not already defined.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldIgnoreUndefinedContentDisposition() throws Exception {
        transformer.transform(message, "UTF-8");
        assertNull(message.getStringProperty(CONTENT_DISPOSITION_HEADER, null));
    }

    /**
     * Verifies that the content disposition is ignored if it's requesting inline display of the file contents.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldIgnoreInlineContentDisposition() throws Exception {
        message.setStringProperty(CONTENT_DISPOSITION_HEADER, "inline");
        transformer.transform(message, "UTF-8");
        assertEquals("inline", message.getStringProperty(CONTENT_DISPOSITION_HEADER, ""));
    }

    /**
     * Verifies that the content disposition is updated if it's indicating that the file contents should be treated as
     * an
     * attachment.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldUpdateAttachmentContentDisposition() throws Exception {
        message.setStringProperty(CONTENT_DISPOSITION_HEADER, "attachment");
        transformer.transform(message, "UTF-8");
        assertEquals("attachment; filename=foo.txt", message.getStringProperty(CONTENT_DISPOSITION_HEADER, ""));
    }

    /**
     * Verifies that the content disposition is updated if it's defined and set to an unrecognized value.
     * 
     * @throws Exception if an error occurs.
     */
    @Test
    public void shouldUpdateArbitraryContentDisposition() throws Exception {
        message.setStringProperty(CONTENT_DISPOSITION_HEADER, "something arbitrary");
        transformer.transform(message, "UTF-8");
        assertEquals("attachment; filename=foo.txt", message.getStringProperty(CONTENT_DISPOSITION_HEADER, ""));
    }

    /**
     * Verifies that a message payload that is not an instance of DownloadableFile results in a TransformerException.
     * 
     * @throws Exception if an error occurs.
     */
    @Test(expected = TransformerException.class)
    public void shouldRejectInvalidMessagePayload() throws Exception {
        message.setPayload("some arbitrary payload");
        transformer.transform(message, "UTF-8");
    }
}
