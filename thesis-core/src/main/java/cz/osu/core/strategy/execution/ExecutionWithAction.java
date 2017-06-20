package cz.osu.core.strategy.execution;

import javax.inject.Inject;

import java.lang.reflect.InvocationTargetException;

import cz.osu.core.action.ActionFacade;
import cz.osu.core.enums.ScopeType;
import cz.osu.core.factory.EvaluationStrategyFactory;
import cz.osu.core.model.Method;
import cz.osu.core.model.Statement;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
public class ExecutionWithAction implements ExecutionStrategy {

    private ActionFacade actionFacade = new ActionFacade();

    private EvaluationStrategy evaluationStrategy;

    private void setEvaluationStrategy(ScopeType scopeType) {
        this.evaluationStrategy = EvaluationStrategyFactory.getEvaluationStrategy(scopeType);
    }

    private Method evaluate(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method currentMethod;

        while(statement.hasAtLeastTwoMethods()) {
            currentMethod = statement.removeMethod();
            setEvaluationStrategy(currentMethod.getScope().getScopeType());
            Object value = evaluationStrategy.evaluate(currentMethod);
            statement.getMethod().getScope().setScopeValue(value);
        }
        return statement.getMethod();
    }

    @Override
    public void execute(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        // evaluate statement to be able to set action method scope
        Method actionMethod = evaluate(statement);
        // apply action facade
        actionFacade.apply(actionMethod);
    }
}

