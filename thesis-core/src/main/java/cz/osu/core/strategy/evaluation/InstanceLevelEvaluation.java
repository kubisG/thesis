package cz.osu.core.strategy.evaluation;

import org.springframework.stereotype.Component;

import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import cz.osu.core.model.Method;
import cz.osu.core.model.Scope;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
@Component
public class InstanceLevelEvaluation implements EvaluationStrategy {

    @Override
    public Object evaluate(Method method) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Scope scope = method.getScope();
        Class<?> clazz = scope.getScopeValue().getClass();

        java.lang.reflect.Method clazzMethod = getMethod(clazz, method.getName(), method.getParameterClasses());

        return clazzMethod.invoke(scope.getScopeValue(), method.getParametersAsObjects());
    }

    private java.lang.reflect.Method getMethod(Class<?> clazz, String methodName, Class<?>[] parameterClasses) throws NoSuchMethodException {
        Optional<java.lang.reflect.Method> optionalMethod = Arrays.stream(clazz.getMethods())
                .filter(method -> method.getName().equals(methodName))
                .filter(method -> allParameterMatches(method.getParameterTypes(), parameterClasses))
                .findFirst();

        if (!optionalMethod.isPresent()) {
            throw new NoSuchMethodException("java.lang.NoSuchMethodException:" + clazz.getName());
        }
        return optionalMethod.get();
    }

    private boolean allParameterMatches(Class<?>[] parameterTypes, Class<?>[] parameterClasses) {
        boolean match = true;
        for (int i = 0; i < parameterTypes.length; i++) {
            if(!parameterTypes[i].isAssignableFrom(parameterClasses[i])) {
                match = false;
                break;
            }
        }
        return match;
    }
}
