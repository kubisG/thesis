package cz.osu.core.runner.facade.strategy.evaluation;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import cz.osu.core.model.Method;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
@Component
public class ClassLevelEvaluation implements EvaluationStrategy {

    @Override
    public Object evaluate(Method method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> clazz = method.getScope().getScopeClass();
        java.lang.reflect.Method clazzMethod = clazz.getMethod(method.getName(), method.getParameterClasses());

        return clazzMethod.invoke(null, method.getParametersAsObjects());
    }
}
