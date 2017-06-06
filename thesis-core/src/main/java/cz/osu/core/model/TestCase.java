package cz.osu.core.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class TestCase {

    private Queue<Statement> statements = new LinkedList<>();

    public TestCase() {}

    public TestCase(Queue statements) {
        this.statements = statements;
    }

    public Queue getMethods() {
        return statements;
    }

    public void add(Statement statement) {
        statements.add(statement);
    }
}
