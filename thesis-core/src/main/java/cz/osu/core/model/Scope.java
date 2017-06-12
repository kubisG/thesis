package cz.osu.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import cz.osu.core.enums.ScopeType;

/**
 * Project: thesis
 * Created by Jakub on 7. 6. 2017.
 */
public class Scope {

    private ScopeType scopeType;
    private Object scopeValue;
    private Class scopeClass;

    public Scope() {
    }

    public Scope(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public ScopeType getScopeType() {
        return scopeType;
    }

    public void setScopeType(ScopeType scopeType) {
        this.scopeType = scopeType;
    }

    public Object getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(Object scopeValue) {
        this.scopeValue = scopeValue;
    }

    public Class getScopeClass() {
        return scopeClass;
    }

    public void setScopeClass(Class scopeClass) {
        this.scopeClass = scopeClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Scope)) return false;

        Scope scope = (Scope) o;

        return new EqualsBuilder()
                .append(scopeType, scope.scopeType)
                .append(scopeValue, scope.scopeValue)
                .append(scopeClass, scope.scopeClass)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(scopeType)
                .append(scopeValue)
                .append(scopeClass)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("scopeType", scopeType)
                .append("scopeValue", scopeValue)
                .append("scopeClass", scopeClass)
                .toString();
    }
}
