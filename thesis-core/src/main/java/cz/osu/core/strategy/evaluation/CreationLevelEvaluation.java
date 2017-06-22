package cz.osu.core.strategy.evaluation;

import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cz.osu.core.model.Method;
import cz.osu.core.model.Scope;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
@Component
public class CreationLevelEvaluation implements EvaluationStrategy {

    @Override
    public Object evaluate(Method method) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Scope scope = method.getScope();
        Class<?> clazz = scope.getScopeClass();
        Constructor<?> constructor = clazz.getConstructor(method.getParameterClasses());

        return constructor.newInstance(method.getParametersAsObjects());
    }
}
