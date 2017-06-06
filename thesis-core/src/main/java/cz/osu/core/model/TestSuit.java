package cz.osu.core.model;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class TestSuit {

    private Queue<TestCase> testCases = new LinkedList<>();

    public TestSuit() {
    }

    public TestSuit(Queue<TestCase> testCases) {
        this.testCases = testCases;
    }

    public Queue<TestCase> getTestCases() {
        return testCases;
    }

    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
    }

    public TestCase getNextTestCase() {
        return testCases.poll();
    }

    public boolean hasNextTestCase() {
        return !testCases.isEmpty();
    }
}
