package cz.osu.test;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

/**
 * Project: thesis
 * Created by Jakub on 20. 4. 2017.
 */
public class TestMethod {

    public static final MethodDeclaration TEST_METHOD = buildTestMethod();

    public static final BlockStmt TEST_METHOD_BODY = getBlockStmt();

    private static ExpressionStmt getFirstStmt() {
        final Range range = new Range(new Position(27, 9), new Position(27, 32));
        final ClassOrInterfaceType type = new ClassOrInterfaceType("String");
        final StringLiteralExpr initializer = new StringLiteralExpr("some");
        final SimpleName name = new SimpleName("baseUrl");

        final VariableDeclarator variable = new VariableDeclarator(range, type, name, initializer);
        final VariableDeclarationExpr expression = new VariableDeclarationExpr(variable);

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getSecondStmt() {
        final Range range = new Range(new Position(28, 9), new Position(28, 26));
        final NameExpr target = new NameExpr("baseUrl");
        final StringLiteralExpr value = new StringLiteralExpr("some2");
        final AssignExpr.Operator operator = AssignExpr.Operator.ASSIGN;
        final AssignExpr expression = new AssignExpr(range, target, value, operator);

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getThirdStmt() {
        final Range range = new Range(new Position(29, 9), new Position(29, 65));
        final MethodCallExpr expression = TestUtil.getMethodCallExpr(range, 
                new NameExpr("driver"), "get",
                new NameExpr("baseUrl"),
                new StringLiteralExpr("stringLiteral"),
                new CharLiteralExpr('c'),
                new DoubleLiteralExpr(2.0),
                new FieldAccessExpr(new ThisExpr(), "baseUrl")
        );

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getFourthStmt() {
        final Range range = new Range(new Position(30, 9), new Position(30, 64));
        // method type argument
        final MethodCallExpr secondMethod = TestUtil.getMethodCallExpr(range, new NameExpr("By"), "linkText", new StringLiteralExpr("StreetBundy"));

        // first method in chain
        final MethodCallExpr firstMethod = TestUtil.getMethodCallExpr(range, new NameExpr("driver"), "findElement", secondMethod);

        // last method in chain
        final MethodCallExpr expression = TestUtil.getMethodCallExpr(range, firstMethod, "click");

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getFifthStmt() {
        final Range range = new Range(new Position(31, 9), new Position(31, 68));
        // method type argument
        final MethodCallExpr secondMethod = TestUtil.getMethodCallExpr(range, new NameExpr("By"), "id", new StringLiteralExpr("productbox-63065-image"));

        // first method in chain
        final MethodCallExpr firstMethod = TestUtil.getMethodCallExpr(range, new NameExpr("driver"), "findElement", secondMethod);

        // last method in chain
        final MethodCallExpr expression = TestUtil.getMethodCallExpr(range, firstMethod, "click");

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getSixthStmt() {
        final Range range = new Range(new Position(32, 9), new Position(32, 103));
        // method type argument
        final MethodCallExpr secondMethod = TestUtil.getMethodCallExpr(range, new NameExpr("By"), "id", new StringLiteralExpr("product-size-select"));

        // first method in chain
        final ObjectCreationExpr firstMethod = TestUtil.getObjectCreationExpr(range, null, "Select", secondMethod);

        // last method in chain
        final MethodCallExpr expression = TestUtil.getMethodCallExpr(range, firstMethod, "selectByVisibleText", new StringLiteralExpr("M, skladem"));

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getSeventhStmt() {
        final Range range = new Range(new Position(33, 9), new Position(33, 64));
        // method type argument
        final MethodCallExpr secondMethod = TestUtil.getMethodCallExpr(range, new NameExpr("By"), "id", new StringLiteralExpr("frmcartAddForm-add"));

        // first method in chain
        final MethodCallExpr firstMethod = TestUtil.getMethodCallExpr(range, new NameExpr("driver"), "findElement", secondMethod);

        // last method in chain
        final MethodCallExpr expression = TestUtil.getMethodCallExpr(range, firstMethod, "click");

        return new ExpressionStmt(expression);
    }

    private static IfStmt getEighthStmt() {
        // final Range range = new Range(new Position(33, 9), new Position(33, 64));

        return new IfStmt();
    }

    public static BlockStmt getBlockStmt() {
        final BlockStmt body = new BlockStmt();

        // set statements
        final NodeList<Statement> statements = new NodeList<>();
        statements.add(getFirstStmt());
        statements.add(getSecondStmt());
        statements.add(getThirdStmt());
        statements.add(getFourthStmt());
        statements.add(getFifthStmt());
        statements.add(getSixthStmt());
        statements.add(getSeventhStmt());
        statements.add(getSeventhStmt());
        statements.add(getEighthStmt());
        body.setStatements(statements);

        // set range
        Range range = new Range(new Position(26, 47), new Position(34, 5));
        body.setRange(range);

        return body;
    }

    private static MethodDeclaration buildTestMethod() {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        NodeList<AnnotationExpr> annotations = new NodeList<>();

        annotations.add(new MarkerAnnotationExpr("Test"));

        methodDeclaration.setBody(getBlockStmt());
        methodDeclaration.setName("testZezula");
        methodDeclaration.setAnnotations(annotations);

        return methodDeclaration;
    }
}
