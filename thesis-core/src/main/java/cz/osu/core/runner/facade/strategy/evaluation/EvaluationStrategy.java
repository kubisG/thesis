package cz.osu.core.runner.facade.strategy.evaluation;

import java.lang.reflect.InvocationTargetException;

import cz.osu.core.model.Method;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
public interface EvaluationStrategy {

    Object evaluate(Method method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;
}
