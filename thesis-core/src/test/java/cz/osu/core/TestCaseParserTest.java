package cz.osu.core;

import cz.osu.test.BeforeMethod;
import cz.osu.test.Fields;
import cz.osu.test.TestMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.io.FileNotFoundException;
import java.util.List;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import cz.osu.core.model.Statement;
import cz.osu.core.model.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Project: thesis
 * Created by Jakub on 20. 4. 2017.
 */

@ContextConfiguration(locations = {"/application-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCaseParserTest {

    @Inject
    private TestCaseParser testCaseParser;

    @Inject
    private BindingResolver bindingResolver;

    @Before
    public void setUp() throws FileNotFoundException {
        // set up TestCaseParser
        testCaseParser.setTestCase(TestMethod.TEST_METHOD_BODY);

        // set up BindingResolver
        bindingResolver.setFields(Fields.FIELDS);
        bindingResolver.setBeforeMethod(BeforeMethod.BEFORE_METHOD_BODY);
        bindingResolver.setTestCase(TestMethod.TEST_METHOD_BODY);
    }

    @Test
    public void testTestCaseParserShouldParseMethodCallExpr() {
        // prepare
        List<MethodCallExpr> expectedResult = null;
        ExpressionStmt expressionStmt = (ExpressionStmt) TestMethod.TEST_METHOD_BODY.getStatements().get(7);
        MethodCallExpr methodCall = (MethodCallExpr) expressionStmt.getExpression();

        // execute
        Statement actualResult = testCaseParser.parseMethodCall(methodCall);

        // verify
        assertThat(actualResult).isEqualTo(null);
    }

    @Test
    public void testTestCaseParserShouldParseMethodBody() {
        // execute
        TestCase actualResult = testCaseParser.parseTestCase();

        // verify
        assertThat(actualResult).isEqualTo(null);
    }
}