package cz.osu.core.parser;


import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import cz.osu.core.model.TestSuit;
import cz.osu.core.parser.TestSuitParser;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */

@ContextConfiguration(locations = {"/application-context-test.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestSuitParserTest {

    @Inject
    private TestSuitParser testSuitParser;

    private static CompilationUnit COMPILATION_UNIT;

    @BeforeClass
    public static void setUp() throws FileNotFoundException {
        String baseDirPath = ClassLoader.getSystemResource("selenium").getPath();

        String validSeleniumTests = "/valid_tests/";
        String validSeleniumTest = "zezula_test.java";

        File file = new File(baseDirPath + validSeleniumTests + validSeleniumTest);

        COMPILATION_UNIT = JavaParser.parse(file);
    }


    @Test
    public void testTestSuitParserShouldReturnFields() throws Exception {
        final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
        // execute
        final TestSuit testSuit = testSuitParser.parse(COMPILATION_UNIT);

        assertThat(testSuit).isNull();
    }
}