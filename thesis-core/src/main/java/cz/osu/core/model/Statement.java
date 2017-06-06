package cz.osu.core.model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 22. 4. 2017.
 */
public class Statement {

    private Queue<Method> methods = new LinkedList<>();

    public Statement() {}

    public Statement(Queue methods) {
        this.methods = methods;
    }

    public Queue getMethods() {
        return methods;
    }

    public void add(Method method) {
        methods.add(method);
    }
}
