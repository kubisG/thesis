package cz.osu.core;

import org.springframework.stereotype.Component;
import sun.awt.image.ImageWatched;

import javax.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import cz.osu.core.model.Method;
import cz.osu.core.model.Statement;
import cz.osu.core.model.TestCase;
import cz.osu.core.model.Variable;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
@Component
public class TestCaseParser {

    private final VariableParser variableParser;

    private final BindingResolver bindingResolver;

    @Inject
    public TestCaseParser(VariableParser variableParser, BindingResolver bindingResolver) {
        this.variableParser = variableParser;
        this.bindingResolver = bindingResolver;
    }

    private <T extends Expression> List<T> getExpressionsByType(List<ExpressionStmt> statements, Class<T> type) {
        return statements.stream()
                .map(statement -> statement.getExpression())
                .filter(expression -> type.isInstance(expression))
                .map(expression -> (T) expression)
                .collect(Collectors.toList());
    }

    private String getName(Expression argument) {
        if (argument instanceof MethodCallExpr) {
            return ((MethodCallExpr) argument).getName().getIdentifier();
        }
        return null;
    }

    private String getType(Expression argument) {
        if (argument instanceof ObjectCreationExpr) {
            return ((ObjectCreationExpr) argument).getType().getNameAsString();
        }
        return null;
    }

    private boolean isNameOrFieldAccessExpr(Expression argument) {
        return argument instanceof NameExpr || argument instanceof FieldAccessExpr;
    }

    private void addMethodTypeParameter(Method method, Expression argument) {
        Method methodTypeParam = new Method();
        methodTypeParam.setName(getName(argument));
        methodTypeParam.setType(getType(argument));

        // recursive call
        setMethodParams(methodTypeParam, (NodeWithArguments) argument);
        method.addMethodTypeParameters(methodTypeParam);
    }

    private void addExpressionParameter(Method method, Expression argumentValue) {
        if (argumentValue instanceof NodeWithArguments) {
            addMethodTypeParameter(method, argumentValue);
        } else {
            Variable variable = variableParser.parse(argumentValue);
            method.addParameter(variable);
        }
    }

    private void setMethodParams(Method method, NodeWithArguments nodeWithArguments) {
        List<Expression> arguments = nodeWithArguments.getArguments();

        for (Expression argument: arguments) {
            if (argument instanceof NodeWithArguments) {
                // contains recursive call
                addMethodTypeParameter(method, argument);
            } else if (isNameOrFieldAccessExpr(argument)) {
                Expression argumentValue = bindingResolver.resolveBindings(argument);
                addExpressionParameter(method, argumentValue);
            } else {
                Variable variable = variableParser.parse(argument);
                method.addParameter(variable);
            }
        }
    }

    private boolean isArgumentOfAnotherMethod(List<MethodCallExpr> methodCalls, MethodCallExpr methodCallExpr) {
        return methodCalls.stream()
                .flatMap(methodCall -> methodCall.getArguments().stream())
                .filter(argument -> argument instanceof MethodCallExpr)
                .anyMatch(methodCallArg -> methodCallArg.equals(methodCallExpr));
    }

    private List<MethodCallExpr> breakDownToSingleMethodCalls(MethodCallExpr methodCallExpr) {
        List<MethodCallExpr> methodCalls = methodCallExpr.getChildNodesByType(MethodCallExpr.class);

        // add parent method call as well
        methodCalls.add(0, methodCallExpr);

        return methodCalls.stream()
                .filter(methodCall -> !isArgumentOfAnotherMethod(methodCalls, methodCall))
                .collect(Collectors.toList());
    }

    private Method parseSingleMethodCallExpr(MethodCallExpr singleMethodCall) {
        String methodName = singleMethodCall.getNameAsString();
        Method method = new Method();

        method.setName(methodName);
        setMethodParams(method, singleMethodCall);

        return method;
    }

    Statement parseMethodCallExpr(MethodCallExpr methodCallExpr) {
        Statement statement = new Statement();

        // break down whole method call to single ones
        List<MethodCallExpr> singleMethodCallExprs = breakDownToSingleMethodCalls(methodCallExpr);

        // parse each single method to Method and collect them into Statement class
        singleMethodCallExprs.stream()
                .map(singleMethodCallExpr -> parseSingleMethodCallExpr(singleMethodCallExpr))
                .forEach(statement::add);

        return statement;
    }

    public TestCase parse(BlockStmt testBody) {
        TestCase testCase = new TestCase();

        // get only expression statements
        List<ExpressionStmt> statements = testBody.getChildNodesByType(ExpressionStmt.class);

        // get only method call expressions
        List<MethodCallExpr> methodCallExprs = getExpressionsByType(statements, MethodCallExpr.class);

        // parse method call expressions to statement and collect them into TestCase class
        methodCallExprs.stream()
                .map(methodCallExpr -> parseMethodCallExpr(methodCallExpr))
                .forEach(testCase::add);

        return testCase;
    }

}
