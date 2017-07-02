package cz.osu.core.model;

import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class TestCase {

    private final String name;

    private final String driverName;

    private final Queue<Statement> statements;

    public TestCase(String name, String driverName, Queue<Statement> statements) {
        this.name = name;
        this.driverName = driverName;
        this.statements = statements;
    }

    public String getName() {
        return name;
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
