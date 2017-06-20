package cz.osu.core.model;

import org.openqa.selenium.WebDriver;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class TestCase {

    private final String driverName;

    private final Queue<Statement> statements;

    public TestCase(String driverName, Queue<Statement> statements) {
        this.driverName = driverName;
        this.statements = statements;
    }

    public String getDriverName() {
        return driverName;
    }

    public Queue<Statement> getStatements() {
        return statements;
    }

    public boolean hasNextStatement() {
        return !statements.isEmpty();
    }

    public Statement getNextStatement() {
        return statements.peek();
    }

    public Statement removeNextStatement() {
        return statements.poll();
    }
}
