package cz.osu.core.resolver;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.springframework.stereotype.Component;

import java.sql.Ref;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.javaparser.ast.ImportDeclaration;

/**
 * Project: thesis
 * Created by Jakub on 13. 6. 2017.
 */
@Component
public class ClassResolver {

    private static final String FULLY_QUALIFIED_NAME_REGEX = "([a-zA-Z_$][a-zA-Z\\d_$]*\\.)+[a-zA-Z_$][a-zA-Z\\d_$]*";

    private static final Pattern FULLY_QUALIFIED_NAME_PATTERN = Pattern.compile(FULLY_QUALIFIED_NAME_REGEX);

    private List<ImportDeclaration> imports;

    private Map<String, String> availableClassNames;

    public void setImports(List<ImportDeclaration> imports) {
        this.imports = imports;
    }

    public void setAvailableClassNames(Map<String, String> availableClassNames) {
        this.availableClassNames = availableClassNames;
    }

    private String findScopeClassNameInJar(String scopeClassName) {
        return availableClassNames.get(scopeClassName);
    }

    private boolean isFullyQualifiedName(String scopeClassName) {
        Matcher matcher = FULLY_QUALIFIED_NAME_PATTERN.matcher(scopeClassName);
        return matcher.matches();
    }

    private Optional<String> findScopeClassNameInImports(String scopeClassName) {
        return imports.stream()
                .map(impt -> impt.getNameAsString())
                .filter(importName -> importName.contains(scopeClassName))
                .findFirst();
    }

    /*private Set<Class<?>> setUp(){
        List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false *//* don't exclude Object.class *//*), new ResourcesScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("org.openqa.selenium"))));

        Set<Class<?>> classes = reflections.getSubTypesOf(Object.class);

        return classes;
    }*/

    public Class<?> resolveExpressionClass(String scopeExprName) {
        Optional<String> tmpScopeClassName;
        String scopeClassName;

        if (isFullyQualifiedName(scopeExprName)) {
            scopeClassName = scopeExprName;
        } else if ((tmpScopeClassName = findScopeClassNameInImports(scopeExprName)).isPresent()) {
            scopeClassName = tmpScopeClassName.get();
        } else {
            scopeClassName = findScopeClassNameInJar(scopeExprName);
        }

        try {
            return Class.forName(scopeClassName);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Cannot find class for scope");
        }
    }
}
