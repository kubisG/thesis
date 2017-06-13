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

    private List<Parameter> parameters = new LinkedList<>();

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

    public void addParameter(Parameter parameter) {
        parameters.add(parameter);
    }

}
