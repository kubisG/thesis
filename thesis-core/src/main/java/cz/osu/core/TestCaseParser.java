package cz.osu.core;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import cz.osu.core.model.Method;
import cz.osu.core.model.TestCase;
import cz.osu.core.model.Variable;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.List;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */

@Component
public class TestCaseParser {

    @Inject
    private VariableParser variableParser;

    @Inject
    private BindingResolver bindingResolver;

    private BlockStmt testCase;

    public void setTestCase(BlockStmt testCase) {
        this.testCase = testCase;
    }

    List<MethodCallExpr> filterMethodCallExpr(BlockStmt methodBody) {
        return methodBody.getChildNodesByType(MethodCallExpr.class);
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

    private void addExpressionParameter(Method method, Expression argument, Expression argumentValue) {
        if (argumentValue instanceof NodeWithArguments) {
            addMethodTypeParameter(method, argumentValue);
        } else {
            Variable variable = variableParser.parse(argument);
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
                addExpressionParameter(method, argument, argumentValue);
            } else {
                Variable variable = variableParser.parse(argument);
                method.addParameter(variable);
            }
        }
    }

    Method parseMethodCallExpr(MethodCallExpr methodCallExpr) {
        String methodName = methodCallExpr.getName().getIdentifier();
        Method method = new Method();

        method.setName(methodName);
        setMethodParams(method, methodCallExpr);

        return method;
    }

    TestCase parseTestCase() {
        bindingResolver.setTestCase(testCase);
        return null;
    }

}
