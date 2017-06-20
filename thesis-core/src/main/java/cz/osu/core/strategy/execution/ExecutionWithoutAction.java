package cz.osu.core.strategy.execution;

import java.lang.reflect.InvocationTargetException;

import cz.osu.core.enums.ScopeType;
import cz.osu.core.factory.EvaluationStrategyFactory;
import cz.osu.core.model.Method;
import cz.osu.core.model.Statement;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
public class ExecutionWithoutAction implements ExecutionStrategy {

    private EvaluationStrategy evaluationStrategy;

    private void setEvaluationStrategy(ScopeType scopeType) {
        this.evaluationStrategy = EvaluationStrategyFactory.getEvaluationStrategy(scopeType);
    }

    @Override
    public void execute(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method currentMethod;
        Method nextMethod;

        while(statement.hasMethod()) {
            currentMethod = statement.removeMethod();
            setEvaluationStrategy(currentMethod.getScope().getScopeType());
            Object value = evaluationStrategy.evaluate(currentMethod);
            if ((nextMethod = statement.getMethod()) != null) {
                nextMethod.getScope().setScopeValue(value);
            }
        }
    }
}
