package cz.osu.core.loader;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import javax.inject.Inject;

import cz.osu.core.exception.FileExceptionParams;
import cz.osu.core.exception.FileProviderException;
import cz.osu.core.util.FileExceptionUtil;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 13. 3. 2017.
 *
 * Class is responsible for creating and providing a file queue.
 */
@Component
public final class TestSuitsProvider {

    private FileExceptionUtil exceptionUtil;

    @Inject
    public TestSuitsProvider(FileExceptionUtil exceptionUtil) {
        this.exceptionUtil = exceptionUtil;
    }

    /**
     * Method creates and provides a file queue.
     * @param directoryPath path to directory with files.
     * @return file queue.
     * @throws FileProviderException if directoryPath is null.
     * If directoryPath doesn't refer to directory.
     * If system security manager deny access to current directory.
     */
    public Queue<File> getFilesFromDirectory(String directoryPath) throws FileProviderException {
        FileExceptionParams fileExceptionParams;

        if (directoryPath == null) {
            throw new FileProviderException("fileprovider.nullpointer");
        }
        final File directory = new File(directoryPath);

        try {
            Collection<File> files = FileUtils.listFiles(directory, null, true);
            return new LinkedList<>(files);
        } catch (IllegalArgumentException ex) {
            fileExceptionParams = exceptionUtil.getFileLoaderExceptionParams(directory, "fileprovider.illegalargument");
            throw new FileProviderException(fileExceptionParams);
        } catch (SecurityException ex) {
            fileExceptionParams = exceptionUtil.getFileLoaderExceptionParams(directory, "fileprovider.security");
            throw new FileProviderException(fileExceptionParams);
        }
    }
}
