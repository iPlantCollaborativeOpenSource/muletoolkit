package org.iplantc.muletoolkit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit tests for org.iplantc.muletoolkit.DownloadableFile.
 * 
 * @author Dennis Roberts
 */
public class DownloadableFileTest {

    /**
     * Verifies that the constructor initializes the file name and contents.
     */
    @Test
    public void constructorShouldInitializeProperties() {
        DownloadableFile file = new DownloadableFile("foo.txt", "text/plain", "bar".getBytes());
        assertEquals("foo.txt", file.getName());
        assertEquals("bar", new String(file.getContents()));
    }

    /**
     * Verifies that the constructor will reject a null file name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldRejectNullFileName() {
        new DownloadableFile(null, "text/plain", "bar".getBytes());
    }

    /**
     * Verifies that the constructor will reject an empty file name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldRejectEmptyFileName() {
        new DownloadableFile("", "text/plain", "bar".getBytes());
    }

    /**
     * Verifies that the constructor will reject an empty content type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldRejectEmptyContenttype() {
        new DownloadableFile("foo.txt", "", "bar".getBytes());
    }

    /**
     * Verifies that the constructor will reject null file contents.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorShouldRejectNullFileContents() {
        new DownloadableFile("foo.txt", "text/plain", null);
    }
}
