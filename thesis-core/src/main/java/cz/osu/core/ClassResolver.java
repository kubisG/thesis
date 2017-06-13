package cz.osu.core;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public Class<?> resolveScopeClass(String scopeExprName) {
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
