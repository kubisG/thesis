package cz.osu.core.model;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project: thesis
 * Created by Jakub on 17. 4. 2017.
 */
public class Method {

    private String name;

    private String type;

    private final List<Object> parameters = new LinkedList<>();

    private List<Class<?>> parameterTypes = new LinkedList<>();

    private List<Method> methodTypeParameters = new LinkedList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public List<Class<?>> getParameterTypes() {
        return parameterTypes;
    }

    public List<Method> getMethodTypeParameters() {
        return methodTypeParameters;
    }

    public void addParameter(Object parameter) {
        parameters.add(parameter);
    }

    public void addParameterType(Class<?> parameterType) {
        parameterTypes.add(parameterType);
    }

    public void addMethodTypeParameters(Method method) {
        methodTypeParameters.add(method);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Method)) return false;

        Method method = (Method) o;

        return new EqualsBuilder()
                .append(name, method.name)
                .append(parameters, method.parameters)
                .append(parameterTypes, method.parameterTypes)
                .append(methodTypeParameters, method.methodTypeParameters)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(parameters)
                .append(parameterTypes)
                .append(methodTypeParameters)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("parameters", parameters)
                .append("parameterTypes", parameterTypes)
                .append("methodTypeParameters", methodTypeParameters)
                .toString();
    }
}
