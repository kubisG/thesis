package cz.osu.core.runner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
@ContextConfiguration(locations = {"/application-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCaseRunnerTest {

    @Test
    public void run() throws Exception {
    }

}