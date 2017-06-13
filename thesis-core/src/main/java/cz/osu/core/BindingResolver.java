package cz.osu.core;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.Position;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.nodeTypes.NodeWithVariables;
import com.github.javaparser.ast.stmt.BlockStmt;

import cz.osu.core.util.ExpressionComparator;

/**
 * Project: thesis
 * Created by Jakub on 23. 4. 2017.
 */

@Component
public class BindingResolver {

    private BlockStmt testCase;

    private BlockStmt beforeMethod;

    private BlockStmt beforeClassMethod;

    private List<FieldDeclaration> fields;

    public void setTestCase(BlockStmt testCase) {
        this.testCase = testCase;
    }

    public void setBeforeMethod(BlockStmt beforeMethod) {
        this.beforeMethod = beforeMethod;
    }

    public void setBeforeClassMethod(BlockStmt beforeClassMethod) {
        this.beforeClassMethod = beforeClassMethod;
    }

    public void setFields(List<FieldDeclaration> fields) {
        this.fields = fields;
    }

    private <T extends Node> Expression getArgumentValue(NodeWithVariables<T> nodeWithVariables) {
        Expression value = null;
        // all variables has the same initialization value e.g. int a, b, c = 10;
        Optional<Expression> optionalValue = nodeWithVariables.getVariables().get(0).getInitializer();

        if (optionalValue.isPresent()) {
            value = optionalValue.get();
        }
        return value;
    }

    private <V extends Node, T extends NodeWithVariables<V>> List<T> filterByArgumentName(List<T> nodeWithVariables, String argumentName) {
        return nodeWithVariables.stream()
                .filter(node -> node.getVariables().stream()
                        .map(variable -> variable.getNameAsString())
                        .anyMatch(variableName -> variableName.equals(argumentName)))
                .collect(Collectors.toList());
    }

    private String getTargetName(AssignExpr assignExpression) {
        String targetName = null;
        Expression target = assignExpression.getTarget();

        if (target instanceof NodeWithSimpleName) {
            targetName = ((NodeWithSimpleName) target).getName().getIdentifier();
        }
        return targetName;
    }

    private List<AssignExpr> filterAssignExprByArgumentName(List<AssignExpr> assignExpressions, String argumentName) {
        return assignExpressions.stream()
                .filter(assignExpr -> argumentName.equals(getTargetName(assignExpr)))
                .collect(Collectors.toList());
    }

    private List<Expression> findArgumentValuesInAssignExpressionsByType(List<AssignExpr> assignExpressions, Class<?> type, String argumentName){
        return filterAssignExprByArgumentName(assignExpressions, argumentName).stream()
                .filter(assignExpr -> type.isInstance(assignExpr.getTarget()))
                .map(assignExpr -> assignExpr.getValue())
                .collect(Collectors.toList());
    }

    private List<Expression> findArgumentValuesInAssignExpressions(List<AssignExpr> assignExpressions, String argumentName){
        return filterAssignExprByArgumentName(assignExpressions, argumentName).stream()
                .map(assignExpr -> assignExpr.getValue())
                .collect(Collectors.toList());
    }

    private Expression findArgumentValueInVariableDeclarationExpressions(List<VariableDeclarationExpr> variableExpressions, String argumentName) {
        Expression value = null;
        Optional<VariableDeclarationExpr> optionalVariableExpr = filterByArgumentName(variableExpressions, argumentName).stream().findAny();

        if (optionalVariableExpr.isPresent()) {
            value = getArgumentValue(optionalVariableExpr.get());
        }
        return value;
    }

    boolean isLocalVariable(Expression argument) {
        String argumentName = ((NodeWithSimpleName) argument).getNameAsString();
        List<VariableDeclarationExpr> variableDeclarationExprs = testCase.getChildNodesByType(VariableDeclarationExpr.class);

        Optional<VariableDeclarationExpr> optionalVariableExpr = filterByArgumentName(variableDeclarationExprs, argumentName).stream().findAny();

        return optionalVariableExpr.isPresent();
    }

    private boolean isNameOrFieldAccessExpr(Expression argument) {
        return argument instanceof NameExpr || argument instanceof FieldAccessExpr;
    }

    Expression findLastValueBeforeUsage(List<Expression> possibleValues, Expression argument) {
        if (!argument.getRange().isPresent()) {
            throw new IllegalStateException("cannot resolve argument binding");
        }

        Position argumentPosition = argument.getRange().get().end;

        Optional<Expression> possibleArgValue = possibleValues.stream()
                .filter(possibleValue -> possibleValue.getRange().isPresent())
                .filter(value -> value.getRange().get().isBefore(argumentPosition))
                .max(new ExpressionComparator());

        return possibleArgValue.orElse(null);
    }

    Expression findArgumentValueInFields(String argumentName) {
        Expression value = null;
        Optional<FieldDeclaration> optionalField = filterByArgumentName(fields, argumentName).stream().findAny();

        if (optionalField.isPresent()) {
            value = getArgumentValue(optionalField.get());
        }
        return value;
    }

    List<Expression> findArgumentValuesInBeforeMethod(String argumentName){
        List<AssignExpr> assignExprs = beforeMethod.getChildNodesByType(AssignExpr.class);
        return findArgumentValuesInAssignExpressions(assignExprs, argumentName);
    }

    List<Expression> findArgumentValuesInTestCase(String argumentName) {
        List<AssignExpr> assignExprs = testCase.getChildNodesByType(AssignExpr.class);
        return findArgumentValuesInAssignExpressions(assignExprs, argumentName);
    }

    List<Expression> findArgumentValuesInTestCase(String argumentName, Class<?> type) {
        List<AssignExpr> assignExprs = testCase.getChildNodesByType(AssignExpr.class);

        if (type.isAssignableFrom(NameExpr.class)) {
            return findArgumentValuesInAssignExpressionsByType(assignExprs, NameExpr.class, argumentName);
        } else {
            return findArgumentValuesInAssignExpressionsByType(assignExprs, FieldAccessExpr.class, argumentName);
        }
    }

    List<Expression> resolveLocalVariableBindingsByType(NameExpr argument) {
        String argumentName = argument.getNameAsString();
        List<VariableDeclarationExpr> variableDeclarationExprs = testCase.getChildNodesByType(VariableDeclarationExpr.class);
        Expression variableExprValue = findArgumentValueInVariableDeclarationExpressions(variableDeclarationExprs, argumentName);
        List<Expression> assignExprValues = findArgumentValuesInTestCase(argumentName, NameExpr.class);

        if (variableExprValue != null) {
            assignExprValues.add(variableExprValue);
        }
        return assignExprValues;
    }

    List<Expression> resolveLocalVariableBindingsByType(FieldAccessExpr argument) {
        String argumentName = argument.getNameAsString();
        List<Expression> argumentValues = new LinkedList<>();
        Expression valueInFields = findArgumentValueInFields(argumentName);
        List<Expression> valuesInBeforeMethod = findArgumentValuesInBeforeMethod(argumentName);
        List<Expression> valuesInTestCase = findArgumentValuesInTestCase(argumentName, FieldAccessExpr.class);

        if (!valuesInTestCase.isEmpty()) {
            argumentValues.addAll(valuesInTestCase);
        } else if (!valuesInBeforeMethod.isEmpty()) {
            argumentValues.addAll(valuesInBeforeMethod);
        } else if (valueInFields != null) {
            argumentValues.add(valueInFields);
        }
        return argumentValues;
    }

    List<Expression> resolveLocalVariableBindings(Expression argument) {
        if (argument instanceof NameExpr) {
            return resolveLocalVariableBindingsByType((NameExpr) argument);
        }
        else {
            return resolveLocalVariableBindingsByType((FieldAccessExpr) argument);
        }
    }

    List<Expression> resolveGlobalVariableBindings(Expression argument) {
        String argumentName = ((NodeWithSimpleName) argument).getNameAsString();
        List<Expression> argumentValues = new LinkedList<>();
        Expression valueInFields = findArgumentValueInFields(argumentName);
        List<Expression> valuesInBeforeMethod = findArgumentValuesInBeforeMethod(argumentName);
        List<Expression> valuesInTestCase = findArgumentValuesInTestCase(argumentName);

        if (!valuesInTestCase.isEmpty()) {
            argumentValues.addAll(valuesInTestCase);
        } else if (!valuesInBeforeMethod.isEmpty()) {
            argumentValues.addAll(valuesInBeforeMethod);
        } else if (valueInFields != null) {
            argumentValues.add(valueInFields);
        }
        return argumentValues;
    }

    Expression resolveBinding(Expression argument){
        boolean localVariable = isLocalVariable(argument);
        List<Expression> argumentValues;

        if (localVariable) {
            argumentValues =  resolveLocalVariableBindings(argument);
        } else {
            argumentValues = resolveGlobalVariableBindings(argument);
        }
        return findLastValueBeforeUsage(argumentValues, argument);
    }

    public Expression resolveExpressionBinding(Expression expression) {
        Expression resolvedScopeExpression = expression;

        while (isNameOrFieldAccessExpr(resolvedScopeExpression)) {
            Expression tmpExpr =  resolveBinding(resolvedScopeExpression);
            if (tmpExpr == null) {
                return resolvedScopeExpression;
            }
            resolvedScopeExpression = tmpExpr;
        }
        return resolvedScopeExpression;
    }
}
