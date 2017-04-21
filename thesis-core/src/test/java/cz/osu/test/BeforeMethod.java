package cz.osu.test;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

/**
 * Project: thesis
 * Created by Jakub on 21. 4. 2017.
 */
public class BeforeMethod {

    public static final MethodDeclaration BEFORE_METHOD = buildBeforeMethod();

    private static ExpressionStmt getFirstExpressionStmt(){
        Range range = new Range(new Position(20, 9), new Position(20, 37));
        final AssignExpr expression = TestUtil.getAssignExpr("driver", TestUtil.getObjectCreationExpr(range, null, "FirefoxDriver"));

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getSecondExpressionStmt(){
        Range range = new Range(new Position(21, 9), new Position(21, 52));
        final AssignExpr expression = TestUtil.getAssignExpr("baseUrl", new StringLiteralExpr("http://www.snowboard-zezula.cz/"));

        return new ExpressionStmt(expression);
    }

    private static ExpressionStmt getThirdExpressionStmt(){
        Range range = new Range(new Position(22, 9), new Position(22, 71));

        // build method call chain
        final MethodCallExpr firstMethodCall = TestUtil.getMethodCallExpr(range, new NameExpr("driver"), "manage");
        final MethodCallExpr secondMethodCall = TestUtil.getMethodCallExpr(range, firstMethodCall, "timeouts");

        final MethodCallExpr expression = TestUtil.getMethodCallExpr(range, secondMethodCall, "implicitlyWait", new IntegerLiteralExpr(30),
                new FieldAccessExpr(new NameExpr("TimeUnit"), "SECONDS"));

        return new ExpressionStmt(expression);
    }

    private static NodeList<Statement> getStatements() {
        NodeList<Statement> statements = new NodeList<>();

        statements.add(getFirstExpressionStmt());
        statements.add(getSecondExpressionStmt());
        statements.add(getThirdExpressionStmt());

        return statements;
    }

    public static BlockStmt getBlockStmt() {
        final BlockStmt body = new BlockStmt();

        // set statements
        body.setStatements(getStatements());

        // set range
        Range range = new Range(new Position(19, 42), new Position(23, 5));
        body.setRange(range);

        return body;
    }

    private static MethodDeclaration buildBeforeMethod() {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        NodeList<AnnotationExpr> annotations = new NodeList<>();

        annotations.add(new MarkerAnnotationExpr("Before"));

        methodDeclaration.setBody(getBlockStmt());
        methodDeclaration.setName("setUp");
        methodDeclaration.setAnnotations(annotations);

        return methodDeclaration;
    }
}
