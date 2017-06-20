package cz.osu.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

import cz.osu.core.enums.ScopeType;

/**
 * Project: thesis
 * Created by Jakub on 7. 6. 2017.
 */
public class Scope {

    private ScopeType scopeType;

    private Object scopeValue;

    private Class<?> scopeClass;

    public Scope() {
    }

    public Scope(ScopeType scopeType, Object scopeValue, Class<?> scopeClass) {
        this.scopeType = scopeType;
        this.scopeValue = scopeValue;
        this.scopeClass = scopeClass;
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

    public Class<?> getScopeClass() {
        return scopeClass;
    }

    public void setScopeClass(Class<?> scopeClass) {
        this.scopeClass = scopeClass;
    }

}
