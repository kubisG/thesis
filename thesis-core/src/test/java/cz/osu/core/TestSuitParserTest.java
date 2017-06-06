package cz.osu.core;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import cz.osu.core.enums.Annotations;

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

    @Before
    public void setUp() throws FileNotFoundException {
        String baseDirPath = ClassLoader.getSystemResource("selenium").getPath();
        String validSeleniumTests = "/valid_tests/";
        String validSeleniumTest = "zezula_test.java";

        File file = new File(baseDirPath + validSeleniumTests + validSeleniumTest);

        CompilationUnit compilationUnit = JavaParser.parse(file);

        testSuitParser.setCompilationUnit(compilationUnit);
    }

    @Test
    public void testTestSuitParserShouldFilterMethodsByTestAnnotation() throws Exception {
        // execute
        final List<MethodDeclaration> methods = testSuitParser.filterMethodsByAnnotation(Annotations.TEST.getValue());

        // verify
        assertThat(allMethodsHaveGivenAnnotation(methods, Annotations.TEST.getValue())).isTrue();
    }

    @Test
    public void testTestSuitParserShouldFilterMethodsByBeforeAnnotation() throws Exception {
        // execute
        final List<MethodDeclaration> methods = testSuitParser.filterMethodsByAnnotation(Annotations.BEFORE.getValue());

        // verify
        assertThat(allMethodsHaveGivenAnnotation(methods, Annotations.BEFORE.getValue())).isTrue();
    }

    @Test
    public void testTestSuitParserShouldFilterMethodsByAfterAnnotation() throws Exception {
        // execute
        final List<MethodDeclaration> methods = testSuitParser.filterMethodsByAnnotation(Annotations.AFTER.getValue());

        // verify
        assertThat(allMethodsHaveGivenAnnotation(methods, Annotations.AFTER.getValue())).isTrue();
    }

    @Test
    public void testTestSuitParserShouldReturnFields() throws Exception {
        // execute
        final List<FieldDeclaration> fields = testSuitParser.getFieldDeclarations();

        assertThat(fields).isNull();
    }

    private boolean allMethodsHaveGivenAnnotation(List<MethodDeclaration> filteredMethods, String annotation) {
        return filteredMethods.stream()
                .allMatch(method -> method.getAnnotationByName(annotation).isPresent());
    }
}