package cz.osu.core.parser;

import org.springframework.stereotype.Component;

import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.LiteralStringValueExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;

import cz.osu.core.model.Variable;

/**
 * Project: thesis
 * Created by Jakub on 20. 4. 2017.
 */
@Component
public class VariableParser {

    private Variable parseLiteralValueExpr(LiteralStringValueExpr argument) {
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

    private Variable parseLiteralExpr(LiteralExpr argument) {
        if (argument instanceof NullLiteralExpr) {
            return new Variable(null, null);
        } else if (argument instanceof BooleanLiteralExpr) {
            Boolean value = ((BooleanLiteralExpr) argument).getValue();
            return new Variable(value, Boolean.class);
        }
        return parseLiteralValueExpr((LiteralStringValueExpr) argument);
    }

    public Variable parse(Expression argument) {
        if (argument instanceof LiteralExpr) {
            return parseLiteralExpr((LiteralExpr) argument);
        }
        throw new UnsupportedOperationException("Unable to parse expression argument");
    }
}
