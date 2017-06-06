package cz.osu.core;

import cz.osu.test.BeforeMethod;
import cz.osu.test.Fields;
import cz.osu.test.TestMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.expression.spel.ast.BooleanLiteral;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.util.List;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import cz.osu.core.model.Variable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Project: thesis
 * Created by Jakub on 20. 4. 2017.
 */

@ContextConfiguration(locations = {"/application-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class VariableParserTest {

    @Inject
    private VariableParser variableParser;

    @Before
    public void setUp() {

    }

    @Test
    public void testVariableParserShouldParseStringLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable("baseUrl", String.class);
        final StringLiteralExpr argument = new StringLiteralExpr("baseUrl");

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

    @Test
    public void testVariableParserShouldParseDoubleLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable(2.0, Double.class);
        final DoubleLiteralExpr argument = new DoubleLiteralExpr("2.0");

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

    @Test
    public void testVariableParserShouldParseIntegerLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable(2, Integer.class);
        final IntegerLiteralExpr argument = new IntegerLiteralExpr("2");

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

    @Test
    public void testVariableParserShouldParseCharLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable('c', Character.class);
        final CharLiteralExpr argument = new CharLiteralExpr("c");

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

    @Test
    public void testVariableParserShouldParseLongLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable(11111111111L, Long.class);
        final LongLiteralExpr argument = new LongLiteralExpr("11111111111");

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

    @Test
    public void testVariableParserShouldParseNullLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable(null, null);
        final NullLiteralExpr argument = new NullLiteralExpr();

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

    @Test
    public void testVariableParserShouldParseBooleanLiteralExpression() throws Exception {
        // prepare
        final Variable expectedVariable = new Variable(true, Boolean.class);
        final BooleanLiteralExpr argument = new BooleanLiteralExpr(true);

        // execute
        Variable actualVariable = variableParser.parse(argument);

        // verify
        assertThat(actualVariable).isEqualTo(expectedVariable);
    }

}