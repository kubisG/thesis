package cz.osu.core.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.osu.core.exception.FileExceptionParams;
import cz.osu.core.model.Position;

import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 13. 4. 2017.
 */

@Component
public class FileExceptionUtil extends MessageSourceWrapper {

    private static final String ERROR_POSITION_REGEX = "line\\s+\\d+,\\scolumn\\s\\d+";
    private static final String NUMBER_FORMAT_REGEX = "\\d+";
    private static final Pattern ERROR_POSITION_PATTERN = Pattern.compile(ERROR_POSITION_REGEX);
    private static final Pattern NUMBER_FORMAT_PATTERN = Pattern.compile(NUMBER_FORMAT_REGEX);

    public FileExceptionParams getFileLoaderExceptionParams(File currentFile, String propertyPlaceholder) {
        final String fileName = currentFile.getName();
        final String filePath = currentFile.getPath();
        final String message = getMessage(propertyPlaceholder, fileName, filePath);

        return new FileExceptionParams(message, fileName, filePath);
    }

    /**
     * Method which set up and returned FileExceptionParams.
     * @param currentFile current processed file.
     * @param propertyPlaceholder error message template defined in message property file.
     * Depends on current locals and current error.
     * @param parseProblemMessage message describes error which occurred during parsing file.
     * @return FileExceptionParams.
     */
    public FileExceptionParams getFileLoaderExceptionParams(File currentFile, String propertyPlaceholder, String parseProblemMessage) {
        final String fileName = currentFile.getName();
        final String filePath = currentFile.getPath();
        final Position position = getErrorPosition(parseProblemMessage);

        final String message = getMessage(propertyPlaceholder, fileName, position.getLine(), position.getColumn(), filePath);

        return new FileExceptionParams(message, fileName, filePath, position);
    }

    /**
     * Method parse list with line and column to Position object (wrapper).
     * @param positionParts list which contains line and column.
     * @return Position wrapper which represents position of error. Line and column are nullable in wrapper.
     */
    private Position parseListToPosition(List<Integer> positionParts) {
        Position position = new Position();

        if (positionParts != null) {
            position.setLine(positionParts.get(0));
            position.setColumn(positionParts.get(1));
        }
        return position;
    }

    /**
     * Method returns position of parse problem.
     * @param parseProblemMessage message contains information about parse problems.
     * @return Position which contains line and column which represents error position. If error position is not present
     * (in parseProblemMessage) then return Position with null line and column.
     */
    private Position getErrorPosition(String parseProblemMessage) {
        Matcher matcher = ERROR_POSITION_PATTERN.matcher(parseProblemMessage);
        List<Integer> positionParts = new LinkedList<>();

        if (!matcher.find()) return new Position();

        String errorPositionString = matcher.group();
        matcher = NUMBER_FORMAT_PATTERN.matcher(errorPositionString);

        // find and keep only numbers (line and column number)
        while (matcher.find()) {
            positionParts.add(Integer.valueOf(matcher.group()));
        }

        return parseListToPosition(positionParts);
    }

}
