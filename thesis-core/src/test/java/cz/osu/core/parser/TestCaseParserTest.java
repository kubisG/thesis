package cz.osu.core.parser;

import cz.osu.test.BeforeMethod;
import cz.osu.test.Fields;
import cz.osu.test.TestMethod;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.io.FileNotFoundException;
import java.util.List;

import com.github.javaparser.ast.expr.MethodCallExpr;

import cz.osu.core.model.TestCase;
import cz.osu.core.parser.resolver.BindingResolver;

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
        // set up BindingResolver
        bindingResolver.setFields(Fields.FIELDS);
        bindingResolver.setBeforeMethod(BeforeMethod.BEFORE_METHOD_BODY);
        bindingResolver.setTestCase(TestMethod.TEST_METHOD_BODY);
    }

    @Test
    @Ignore
    public void testTestCaseParserShouldParseMethodCallExpr() {
        // prepare
        List<MethodCallExpr> expectedResult = null;

        // execute
        TestCase actualResult = testCaseParser.parse(TestMethod.TEST_METHOD);

        // verify
        assertThat(actualResult).isEqualTo(null);
    }

}