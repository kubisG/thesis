package cz.osu.core;

import javax.inject.Inject;

import cz.osu.core.exception.FileLoaderException;
import cz.osu.core.exception.FileProviderException;
import cz.osu.core.loader.TestSuitLoader;
import cz.osu.core.loader.TestSuitsProvider;
import cz.osu.core.model.TestSuit;
import cz.osu.core.parser.TestSuitParser;
import cz.osu.core.runner.TestSuitRunner;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Project: thesis
 * Created by Jakub on 13. 3. 2017.
 */

@Component
public class UserDocumentationMaker {

    private final TestSuitLoader testSuitLoader;

    private final TestSuitsProvider testSuitsProvider;

    private final TestSuitParser testSuitParser;

    private final TestSuitRunner testSuitRunner;

    @Inject
    public UserDocumentationMaker(TestSuitLoader testSuitLoader, TestSuitsProvider testSuitsProvider, TestSuitParser testSuitParser, TestSuitRunner testSuitRunner) {
        this.testSuitLoader = testSuitLoader;
        this.testSuitsProvider = testSuitsProvider;
        this.testSuitParser = testSuitParser;
        this.testSuitRunner = testSuitRunner;
    }

    private String createPath(String baseDirPath) {
        return baseDirPath
                .concat(File.separator)
                .concat(testSuitLoader.getTestName())
                .concat(File.separator);
    }

    private void tryCreateTestSuitDir(String testSuitPath) {
        Path path = Paths.get(testSuitPath);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void process(String inputDirectory, String outputDirectory) throws FileProviderException, FileLoaderException,
                                                                            NoSuchMethodException, InterruptedException,
                                                                            IOException, InstantiationException, AWTException,
                                                                            IllegalAccessException, InvocationTargetException {
        // try to get test files from given directory
        Queue<File> testFiles = testSuitsProvider.getFilesFromDirectory(inputDirectory);
        // set up test suit loader to be able to load test files
        testSuitLoader.setFiles(testFiles);

        while (testSuitLoader.hasNextTest()) {
            // build path for test suit directory
            String testSuitPath = createPath(outputDirectory);
            // try to create output dir for current test suit
            tryCreateTestSuitDir(testSuitPath);
            // load test file
            CompilationUnit c = testSuitLoader.loadNextTest();
            // parse test suit
            TestSuit testSuit = testSuitParser.parse(c);
            // run and record all test cases in current test suit
            testSuitRunner.run(testSuit, testSuitPath);
        }
    }
}
