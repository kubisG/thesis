package cz.osu.core.runner;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CountDownLatch;

import cz.osu.core.runner.action.ActionFacade;
import cz.osu.core.enums.ScopeType;
import cz.osu.core.runner.factory.WebDriverFactory;
import cz.osu.core.model.Scope;
import cz.osu.core.model.Statement;
import cz.osu.core.model.TestCase;
import cz.osu.core.recorder.Recorder;

/**
 * Project: thesis
 * Created by Jakub on 6. 6. 2017.
 */
@Component
public class TestCaseRunner {

    private final StatementRunner statementRunner;

    private final ActionFacade actionFacade;

    private final Recorder recorder;

    private Thread recorderThread;

    private WebDriver driver;

    @Inject
    public TestCaseRunner(StatementRunner statementRunner, ActionFacade actionFacade, Recorder recorder) {
        this.statementRunner = statementRunner;
        this.actionFacade = actionFacade;
        this.recorder = recorder;
    }

    private void startRecord(String outputFile) throws InterruptedException {
        // do some thread synchronization using latch
        // main thread have to wait for runner thread
        CountDownLatch latch = new CountDownLatch(1);
        // set recorder
        recorder.setOutputFile(outputFile);
        recorder.setLatch(latch);
        // create record thread
        recorderThread = new Thread(recorder);
        recorderThread.start();
        // wait for record thread
        latch.await();
    }

    private void stopRecord() {
        recorderThread.interrupt();
    }

    private void startDriver(String driverName) throws IOException {
        driver = WebDriverFactory.getWebDriver(driverName);
        actionFacade.setDriver(driver);
    }

    private void stopDriver() {
        driver.close();
    }

    private void setDriverAsScope(Statement statement) {
        statement.removeMethod();
        Scope scope = statement.getFirstMethod().getScope();
        scope.setScopeValue(driver);
        scope.setScopeType(ScopeType.CLASS_INSTANCE);
    }

    private void setStatementScope(Statement statement) {
        setDriverAsScope(statement);

        // use above function recursively on method parameters too
        statement.getMethods().stream()
                .flatMap(method -> method.getParameters().stream())
                .filter(methodParameter -> (methodParameter instanceof Statement))
                .map(methodParameter -> (Statement) methodParameter)
                .filter(stmt -> stmt.getFirstMethod().getScope().getScopeType().equals(ScopeType.DRIVER_INSTANCE))
                .forEach(this::setStatementScope);
    }

    private void prepare(TestCase testCase) {
        testCase.getStatements().stream()
                .filter(statement -> statement.getFirstMethod().getScope().getScopeType().equals(ScopeType.DRIVER_INSTANCE))
                .forEach(this::setStatementScope);
    }

    private void execute(TestCase testCase) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, AWTException, InterruptedException {
        while (testCase.hasNextStatement()) {
            statementRunner.run(testCase.removeNextStatement());
        }
    }

    public void run(TestCase testCase, String testSuitDir) throws IOException, InterruptedException,
                                            InvocationTargetException, NoSuchMethodException,
                                            InstantiationException, IllegalAccessException, AWTException {

        // build absolute path where we will export video with current test case
        String absoluteFileName = testSuitDir.concat(testCase.getName()).concat(".avi");
        // start recorder
        //startRecord(absoluteFileName);
        // start and set up driver
        startDriver(testCase.getDriverName());
        // prepare test case for running
        prepare(testCase);
        // perform each statement in testcase
        execute(testCase);
        // stop driver and close all associated windows
        stopDriver();
        // stop recorder
        //stopRecord();
    }

}
