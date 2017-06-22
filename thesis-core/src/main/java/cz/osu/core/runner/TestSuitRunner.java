package cz.osu.core.runner;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import cz.osu.core.model.TestSuit;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
@Component
public class TestSuitRunner {

    private final TestCaseRunner testCaseRunner;

    @Inject
    public TestSuitRunner(TestCaseRunner testCaseRunner) {
        this.testCaseRunner = testCaseRunner;
    }

    public void run(TestSuit testSuit) throws IOException, InterruptedException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, AWTException {
        // run all test cases
        while (testSuit.hasNextTestCase()) {
            testCaseRunner.run(testSuit.getNextTestCase());
        }
    }
}
