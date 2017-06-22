package cz.osu.core.runner;

import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import cz.osu.core.Recorder;
import cz.osu.core.action.ActionFacade;
import cz.osu.core.enums.ScopeType;
import cz.osu.core.factory.WebDriverFactory;
import cz.osu.core.model.Scope;
import cz.osu.core.model.Statement;
import cz.osu.core.model.TestCase;

/**
 * Project: thesis
 * Created by Jakub on 6. 6. 2017.
 */
@Component
public class TestCaseRunner {

    private final StatementRunner statementRunner;

    private final Recorder recorder;

    private final ActionFacade actionFacade;

    private WebDriver driver;

    @Inject
    public TestCaseRunner(StatementRunner statementRunner, Recorder recorder, ActionFacade actionFacade) {
        this.statementRunner = statementRunner;
        this.recorder = recorder;
        this.actionFacade = actionFacade;
    }

    private void startDriver(String driverName) throws IOException {
        driver = WebDriverFactory.getWebDriver(driverName);
        actionFacade.setDriver(driver);
    }

    private void stopDriver() {
        driver.quit();
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

    public void run(TestCase testCase) throws IOException, InterruptedException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, AWTException {
        // start and set up driver
        startDriver(testCase.getDriverName());
        // prepare test case for running
        prepare(testCase);
        // perform each statement in testcase
        execute(testCase);
        // wait 3 sec
        Thread.sleep(5000);
        // stop driver and close all associated windows
        stopDriver();
    }

}
