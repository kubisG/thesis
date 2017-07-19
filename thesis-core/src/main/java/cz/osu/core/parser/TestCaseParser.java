package cz.osu.core.parser;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.ExpressionStmt;

import cz.osu.core.enums.Action;
import cz.osu.core.enums.ExpressionType;
import cz.osu.core.enums.ScopeType;
import cz.osu.core.enums.WebDriverType;
import cz.osu.core.model.Method;
import cz.osu.core.model.Scope;
import cz.osu.core.model.Statement;
import cz.osu.core.model.TestCase;
import cz.osu.core.model.Variable;
import cz.osu.core.parser.resolver.BindingResolver;
import cz.osu.core.parser.resolver.ClassResolver;

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

    private boolean isTypeOfDriver(String scopeTypeName) {
        return Arrays.stream(WebDriverType.values())
                .anyMatch(driverType -> driverType.equals(scopeTypeName));
    }

    // TODO: 20. 6. 2017 replace hardcoded List with actions
    private boolean hasAction(Deque<Method> methods) {
        String methodName = methods.getLast().getName();

        return Arrays.stream(Action.values())
                .anyMatch(action -> action.equals(methodName));
    }

    private String getName(MethodCallExpr methodCallExpr) {
        return methodCallExpr.getNameAsString();
    }

    private Scope parseScope(String scopeExprName, ScopeType scopeType) {
        Scope scope = new Scope(scopeType);

        Class<?> scopeClass = classResolver.resolveExpressionClass(scopeExprName);
        scope.setScopeClass(scopeClass);

        return scope;
    }

    private Scope parseNameExprScope(NameExpr scopeExpr) {
        String scopeExprName = scopeExpr.getNameAsString();
        return parseScope(scopeExprName, ScopeType.CLASS);
    }

    private Scope parseObjectCreationExprScope(ObjectCreationExpr scopeExpr) {
        String scopeExprName = scopeExpr.getType().getNameAsString();

        if (isTypeOfDriver(scopeExprName)) {
            return parseScope(scopeExprName, ScopeType.DRIVER_INSTANCE);
        }
        return parseScope(scopeExprName, ScopeType.NEW_CLASS_INSTANCE);
    }

    private Scope parseLiteralExprScope(LiteralExpr scopeExpr) {
        Variable scopeVar = variableParser.parse(scopeExpr);
        return new Scope(ScopeType.NEW_CLASS_INSTANCE, scopeVar.getValue(), scopeVar.getType());
    }

    private Expression tryGetScopeExpr(MethodCallExpr methodCallExpr) {
        if (!methodCallExpr.getScope().isPresent()) {
            throw new UnsupportedOperationException("Method scope missing");
        }
        return methodCallExpr.getScope().get();
    }

    private Scope getScope(MethodCallExpr singleMethodCallExpr) {
        Scope scope;
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
                scope = new Scope(ScopeType.CLASS_INSTANCE);
                break;
            default:
                scope = parseLiteralExprScope((LiteralExpr) scopeExpr);
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
                Method param = parseNodeWithArguments((ObjectCreationExpr) argument);
                method.addParameter(new Statement(param));
                break;
            default:
                addLiteralParameter(method, argument);
        }
    }

    private void addMethodParameters(Method method, NodeWithArguments nodeWithArguments) {
        List<Expression> arguments = nodeWithArguments.getArguments();
        arguments.forEach(argument -> addMethodParameter(method, argument));
    }

    private List<NodeWithArguments> breakDownToSingleMethodCalls(MethodCallExpr methodCallExpr) {
        List<NodeWithArguments> singleMethodCallExprs = new LinkedList<>();
        Expression expression = methodCallExpr;

        while (expression instanceof MethodCallExpr) {
            MethodCallExpr tmpMethodCallExpr = (MethodCallExpr) expression;
            singleMethodCallExprs.add(tmpMethodCallExpr);
            expression = tmpMethodCallExpr.getScope().orElse(null);
        }
        // if the returned expression is instance of ObjectCreationExpr than add it too
        if (expression instanceof ObjectCreationExpr) {
            singleMethodCallExprs.add((ObjectCreationExpr)expression);
        }
        return singleMethodCallExprs;
    }

    private Method parseNodeWithArguments(NodeWithArguments nodeWithArguments) {
        Method method = new Method();

        if (nodeWithArguments instanceof ObjectCreationExpr) {
            method.setScope(parseObjectCreationExprScope((ObjectCreationExpr) nodeWithArguments));
        }
        else if (nodeWithArguments instanceof MethodCallExpr){
            method.setName(getName((MethodCallExpr) nodeWithArguments));
            method.setScope(getScope((MethodCallExpr) nodeWithArguments));
        } else
            throw new UnsupportedOperationException("Unable to parse node with arguments");

        addMethodParameters(method, nodeWithArguments);
        return method;
    }

    private Statement parseMethodCallExpr(MethodCallExpr methodCallExpr) {
        // resolve method call scope
        resolveScopeForMethodCallExpr(methodCallExpr);
        // break down whole method call to single ones
        List<NodeWithArguments> singleMethodCallExprs = breakDownToSingleMethodCalls(methodCallExpr);
        // parse each single method to Method and collect them into Statement class
        Deque<Method> methods = singleMethodCallExprs.stream()
                .map(singleMethodCallExpr -> parseNodeWithArguments(singleMethodCallExpr))
                .collect(LinkedList::new, LinkedList::addFirst, LinkedList::addAll);

        Boolean applyActionFlag = hasAction(methods);

        return new Statement(methods, applyActionFlag);
    }

    public TestCase parse(MethodDeclaration testCase) {
        // get only expression statements
        List<ExpressionStmt> statementExprs = testCase.getChildNodesByType(ExpressionStmt.class);
        // get only method call expressions
        List<MethodCallExpr> methodCallExprs = getExpressionsByType(statementExprs, MethodCallExpr.class);
        // parse method call expressions to statement and collect them into TestCase class
        Queue<Statement> statements = methodCallExprs.stream()
                .map(methodCallExpr -> parseMethodCallExpr(methodCallExpr))
                .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
        // get class of WebDriver instance
        String driverName = bindingResolver.resolveDriverNameBinding();
        // get name of test case
        String testCaseName = testCase.getNameAsString();

        return new TestCase(testCaseName, driverName, statements);
    }
}
