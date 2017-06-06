package cz.osu.core;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.nodeTypes.NodeWithArguments;
import com.github.javaparser.ast.stmt.BlockStmt;
import cz.osu.core.enums.Annotations;
import cz.osu.core.model.Method;
import cz.osu.core.model.TestCase;
import cz.osu.core.model.TestSuit;
import cz.osu.core.model.Variable;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Project: thesis
 * Created by Jakub on 10. 4. 2017.
 */
@Component
public class TestSuitParser {

    private final TestCaseParser testCaseParser;

    private final BindingResolver bindingResolver;

    @Inject
    public TestSuitParser(TestCaseParser testCaseParser, BindingResolver bindingResolver) {
        this.testCaseParser = testCaseParser;
        this.bindingResolver = bindingResolver;
    }

    /**
     * Method filters MethodDeclarations by given annotation.
     * @param annotation filtering criteria.
     * @return List of MethodDeclarations whose annotation matches @param annotation.
     */
    private List<MethodDeclaration> filterMethodsByAnnotation(CompilationUnit compilationUnit, Annotations annotation) {
        List<MethodDeclaration> methods = compilationUnit.getChildNodesByType(MethodDeclaration.class);

        return methods.stream()
                .filter(method -> method.getAnnotationByName(annotation.getValue()).isPresent())
                .collect(Collectors.toList());
    }

    private List<BlockStmt> getTestCases(CompilationUnit compilationUnit) {
        List<MethodDeclaration> testCases  =  filterMethodsByAnnotation(compilationUnit, Annotations.TEST);

        return testCases.stream()
                .filter(md -> !isTestIgnored(md))
                .map(md -> md.getBody())
                .filter(obs -> obs.isPresent())
                .map(obs -> obs.get())
                .collect(Collectors.toList());
    }

    private BlockStmt getSetUpMethod(CompilationUnit compilationUnit, Annotations annotation) {
        List<MethodDeclaration> setUpMethods = filterMethodsByAnnotation(compilationUnit, annotation);

        if (setUpMethods.size() > 1){
            throw new IllegalStateException("Test suit must not have more than one set up method");
        }

        if(setUpMethods.isEmpty() || !setUpMethods.get(0).getBody().isPresent()) {
            return new BlockStmt();
        }
        return setUpMethods.get(0).getBody().get();
    }

    private List<FieldDeclaration> getFields(CompilationUnit compilationUnit) {
        return compilationUnit.getChildNodesByType(FieldDeclaration.class);
    }

    private boolean isTestIgnored(MethodDeclaration testCase) {
        return testCase.getAnnotationByName(Annotations.IGNORE.getValue()).isPresent();
    }

    public TestSuit parse(CompilationUnit compilationUnit) {
        TestSuit testSuit = new TestSuit();

        // get test cases for current test suit
        List<BlockStmt> testCases = getTestCases(compilationUnit);

        // set up binding resolver
        bindingResolver.setFields(getFields(compilationUnit));
        bindingResolver.setBeforeMethod(getSetUpMethod(compilationUnit, Annotations.BEFORE));
        bindingResolver.setBeforeClassMethod(getSetUpMethod(compilationUnit, Annotations.BEFORE_CLASS));

        // parse all test cases
        for (BlockStmt testCase : testCases) {
            bindingResolver.setTestCase(testCase);
            testSuit.addTestCase(testCaseParser.parse(testCase));
        }
        return testSuit;
    }
}
