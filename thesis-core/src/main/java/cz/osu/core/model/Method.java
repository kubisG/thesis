package cz.osu.core.model;

import org.openqa.selenium.By;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Project: thesis
 * Created by Jakub on 17. 4. 2017.
 */
public class Method {

    private String name;

    private Scope scope;

    private List<Object> parameters = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public Object[] getParametersAsObjects() {
        return parameters.stream()
                .map(parameter -> (Variable) parameter)
                .map(variable -> variable.getValue())
                .collect(Collectors.toList())
                .toArray(new Object[parameters.size()]);
    }

    public void addParameter(Object parameter) {
        parameters.add(parameter);
    }

    public boolean hasStatementParameter() {
        return parameters.stream()
                .anyMatch(parameter -> (parameter instanceof Statement));
    }

    public Class<?>[] getParameterClasses() {
        List<Class<?>> classes = getVariables().stream()
                .map(variable -> variable.getValue().getClass())
                .collect(Collectors.toList());

        return classes.toArray(new Class<?>[classes.size()]);
    }

    public List<Variable> getVariables() {
        return parameters.stream()
                .filter(parameter -> (parameter instanceof Variable))
                .map(parameter -> (Variable) parameter)
                .collect(Collectors.toList());
    }

    public List<Statement> getStatements() {
        return parameters.stream()
                .filter(parameter -> (parameter instanceof Statement))
                .map(parameter -> (Statement) parameter)
                .collect(Collectors.toList());
    }

}
