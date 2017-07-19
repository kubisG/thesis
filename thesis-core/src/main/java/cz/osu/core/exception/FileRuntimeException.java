package cz.osu.core.exception;

/**
 * Project: thesis
 * Created by Jakub on 18. 7. 2017.
 *
 * Class representing an exception that occurs when some
 * operation on files fail.
 */
public class FileRuntimeException extends RuntimeException {

    public FileRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileRuntimeException(String message) {
        super(message);
    }
}
