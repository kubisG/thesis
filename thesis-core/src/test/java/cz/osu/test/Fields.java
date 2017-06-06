package cz.osu.test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Project: thesis
 * Created by Jakub on 21. 4. 2017.
 */
public class Fields {

    public static final List<FieldDeclaration> FIELDS = buildFields();

    private static FieldDeclaration getFirstField() {
        final Range range = new Range(new Position(13, 5), new Position(13, 29));

        return getFieldDeclaration(range, "WebDriver", "driver", null);
    }

    private static FieldDeclaration getSecondField() {
        final Range range = new Range(new Position(14, 5), new Position(14, 27));

        return getFieldDeclaration(range, "String", "baseUrl", null);
    }

    private static FieldDeclaration getThirdField() {
        final Range range = new Range(new Position(15, 5), new Position(15, 43));

        return getFieldDeclaration(range, "boolean", "acceptNextAlert", new BooleanLiteralExpr(true));
    }

    private static FieldDeclaration getFourthField() {
        final Range range = new Range(new Position(16, 5), new Position(16, 65));

        return getFieldDeclaration(range, "StringBuffer", "verificationErrors", TestUtil.getObjectCreationExpr(range, null, "StringBuffer"));
    }

    private static FieldDeclaration getFieldDeclaration(Range range, String strType, String strName, Expression initializer) {
        final ClassOrInterfaceType type = new ClassOrInterfaceType(strType);
        final SimpleName name = new SimpleName(strName);

        final VariableDeclarator variable = new VariableDeclarator(range, type, name, initializer);
        NodeList<VariableDeclarator> variables = new NodeList<>();
        variables.add(variable);

        return new FieldDeclaration(range, EnumSet.noneOf(Modifier.class), new NodeList<>(), variables);
    }

    private static List<FieldDeclaration> buildFields() {
        List<FieldDeclaration> fields = new ArrayList<>();

        fields.add(getFirstField());
        fields.add(getSecondField());
        fields.add(getThirdField());
        fields.add(getFourthField());

        return fields;
    }
}
