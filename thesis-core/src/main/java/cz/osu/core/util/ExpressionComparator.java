package cz.osu.core.util;

import java.util.Comparator;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.expr.Expression;


/**
 * Project: thesis
 * Created by Jakub on 23. 4. 2017.
 */
public class ExpressionComparator implements Comparator<Expression> {

    @Override
    public int compare(Expression ex1, Expression ex2) {
        if (hasRange(ex1) && hasRange(ex2)) {
            Position firstPosition = ex1.getRange().get().end;
            Position secondPosition = ex2.getRange().get().end;
            return firstPosition.compareTo(secondPosition);
        } else if (hasRange(ex1) && !hasRange(ex2)) {
            return 1;
        } else if (!hasRange(ex1) && hasRange(ex2)) {
            return -1;
        }
        return 0;
    }

    private boolean hasRange(Expression expression) {
        return expression.getRange().isPresent();
    }
}
