package org.iplantc.muletoolkit;

/**
 * Represents a file that may be downloaded.
 * 
 * @author Dennis Roberts
 */
public class DownloadableFile {

    /**
     * The file name.
     */
    private String name;

    /**
     * The content type.
     */
    private String contentType;

    /**
     * The file contents.
     */
    private byte[] contents;

    /**
     * Sets the file name.
     * 
     * @param name the file name.
     * @throws IllegalArgumentException if the name is null or empty.
     */
    private void setName(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("the file name may not be null or empty");
        }
        this.name = name;
    }

    /**
     * Gets the file name.
     * 
     * @return the file name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the content type to be assigned to the file.  We may not always want to specify the content type, so a null
     * content type is permissible.
     * 
     * @param contentType the content type to assign to the file.
     * @throws IllegalArgumentException if the content type is null or empty.
     */
    private void setContentType(String contentType) {
        if (contentType != null && contentType.equals("")) {
            throw new IllegalArgumentException("the content type may not be null or empty");
        }
        this.contentType = contentType;
    }

    /**
     * Gets the content type.
     * 
     * @return the content type.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Sets the file contents.
     * 
     * @param contents the file contents.
     * @throws IllegalArgumentException if the file contents are null.
     */
    private void setContents(byte[] contents) {
        if (contents == null) {
            throw new IllegalArgumentException("the file contents may not be null");
        }
        this.contents = contents;
    }

    /**
     * Gets the file contents.
     * 
     * @return the file contents.
     */
    public byte[] getContents() {
        return contents;
    }

    /**
     * Constructs a downloadable file with the given name and contents.
     * 
     * @param name the name of the file, which may not be null or empty.
     * @param contentType the content type to assign to the file.
     * @param contents the contents of the file, which may not be null.
     * @throws IllegalArgumentException if either argument is invalid.
     */
    public DownloadableFile(String name, String contentType, byte[] contents) {
        setName(name);
        setContentType(contentType);
        setContents(contents);
    }
}
