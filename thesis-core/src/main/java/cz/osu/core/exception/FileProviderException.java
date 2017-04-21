package cz.osu.core.exception;

/**
 * Project: thesis
 * Created by Jakub on 24. 3. 2017.
 */
public class FileProviderException extends FileBaseException {

    public FileProviderException(String message) {
        super(message);
    }

    public FileProviderException(FileExceptionParams params) {
        super(params.getMessage(), params.getFileName(), params.getFilePath());
    }
}
