package cz.osu.test;

import java.util.Arrays;

import com.github.javaparser.Range;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Project: thesis
 * Created by Jakub on 21. 4. 2017.
 */
class TestUtil {

    private static NodeList<Expression> getArguments(Expression... exprArguments){
        return Arrays.stream(exprArguments)
                .collect(NodeList::new, NodeList::add, NodeList::addAll);
    }

    static MethodCallExpr getMethodCallExpr(Range range, Expression scope, String strName, Expression... exprArguments){
        return new MethodCallExpr(range, scope, null, new SimpleName(strName), getArguments(exprArguments));
    }

    static ObjectCreationExpr getObjectCreationExpr(Range range, Expression scope, String strType, Expression... exprArguments){
        return new ObjectCreationExpr(range, scope, new ClassOrInterfaceType(strType), null, getArguments(exprArguments), null);
    }

    static AssignExpr getAssignExpr(String strTarget, Expression value) {
        final AssignExpr.Operator operator = AssignExpr.Operator.ASSIGN;
        return new AssignExpr(new NameExpr(strTarget), value, operator);
    }
}
