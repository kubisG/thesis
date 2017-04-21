package cz.osu.core.exception;

import cz.osu.core.model.Position;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project: thesis
 * Created by Jakub on 26. 3. 2017.
 */
public class FileExceptionParams {
    /**
     * Name of current processed file.
     */
    private final String fileName;

    /**
     * Path to current processed file.
     */
    private final String filePath;

    /**
     * Message which describe what's went wrong.
     */
    private final String message;

    /**
     * Position of error.
     */
    private Position position;

    public FileExceptionParams(String message, String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.message = message;
    }

    public FileExceptionParams(String message, String fileName, String filePath, Position position) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.message = message;
        this.position = position;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getMessage() {
        return message;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FileExceptionParams)) return false;

        FileExceptionParams that = (FileExceptionParams) o;

        return new EqualsBuilder()
                .append(fileName, that.fileName)
                .append(filePath, that.filePath)
                .append(message, that.message)
                .append(position, that.position)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(fileName)
                .append(filePath)
                .append(message)
                .append(position)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("fileName", fileName)
                .append("filePath", filePath)
                .append("message", message)
                .append("position", position)
                .toString();
    }
}
