package cz.osu.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ast.CompilationUnit;

import cz.osu.core.exception.FileExceptionParams;
import cz.osu.core.exception.FileLoaderException;
import cz.osu.core.model.Position;
import cz.osu.core.util.FileExceptionUtil;

import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 26. 2. 2017.
 *
 * Class which load and parse input selenium test file/s.
 */
@Component
public final class TestSuitLoader {

    @Inject
    private FileExceptionUtil exceptionUtil;

    /**
     * Queue of files which contains selenium test/s.
     */
    private Queue<File> files = new LinkedList<>();

    /**
     * Method uses loadTest method to load next file from base directory. The result of this method is compilation unit.
     * @return CompilationUnit which represent selenium test file in objects (class, methods, fields, variables etc.).
     * @throws FileLoaderException if reading file cause exception (e.g. IOException and it's childes)
     * and also if parsing file cause exception (e.g. ParseProblemException).
     */
    public CompilationUnit loadNextTest() throws FileLoaderException {
        File currentFile = files.remove();
        return loadTest(currentFile);
    }

    /**
     * Method read and parse file. The result of this method is compilation unit.
     * @return CompilationUnit which represent selenium test file in objects (class, methods, fields, variables etc.).
     * @throws FileLoaderException if reading file cause exception (e.g. IOException and it's childes)
     * or if parsing file cause exception (e.g. ParseProblemException).
     */
    CompilationUnit loadTest(File currentFile) throws FileLoaderException {
        CompilationUnit compilationUnit;
        FileExceptionParams fileExceptionParams;

        try (FileInputStream fis = new FileInputStream(currentFile)) {
            compilationUnit = JavaParser.parse(fis);
            return compilationUnit;
        } catch (IOException ex) {
            fileExceptionParams = exceptionUtil.getFileLoaderExceptionParams(currentFile, "fileloader.i/o");
            throw new FileLoaderException(fileExceptionParams);
        } catch (ParseProblemException ex) {
            fileExceptionParams = exceptionUtil.getFileLoaderExceptionParams(currentFile, "fileloader.parseproblem", ex.getMessage());
            throw new FileLoaderException(fileExceptionParams);
        }
    }

    /**
     * Method which check if there is another test.
     * @return true if there is another test to load, false otherwise.
     */
    public boolean hasNextTest() {
        return !files.isEmpty();
    }

    /**
     * Method return current size of file queue.
     * @return Integer size.
     */
    public Integer getFilesCount() {
        return files.size();
    }

    /**
     * Method set up file queue.
     * @param files which will be processed.
     */
    public void setFiles(Queue<File> files) {
        this.files = files;
    }

}
