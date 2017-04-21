package cz.osu.core;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import cz.osu.core.model.Variable;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Project: thesis
 * Created by Jakub on 20. 4. 2017.
 */
@Component
public class VariableParser {

    private MethodDeclaration beforeMethod;

    private MethodDeclaration beforeClassMethod;

    private List<FieldDeclaration> fields;

    public void setBeforeMethod(MethodDeclaration beforeMethod) {
        this.beforeMethod = beforeMethod;
    }

    public void setBeforeClassMethod(MethodDeclaration beforeClassMethod) {
        this.beforeClassMethod = beforeClassMethod;
    }

    public void setFields(List<FieldDeclaration> fields) {
        this.fields = fields;
    }

    public Variable parseExpressionToVariable(Expression argument) {
        if (argument instanceof LiteralExpr) {
            return parseLiteralExprToVariable((LiteralExpr) argument);
        } else if (argument instanceof NameExpr) {
            return parseNameOrFieldExprToVariable();
        } else if (argument instanceof BinaryExpr) {

        } else if (argument instanceof FieldAccessExpr) {

        }
        throw new UnsupportedOperationException("Unable to parse expression argument");
    }

    private boolean isNameOrFiledExpr(Expression argument) {
        return (argument instanceof NameExpr) || (argument instanceof FieldAccessExpr);
    }
    // TODO: 19. 4. 2017 bude volat resolve variable binding
    private Variable parseNameOrFieldExprToVariable() {
        return null;
    }

    // TODO: 19. 4. 2017 bude volat parseExpressionToVariable cili bude rekuzivni
    private Variable parseBinaryExprToVariable(BinaryExpr argument) {
        return null;
    }

    private Variable parseLiteralExprToVariable(LiteralExpr argument) {
        if (argument instanceof NullLiteralExpr) {
            return new Variable(null, null);
        } else if (argument instanceof BooleanLiteralExpr) {
            Boolean value = ((BooleanLiteralExpr) argument).getValue();
            return new Variable(value, Boolean.class);
        }
        return parseLiteralValueExprToVariable(((LiteralStringValueExpr) argument));
    }

    private Variable parseLiteralValueExprToVariable(LiteralStringValueExpr argument) {
        if (argument instanceof CharLiteralExpr) {
            Character value = argument.getValue().charAt(0);
            return new Variable(value, Character.class);
        } else if (argument instanceof DoubleLiteralExpr) {
            Double value = Double.valueOf(argument.getValue());
            return new Variable(value, Double.class);
        } else if (argument instanceof IntegerLiteralExpr) {
            Integer value = Integer.valueOf(argument.getValue());
            return new Variable(value, Integer.class);
        } else if (argument instanceof  LongLiteralExpr) {
            Long value = Long.valueOf(argument.getValue());
            return new Variable(value, Long.class);
        }
        return new Variable(argument.getValue(), String.class);
    }

}
