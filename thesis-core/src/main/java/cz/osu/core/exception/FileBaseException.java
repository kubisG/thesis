package cz.osu.core.exception;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project: thesis
 * Created by Jakub on 25. 3. 2017.
 */
public abstract class FileBaseException extends Exception {

    /**
     * Name of file.
     */
    private final String fileName;

    /**
     * Path to file.
     */
    private final String filePath;

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public FileBaseException(String message) {
        super(message);
        this.fileName = null;
        this.filePath = null;
    }

    public FileBaseException(String message, String fileName, String filePath) {
        super(message);
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fileName", fileName)
                .append("filePath", filePath)
                .toString();
    }
}
