package cz.osu.core;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import cz.osu.core.enums.ExpressionType;
import cz.osu.core.enums.ScopeType;
import cz.osu.core.model.Method;
import cz.osu.core.model.Scope;
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

    private final ClassResolver classResolver;

    @Inject
    public TestCaseParser(VariableParser variableParser, BindingResolver bindingResolver, ClassResolver classResolver) {
        this.variableParser = variableParser;
        this.bindingResolver = bindingResolver;
        this.classResolver = classResolver;
    }

    private <T extends Expression> List<T> getExpressionsByType(List<ExpressionStmt> statements, Class<T> type) {
        return statements.stream()
                .map(statement -> statement.getExpression())
                .filter(expression -> type.isInstance(expression))
                .map(expression -> (T) expression)
                .collect(Collectors.toList());
    }

    private Scope parseScope(String scopeExprName, ScopeType scopeType) {
        Scope scope = new Scope(scopeType);

        Class<?> scopeClass = classResolver.resolveScopeClass(scopeExprName);
        scope.setScopeClass(scopeClass);

        return scope;
    }

    private Scope parseNameExprScope(NameExpr scopeExpr) {
        String scopeExprName = scopeExpr.getNameAsString();
        return parseScope(scopeExprName, ScopeType.CLASS);
    }

    private Scope parseObjectCreationExprScope(ObjectCreationExpr scopeExpr) {
        String scopeExprName = scopeExpr.getType().getNameAsString();
        return parseScope(scopeExprName, ScopeType.NEW_CLASS_INSTANCE);
    }

    private Expression tryGetScopeExpr(MethodCallExpr methodCallExpr) {
        if (!methodCallExpr.getScope().isPresent()) {
            throw new UnsupportedOperationException("Method scope missing");
        }
        return methodCallExpr.getScope().get();
    }

    private Scope getScope(MethodCallExpr singleMethodCallExpr) {
        Scope scope = null;
        Expression scopeExpr = tryGetScopeExpr(singleMethodCallExpr);
        String scopeClassName = scopeExpr.getClass().getSimpleName();

        switch (scopeClassName) {
            case "MethodCallExpr":
                scope = new Scope(ScopeType.CLASS_INSTANCE);
                break;
            case "NameExpr":
                scope = parseNameExprScope((NameExpr) scopeExpr);
                break;
            case "ObjectCreationExpr":
                scope = parseObjectCreationExprScope((ObjectCreationExpr) scopeExpr);
                break;
        }
        return scope;
    }

    private void resolveScopeForMethodCallExpr(MethodCallExpr singleMethodCallExpr) {
        Expression scopeExpr = tryGetScopeExpr(singleMethodCallExpr);
        scopeExpr = bindingResolver.resolveExpressionBinding(scopeExpr);
        String className = scopeExpr.getClass().getSimpleName();

        if (ExpressionType.METHOD_CALL.equals(className)) {
            singleMethodCallExpr.setScope(scopeExpr);
            resolveScopeForMethodCallExpr((MethodCallExpr) scopeExpr);
        } else {
            singleMethodCallExpr.setScope(scopeExpr);
        }
    }

    private Scope parseScope(MethodCallExpr singleMethodCallExpr) {
        // resolve method scope expression binding
        resolveScopeForMethodCallExpr(singleMethodCallExpr);
        // parse and get method scope expression
        return getScope(singleMethodCallExpr);
    }

    private void addLiteralParameter(Method method, Expression argument) {
        Variable variable = variableParser.parse(argument);
        method.addParameter(variable);
    }

    private void addMethodParameter(Method method, Expression argument) {
        argument = bindingResolver.resolveExpressionBinding(argument);
        String parameterClassName = argument.getClass().getSimpleName();
        Statement statement;

        switch (parameterClassName) {
            case "MethodCallExpr":
                statement = parseMethodCallExpr((MethodCallExpr) argument);
                method.addParameter(statement);
                break;
            case "ObjectCreationExpr":
                statement = parseObjectCreationExpr((ObjectCreationExpr) argument);
                method.addParameter(statement);
                break;
            default:
                addLiteralParameter(method, argument);
        }
    }

    private void addMethodParameters(Method method, NodeWithArguments nodeWithArguments) {
        List<Expression> arguments = nodeWithArguments.getArguments();
        arguments.forEach(argument -> addMethodParameter(method, argument));
    }

    private List<MethodCallExpr> breakDownToSingleMethodCalls(MethodCallExpr methodCallExpr) {
        List<MethodCallExpr> singleMethodCallExprs = new LinkedList<>();
        Expression expression = methodCallExpr;

        while (expression instanceof MethodCallExpr) {
            MethodCallExpr tmpMethodCallExpr = (MethodCallExpr) expression;
            singleMethodCallExprs.add(tmpMethodCallExpr);
            expression = (tmpMethodCallExpr).getScope().orElse(null);
        }
        return singleMethodCallExprs;
    }

    private Statement parseObjectCreationExpr(ObjectCreationExpr objectCreationExpr) {
        Statement statement = new Statement();
        Method method = new Method();

        // set object creation method
        method.setScope(parseObjectCreationExprScope(objectCreationExpr));
        addMethodParameters(method, objectCreationExpr);
        // set statement
        statement.add(method);

        return statement;
    }

    private Method parseSingleMethodCallExpr(MethodCallExpr singleMethodCall) {
        Method method = new Method();

        method.setName(singleMethodCall.getNameAsString());
        method.setScope(parseScope(singleMethodCall));
        addMethodParameters(method, singleMethodCall);

        return method;
    }

    Statement parseMethodCallExpr(MethodCallExpr methodCallExpr) {
        Statement statement = new Statement();
        // resolve method call scope
        resolveScopeForMethodCallExpr(methodCallExpr);
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
