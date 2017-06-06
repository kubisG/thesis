package cz.osu.core;

import java.io.File;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Queue;

import javax.annotation.Resource;
import javax.inject.Inject;

import com.github.javaparser.ast.CompilationUnit;

import cz.osu.core.exception.FileExceptionParams;
import cz.osu.core.exception.FileLoaderException;
import cz.osu.core.model.Position;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.assertj.core.api.Assertions.assertThat;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Project: thesis
 * Created by Jakub on 13. 3. 2017.
 */
@ContextConfiguration(locations = {"/application-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class FileLoaderTest {

    private static final String VALID_SELENIUM_TEST_DIR_PATH = "/valid_tests";
    private static final String INVALID_SELENIUM_TEST_DIR_PATH  = "/invalid_tests";
    private static final String BASE_DIR_PATH = ClassLoader.getSystemResource("selenium").getPath();
    private static final String CURRENT_LOCALE = Locale.getDefault().toString();

    @Inject
    private FileLoader fileLoader;

    @Inject
    private FileProvider fileProvider;

    @Resource
    private Properties messageProperties;

    @Test
    public void testLoaderShouldLoadNextTestFileAndDecrementFileCount() throws Exception {
        // prepare
        // TODO: 13. 4. 2017 replace fileProvider by mocked queue
        final Queue<File> seleniumTestFiles = fileProvider.getFilesFromDirectory(BASE_DIR_PATH + VALID_SELENIUM_TEST_DIR_PATH);
        fileLoader.setFiles(seleniumTestFiles);
        final Integer expectedCount = fileLoader.getFilesCount() - 1;

        // execute
        final CompilationUnit actualResult = fileLoader.loadNextTest();
        final Integer actualCount = fileLoader.getFilesCount();

        // verify
        assertThat(actualCount, is(expectedCount));
        assertThat(actualResult, is(notNullValue()));
    }

    @Test
    public void testLoaderShouldThrowFileLoaderExceptionWithCorrectMessageIfParsingFileCauseError() throws Exception {
        // prepare
        final String fileName = "/invalid_selenium_test1.java";
        final String messageProperty = CURRENT_LOCALE + "_parse";
        final File file = new File(BASE_DIR_PATH + INVALID_SELENIUM_TEST_DIR_PATH + fileName);
        final FileLoaderException expectedException =  getFileLoaderException(file, messageProperty);

        // execute
        try {
            fileLoader.loadTest(file);
        } catch (FileLoaderException actualException) {
            // verify
            assertThat(actualException).isInstanceOf(FileLoaderException.class)
                    .hasMessage(expectedException.getMessage());
        }
    }

    @Test
    public void testLoaderShouldThrowFileLoaderExceptionWithCorrectMessageIfReadingFileCauseError() throws Exception {
        // prepare
        final String fileName = "/not_existing_file.java";
        final String messageProperty = CURRENT_LOCALE + "_i/o";
        final File file = new File(BASE_DIR_PATH + INVALID_SELENIUM_TEST_DIR_PATH + fileName);
        final FileLoaderException expectedException =  getFileLoaderException(file, messageProperty);

        // execute
        try {
            fileLoader.loadTest(file);
        } catch (FileLoaderException actualException) {
            // verify
            assertThat(actualException).isInstanceOf(FileLoaderException.class)
                    .hasMessage(expectedException.getMessage());
        }
    }

    @Test
    public void testLoaderShouldReturnTrueIfThereIsFileToLoad() throws Exception {
        // prepare
        final Queue<File> seleniumTestFiles = new LinkedList<File>(){{
            add(new File("mockedFile1"));
            add(new File("mockedFile2"));
        }};
        fileLoader.setFiles(seleniumTestFiles);

        // execute
        boolean actualResult = fileLoader.hasNextTest();

        // verify
        assertThat(actualResult, is(true));
    }

    @Test
    public void testLoaderShouldReturnFalseIfThereIsNoFileToLoad() throws Exception {
        // prepare
        fileLoader.setFiles(new LinkedList<>());

        // execute
        boolean actualResult = fileLoader.hasNextTest();

        // verify
        assertThat(actualResult, is(false));
    }

    @Test
    public void testLoaderShouldReturnFileCount() throws Exception {
        // prepare
        final Queue<File> seleniumTestFiles = new LinkedList<File>(){{
            add(new File("mockedFile1"));
            add(new File("mockedFile2"));
        }};
        fileLoader.setFiles(seleniumTestFiles);

        // execute
        Integer actualCount = fileLoader.getFilesCount();

        // verify
        assertThat(actualCount, is(equalTo(2)));
    }

    private FileLoaderException getFileLoaderException(File file, String messageProperty) {
        final String message = messageProperties.get(messageProperty).toString().concat(" ").concat(file.getPath());
        final FileExceptionParams params = new FileExceptionParams(message, file.getName(), file.getPath(), new Position(3, 9));
        return new FileLoaderException(params);
    }

}