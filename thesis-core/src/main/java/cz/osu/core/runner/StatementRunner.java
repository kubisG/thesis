package cz.osu.core.runner;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import cz.osu.core.enums.ScopeType;
import cz.osu.core.factory.EvaluationStrategyFactory;
import cz.osu.core.factory.ExecutionStrategyFactory;
import cz.osu.core.model.Method;
import cz.osu.core.model.Statement;
import cz.osu.core.model.Variable;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;
import cz.osu.core.strategy.execution.ExecutionStrategy;

/**
 * Project: thesis
 * Created by Jakub on 15. 6. 2017.
 */
@Component
public class StatementRunner {

    private EvaluationStrategy evaluationStrategy;

    private ExecutionStrategy executionStrategy;

    private void setEvaluationStrategy(ScopeType scopeType) {
        evaluationStrategy = EvaluationStrategyFactory.getEvaluationStrategy(scopeType);
    }

    private void setExecutionStrategy(Boolean actionFlag) {
        executionStrategy = ExecutionStrategyFactory.getExecutionStrategy(actionFlag);
    }

    // TODO: 20. 6. 2017 ucesat implementaci s novyma metodama pro statement
    private Object evaluateStatementParameter(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method currentMethod = statement.removeMethod();
        Method nextMethod;
        Object value = null;

        while (currentMethod != null) {
            evaluateMethodParameters(currentMethod);
            setEvaluationStrategy(currentMethod.getScope().getScopeType());
            value = evaluationStrategy.evaluate(currentMethod);
            if ((nextMethod = statement.removeMethod()) != null) {
                nextMethod.getScope().setScopeValue(value);
                currentMethod = nextMethod;
            } else
                currentMethod = null;
        }
        return value;
    }

    private void evaluateMethodParameters(Method method) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Object> parameters = method.getParameters();

        for (int i = 0; i < parameters.size(); i++) {
            Object parameter = parameters.get(i);
            if (parameter instanceof Statement) {
                Statement statement = (Statement) parameter;
                Object value = evaluateStatementParameter(statement);
                parameters.set(i, new Variable(value, value.getClass()));
            }
        }
    }

    private void prepare(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (Method method: statement.getMethods()) {
            evaluateMethodParameters(method);
        }
    }

    private void execute(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        setExecutionStrategy(statement.getApplyActionFlag());
        executionStrategy.execute(statement);
    }

    public void run(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // evaluate parameters for all methods in statement
        prepare(statement);
        // perform action
        execute(statement);
    }
}
