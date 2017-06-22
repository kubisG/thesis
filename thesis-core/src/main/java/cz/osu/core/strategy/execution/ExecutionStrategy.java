package cz.osu.core.strategy.execution;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import cz.osu.core.model.Statement;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
public interface ExecutionStrategy {

    void execute(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, AWTException, InterruptedException;
}
