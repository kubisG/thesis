package cz.osu.core;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import cz.osu.core.model.Method;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */

@Component
public class TestCaseParser {

    private VariableParser variableParser;

    private MethodDeclaration testCase;

    public void setVariableParser(VariableParser variableParser) {
        this.variableParser = variableParser;
    }

    public void setTestCase(MethodDeclaration testCase) {
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

    private void setMethodParams(Method method, NodeWithArguments nodeWithArguments) {
        List<Expression> arguments = nodeWithArguments.getArguments();

        for (Expression argument: arguments) {
            if (argument instanceof NodeWithArguments) {
                Method methodTypeParam = new Method();
                methodTypeParam.setName(getName(argument));
                methodTypeParam.setType(getType(argument));
                // recursive call
                setMethodParams(methodTypeParam, (NodeWithArguments) argument);
                method.addMethodTypeParameters(methodTypeParam);
            } else {
                method.addParameter(argument);
                method.addParameterType(argument.getClass());
            }
        }
    }

    Method parseMethodCallExprToMethod(MethodCallExpr methodCallExpr) {
        String methodName = methodCallExpr.getName().getIdentifier();
        Method method = new Method();

        method.setName(methodName);
        setMethodParams(method, methodCallExpr);

        return method;
    }
}
