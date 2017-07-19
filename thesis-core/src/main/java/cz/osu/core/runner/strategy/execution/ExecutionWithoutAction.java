package cz.osu.core.runner.strategy.execution;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.lang.reflect.InvocationTargetException;

import cz.osu.core.runner.facade.EvaluationFacade;
import cz.osu.core.model.Method;
import cz.osu.core.model.Statement;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
@Component
public class ExecutionWithoutAction implements ExecutionStrategy {

    private final EvaluationFacade evaluationFacade;

    @Inject
    public ExecutionWithoutAction(EvaluationFacade evaluationFacade) {
        this.evaluationFacade = evaluationFacade;
    }

    @Override
    public void execute(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method currentMethod;
        Method nextMethod;

        while(statement.hasMethod()) {
            currentMethod = statement.removeMethod();
            Object value = evaluationFacade.evaluate(currentMethod);
            if ((nextMethod = statement.getMethod()) != null) {
                nextMethod.getScope().setScopeValue(value);
            }
        }
    }
}
