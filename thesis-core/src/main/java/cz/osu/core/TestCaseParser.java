package cz.osu.core;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.nodeTypes.NodeWithOptionalScope;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

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

    private Scope parseScope(String scopeExprName, ScopeType scopeType) {
        Scope scope = new Scope(scopeType);

        Class<?> scopeClass = bindingResolver.resolveScopeClass(scopeExprName);
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

    private Scope parseFieldAccessExprScope(FieldAccessExpr scopeExpr) {
        String scopeExprName = scopeExpr.getNameAsString();
        return parseScope(scopeExprName, ScopeType.CLASS);
    }

    private Scope getScope(MethodCallExpr expression) {
        if (!expression.getScope().isPresent()) {
            throw new UnsupportedOperationException("Method scope missing");
        }
        Scope scope = null;
        Expression scopeExpr = expression.getScope().get();
        String scopeClassName = scopeExpr.getClass().getSimpleName();

        switch (scopeClassName) {
            case "MethodCallExpr":
                scope = new Scope(ScopeType.CLASS_INSTANCE);
                break;
            case "NameExpr":
                scope = parseNameExprScope((NameExpr) scopeExpr);
                break;
            case "FieldAccessExpr":
                scope = parseFieldAccessExprScope((FieldAccessExpr) scopeExpr);
                break;
            case "ObjectCreationExpr":
                scope = parseObjectCreationExprScope((ObjectCreationExpr) scopeExpr);
                break;
        }
        return scope;
    }

    private boolean isNameOrFieldAccessExpr(Expression argument) {
        return argument instanceof NameExpr || argument instanceof FieldAccessExpr;
    }

    private void addLiteralParameter(Method method, Expression argument) {
        Variable variable = variableParser.parse(argument);
        method.addParameter(variable);
    }

    private void addMethodCallParameter(Method method, MethodCallExpr argument) {
        Method methodTypeParam = new Method();

        resolveScopeForMethodCallExpr(argument);
        methodTypeParam.setName(getName(argument));
        methodTypeParam.setScope(getScope(argument));
        addMethodParameters(methodTypeParam, argument);
        method.addMethodTypeParameters(methodTypeParam);
    }

    private void addObjectCreationParameter(Method method, ObjectCreationExpr argument) {
        Method methodTypeParam = new Method();

        methodTypeParam.setName(getName(argument));
        methodTypeParam.setScope(parseObjectCreationExprScope(argument));
        addMethodParameters(methodTypeParam, argument);
        method.addMethodTypeParameters(methodTypeParam);
    }

    private void addMethodParameter(Method method, Expression argument) {
        String scopeClassName = argument.getClass().getSimpleName();

        switch (scopeClassName) {
            case "MethodCallExpr":
                addMethodCallParameter(method, (MethodCallExpr) argument);
                break;
            case "ObjectCreationExpr":
                addObjectCreationParameter(method, (ObjectCreationExpr) argument);
                break;
            case "NameExpr":
                addMethodParameter(method, bindingResolver.resolveBindings(argument));
                break;
            case "FieldAccessExpr":
                addMethodParameter(method, bindingResolver.resolveBindings(argument));
                break;
            default:
                addLiteralParameter(method, argument);
        }
    }

    private void addMethodParameters(Method method, NodeWithArguments nodeWithArguments) {
        List<Expression> arguments = nodeWithArguments.getArguments();
        arguments.forEach(argument -> addMethodParameter(method, argument));
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

    private void resolveScopeForMethodCallExpr(MethodCallExpr singleMethodCallExpr) {
        Expression scopeExpr = singleMethodCallExpr.getScope().orElse(null);

        if(scopeExpr == null) {
            throw new IllegalStateException("Method must have scope");
        }

        if (isNameOrFieldAccessExpr(scopeExpr)) {
            Expression resolvedScopeExpr = bindingResolver.resolveBindings(scopeExpr);
            if (resolvedScopeExpr == null) {
                singleMethodCallExpr.setScope(scopeExpr);
            } else if (resolvedScopeExpr instanceof ObjectCreationExpr) {
                singleMethodCallExpr.setScope(resolvedScopeExpr);
            } else if (resolvedScopeExpr instanceof MethodCallExpr) {
                singleMethodCallExpr.setScope(resolvedScopeExpr);
                resolveScopeForMethodCallExpr((MethodCallExpr) resolvedScopeExpr);
            }
        }
    }

    private void resolveScopeForMethodCallExprs(List<MethodCallExpr> singleMethodCallExprs) {
        singleMethodCallExprs
                .forEach(singleMethodCallExpr -> resolveScopeForMethodCallExpr(singleMethodCallExpr));
    }

    private Method parseSingleMethodCallExpr(MethodCallExpr singleMethodCall) {
        Method method = new Method();

        method.setName(getName(singleMethodCall));
        method.setScope(getScope(singleMethodCall));
        addMethodParameters(method, singleMethodCall);

        return method;
    }

    Statement parseMethodCallExpr(MethodCallExpr methodCallExpr) {
        Statement statement = new Statement();

        // break down whole method call to single ones
        List<MethodCallExpr> singleMethodCallExprs = breakDownToSingleMethodCalls(methodCallExpr);
        // resolve scope for each method call
        resolveScopeForMethodCallExprs(singleMethodCallExprs);
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
