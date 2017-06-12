package cz.osu.core.model;

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

    private List<Variable> parameters = new LinkedList<>();

    private List<Method> methodTypeParameters = new LinkedList<>();

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

    public List<Object> getParameterValues() {
        return parameters.stream()
                .map(Variable::getValue)
                .collect(Collectors.toList());
    }

    public List<Class> getParameterTypes() {
        return parameters.stream()
                .map(Variable::getType)
                .collect(Collectors.toList());
    }

    public List<Method> getMethodTypeParameters() {
        return methodTypeParameters;
    }

    public void addParameter(Variable parameter) {
        parameters.add(parameter);
    }

    public void addMethodTypeParameters(Method method) {
        methodTypeParameters.add(method);
    }

}
