package cz.osu.core.model;

import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class TestCase {

    private final Queue methods;

    public TestCase(Queue methods) {
        this.methods = methods;
    }

    public Queue getMethods() {
        return methods;
    }
}
