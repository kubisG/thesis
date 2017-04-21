package cz.osu.core.model;

import java.util.Queue;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class TestSuit {

    private final Queue<TestCase> testCases;

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
