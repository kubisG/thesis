package cz.osu.core.model;

import java.util.Deque;
import java.util.LinkedList;

/**
 * Project: thesis
 * Created by Jakub on 22. 4. 2017.
 */
public class Statement {

    private final Deque<Method> methods;

    private final boolean applyActionFlag;

    public Statement(Deque<Method> methods, Boolean applyActionFlag) {
        this.methods = methods;
        this.applyActionFlag = applyActionFlag;
    }

    public Statement(Method method) {
        this.methods = new LinkedList<>();
        this.methods.add(method);
        this.applyActionFlag = false;
    }

    public Deque<Method> getMethods() {
        return methods;
    }

    public boolean hasMethod() {
        return !methods.isEmpty();
    }

    public Method getMethod() {
        return methods.peekFirst();
    }

    public Method removeMethod() {
        return methods.pollFirst();
    }

    public Method removeLastMethod() {
        return methods.pollLast();
    }

    public Method getFirstMethod() {return methods.getFirst();}

    public Method getLastMethod() {return methods.getLast();}

    public boolean getApplyActionFlag() {
        return applyActionFlag;
    }

    public boolean hasAtLeastTwoMethods() {
        Method top = methods.pollFirst();
        boolean result = hasMethod();
        methods.addFirst(top);
        return result;
    }
}
