package cz.osu.core.exception;

import cz.osu.core.model.Position;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project: thesis
 * Created by Jakub on 26. 2. 2017.
 */
public class FileLoaderException extends FileBaseException {

    /**
     * Position of error which occurred during parsing.
     */
    private final Position position;

    public FileLoaderException(FileExceptionParams params) {
        super(params.getMessage(), params.getFileName(), params.getFilePath());
        this.position = params.getPosition();
    }

    public Position getPosition() {
        return position;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("position", position)
                .toString();
    }
}
