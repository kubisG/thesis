package cz.osu.core;

import cz.osu.test.BeforeMethod;
import cz.osu.test.Fields;
import cz.osu.test.TestMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Project: thesis
 * Created by Jakub on 23. 4. 2017.
 */

@ContextConfiguration(locations = {"/application-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class BindingResolverTest {

    @Inject
    private BindingResolver bindingResolver;

    private Range argumentRange;

    @Before
    public void setUp() {
        bindingResolver.setFields(Fields.FIELDS);
        bindingResolver.setBeforeMethod(BeforeMethod.BEFORE_METHOD_BODY);
        bindingResolver.setTestCase(TestMethod.TEST_METHOD_BODY);

        argumentRange = new Range(new Position(29, 9), new Position(29, 50));
    }

    @Test
    public void testBindingResolverShouldReturnTrueIfNameExprArgumentIsDeclaredWithinTestCase() {
        // prepare
        final Expression mockedArgument = new NameExpr(argumentRange, new SimpleName("baseUrl"));

        // execute
        final boolean actualResult = bindingResolver.isLocalVariable(mockedArgument);

        // verify
        assertThat(actualResult).isTrue();
    }

    @Test
    public void testBindingResolverShouldReturnFalseIfNameExprArgumentIsNotDeclaredWithinTestCase() {
        // prepare
        final Expression mockedArgument = new NameExpr(argumentRange, new SimpleName("unknown"));

        // execute
        final boolean actualResult = bindingResolver.isLocalVariable(mockedArgument);

        // verify
        assertThat(actualResult).isFalse();
    }


    @Test
    public void testBindingResolverShouldReturnTrueIfFieldExprArgumentIsDeclaredWithinTestCase() {
        // prepare
        final Expression mockedArgument = new FieldAccessExpr(argumentRange, new ThisExpr(),
                new NodeList<>(), new SimpleName("baseUrl"));

        // execute
        final boolean actualResult = bindingResolver.isLocalVariable(mockedArgument);

        // verify
        assertThat(actualResult).isTrue();
    }

    @Test
    public void testBindingResolverShouldReturnTrueIfFieldExprArgumentIsNotDeclaredWithinTestCase() {
        // prepare
        final Expression mockedArgument = new FieldAccessExpr(argumentRange, new ThisExpr(),
                new NodeList<>(), new SimpleName("unknown"));

        // execute
        final boolean actualResult = bindingResolver.isLocalVariable(mockedArgument);

        // verify
        assertThat(actualResult).isFalse();
    }

    @Test
    public void testBindingResolverShouldFindLastValueForNameExprBeforeUsage() {
        // prepare
        final Expression mockedArgument = new NameExpr(argumentRange, new SimpleName("baseUrl"));
        final List<Expression> mockedPossibleValues = getPossibleValues();
        final Expression expectedValue = new StringLiteralExpr("correctValue");

        // execute
        final Expression actualValue = bindingResolver.findLastValueBeforeUsage(mockedPossibleValues, mockedArgument);

        // verify
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    public void testBindingResolverShouldFindLastValueForFieldExprBeforeUsage() {
        // prepare
        final Range mockedRange = new Range(new Position(30, 9), new Position(30, 50));
        final Expression mockedArgument = new FieldAccessExpr(mockedRange, new ThisExpr(),
                new NodeList<>(), new SimpleName("baseUrl"));
        final List<Expression> mockedPossibleValues = getPossibleValues();
        final Expression expectedValue = new StringLiteralExpr("correctValue");

        // execute
        final Expression actualValue = bindingResolver.findLastValueBeforeUsage(mockedPossibleValues, mockedArgument);

        // verify
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    public void testBindingResolverShouldReturnArgumentValueIfTheValueIsPresentInFields() {
        // prepare
        final String argumentName = "acceptNextAlert";
        final Expression expectedValue = new BooleanLiteralExpr(true);

        // execute
        Expression actualValue =  bindingResolver.findArgumentValueInFields(argumentName);

        // verify
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    public void testBindingResolverShouldReturnNullIfTheValueIsNotPresentInFields() {
        // prepare
        final String argumentName = "baseUrl";

        // execute
        Expression actualValue =  bindingResolver.findArgumentValueInFields(argumentName);

        // verify
        assertThat(actualValue).isNull();
    }

    @Test
    public void testBindingResolverShouldReturnArgumentValuesIfTheValueIsPresentInBeforeMethod() {
        // prepare
        final String argumentName = "baseUrl";
        final Expression expectedValue = new StringLiteralExpr("http://www.snowboard-zezula.cz/");
        final List<Expression> expectedValues = new LinkedList<>();
        expectedValues.add(expectedValue);

        // execute
        List<Expression> actualValues =  bindingResolver.findArgumentValuesInBeforeMethod(argumentName);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldReturnEmptyListIfTheValueIsNotPresentInBeforeMethod() {
        // prepare
        final String argumentName = "unknown";

        // execute
        List<Expression> actualValues =  bindingResolver.findArgumentValuesInBeforeMethod(argumentName);

        // verify
        assertThat(actualValues).isEmpty();
    }

    @Test
    public void testBindingResolverShouldReturnArgumentValuesForNameExprIfTheValueIsPresentInTestCase() {
        // prepare
        final String argumentName = "baseUrl";
        // only assign expression values
        final Expression expectedValue = new StringLiteralExpr("urlNameExpr2");
        final List<Expression> expectedValues = new LinkedList<>();
        expectedValues.add(expectedValue);

        // execute
        List<Expression> actualValues =  bindingResolver.findArgumentValuesInTestCase(argumentName, NameExpr.class);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldReturnArgumentValuesForNameExprIfTheValueIsNotPresentInTestCase() {
        // prepare
        final String argumentName = "unknown";

        // execute
        List<Expression> actualValues =  bindingResolver.findArgumentValuesInTestCase(argumentName, NameExpr.class);

        // verify
        assertThat(actualValues).isEmpty();
    }

    @Test
    public void testBindingResolverShouldReturnArgumentValuesForFieldExprIfTheValueIsPresentInTestCase() {
        // prepare
        final String argumentName = "baseUrl";
        final StringLiteralExpr expectedValue = new StringLiteralExpr("urlFieldExpr");
        final List<Expression> expectedValues = new LinkedList<>();
        expectedValues.add(expectedValue);

        // execute
        List<Expression> actualValues =  bindingResolver.findArgumentValuesInTestCase(argumentName, FieldAccessExpr.class);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldReturnArgumentValuesForFieldExprIfTheValueIsNotPresentInTestCase() {
        // prepare
        final String argumentName = "unknown";

        // execute
        List<Expression> actualValues =  bindingResolver.findArgumentValuesInTestCase(argumentName, NameExpr.class);

        // verify
        assertThat(actualValues).isEmpty();
    }

    @Test
    public void testBindingResolverShouldResolveLocalVariableBindingsByNameExprType() {
        // prepare
        final NameExpr argument = new NameExpr("baseUrl");
        final StringLiteralExpr expectedValue1 = new StringLiteralExpr("urlNameExpr2");
        final StringLiteralExpr expectedValue2 = new StringLiteralExpr("urlNameExpr1");
        final List<Expression> expectedValues = Arrays.asList(expectedValue1, expectedValue2);

        // execute
        List<Expression> actualValues =  bindingResolver.resolveLocalVariableBindingsByType(argument);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveLocalVariableBindingsByFieldExprType() {
        // prepare
        final FieldAccessExpr argument = new FieldAccessExpr(new ThisExpr(),"baseUrl");
        final StringLiteralExpr expectedValue1 = new StringLiteralExpr("urlFieldExpr");
        final List<Expression> expectedValues = Arrays.asList(expectedValue1);

        // execute
        List<Expression> actualValues =  bindingResolver.resolveLocalVariableBindingsByType(argument);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveLocalVariableBindingsByFieldExprType2() {
        // prepare
        final FieldAccessExpr argument = new FieldAccessExpr(new ThisExpr(),"acceptNextAlert");
        final BooleanLiteralExpr expectedValue1 = new BooleanLiteralExpr(true);
        final List<Expression> expectedValues = Arrays.asList(expectedValue1);

        // execute
        List<Expression> actualValues =  bindingResolver.resolveLocalVariableBindingsByType(argument);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveLocalVariableBindings1() {
        // prepare
        final Expression argument = new FieldAccessExpr(new ThisExpr(),"acceptNextAlert");
        final BooleanLiteralExpr expectedValue1 = new BooleanLiteralExpr(true);
        final List<Expression> expectedValues = Arrays.asList(expectedValue1);

        // execute
        List<Expression> actualValues = bindingResolver.resolveLocalVariableBindings(argument);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveLocalVariableBindings2() {
        // prepare
        final Expression argument = new NameExpr("baseUrl");
        final StringLiteralExpr expectedValue1 = new StringLiteralExpr("urlNameExpr2");
        final StringLiteralExpr expectedValue2 = new StringLiteralExpr("urlNameExpr1");
        final List<Expression> expectedValues = Arrays.asList(expectedValue1, expectedValue2);

        // execute
        List<Expression> actualValues = bindingResolver.resolveLocalVariableBindings(argument);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveGlobalVariableBindings() {
        // prepare
        final Expression argument = new NameExpr("baseUrl");
        final StringLiteralExpr expectedValue1 = new StringLiteralExpr("urlFieldExpr");
        final StringLiteralExpr expectedValue2 = new StringLiteralExpr("urlNameExpr2");
        final List<Expression> expectedValues = Arrays.asList(expectedValue1, expectedValue2);

        // execute
        List<Expression> actualValues = bindingResolver.resolveGlobalVariableBindings(argument);

        // verify
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveBindingsForNameExprBaseUrlArgument1() {
        // baseUrl declared in test case
        // prepare
        final Expression argument = new NameExpr(argumentRange, new SimpleName("baseUrl"));
        final Expression expectedValue = new StringLiteralExpr("urlNameExpr2");

        // execute
        Expression actualValue = bindingResolver.resolveBinding(argument);

        // verify
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    public void testBindingResolverShouldResolveBindingsForFieldExprBaseUrlArgument1() {
        // baseUrl declared in test case
        // prepare
        final Expression argument = new FieldAccessExpr(argumentRange, new ThisExpr(), new NodeList<>(),new SimpleName("baseUrl"));
        final Expression expectedValues = new StringLiteralExpr("urlFieldExpr");

        // execute
        Expression actualValue = bindingResolver.resolveBinding(argument);

        // verify
        assertThat(actualValue).isEqualTo(expectedValues);
    }

    @Test
    public void testBindingResolverShouldResolveBindingsForNameExprBaseUrlArgument2() {
        // baseUrl declared out of test case
        BlockStmt mockedTestMethod = TestMethod.getBlockStmt();
        // remove local declaration of baseUrl from testCase
        Statement statement = mockedTestMethod.getStatement(1);
        mockedTestMethod.remove(statement);
        bindingResolver.setTestCase(mockedTestMethod);

        // prepare
        final Expression argument = new NameExpr(argumentRange, new SimpleName("baseUrl"));
        final StringLiteralExpr expectedValue = new StringLiteralExpr("urlNameExpr2");

        // execute
        Expression actualValue = bindingResolver.resolveBinding(argument);

        // verify
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    @Test
    public void testBindingResolverShouldResolveBindingsForFieldExprBaseUrlArgument2() {
        // baseUrl declared out of test case
        BlockStmt mockedTestMethod = TestMethod.getBlockStmt();
        // remove local declaration of baseUrl from testCase
        Statement statement = mockedTestMethod.getStatement(1);
        mockedTestMethod.remove(statement);
        bindingResolver.setTestCase(mockedTestMethod);

        // prepare
        final Expression argument = new FieldAccessExpr(argumentRange, new ThisExpr(),
                new NodeList<>(),new SimpleName("baseUrl"));
        final Expression expectedValue = new StringLiteralExpr("urlNameExpr2");

        // execute
        Expression actualValue = bindingResolver.resolveBinding(argument);

        // verify
        assertThat(actualValue).isEqualTo(expectedValue);
    }

    private List<Expression> getPossibleValues() {
        // mocked ranges
        final Range mockedRange1 = new Range(new Position(27, 9), new Position(27, 50));
        final Range mockedRange2 = new Range(new Position(28, 9), new Position(28, 50));
        final Range mockedRange3 = new Range(new Position(20, 9), new Position(20, 50));
        final Range mockedRange4 = new Range(new Position(31, 9), new Position(31, 50));

        // mocked possible argument values
        final Expression value1 = new StringLiteralExpr(mockedRange1, "someValue1");
        final Expression value2 = new StringLiteralExpr(mockedRange2, "correctValue");
        final Expression value3 = new StringLiteralExpr(mockedRange3, "someValue3");
        final Expression value4 = new StringLiteralExpr(mockedRange4, "someValue4");

        return Arrays.asList(value1, value2, value3, value4);
    }
}