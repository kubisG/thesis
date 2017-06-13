package cz.osu.core.model;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project: thesis
 * Created by Jakub on 26. 2. 2017.
 *
 * Generic model class which represents field or variable.
 */
public class Variable extends Parameter{

    private final String name;
    private final Object value;
    private final Class<?> type;
        
    public Variable(Object value, Class type) {
        this.name = null;
        this.value = value;
        this.type = type;
    }

    public Variable(String name, Object value, Class type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public Class getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Variable)) return false;

        Variable variable = (Variable) o;

        return new EqualsBuilder()
                .append(name, variable.name)
                .append(value, variable.value)
                .append(type, variable.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(name)
                .append(value)
                .append(type)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("value", value)
                .append("type", type)
                .toString();
    }
}
