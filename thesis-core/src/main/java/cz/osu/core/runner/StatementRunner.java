package cz.osu.core.runner;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import cz.osu.core.enums.ScopeType;
import cz.osu.core.facade.EvaluationFacade;
import cz.osu.core.facade.ExecutionFacade;
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

    private final EvaluationFacade evaluationFacade;

    private final ExecutionFacade executionFacade;

    @Inject
    public StatementRunner(EvaluationFacade evaluationFacade, ExecutionFacade executionFacade) {
        this.evaluationFacade = evaluationFacade;
        this.executionFacade = executionFacade;
    }

    private Object evaluateParameter(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method currentMethod;
        Method nextMethod;
        Object value = null;

        while(statement.hasMethod()) {
            currentMethod = statement.removeMethod();
            evaluateMethodParameters(currentMethod); // indirect recursion
            value = evaluationFacade.evaluate(currentMethod);
            if ((nextMethod = statement.getMethod()) != null) {
                nextMethod.getScope().setScopeValue(value);
            }
        }
        return value;
    }

    private void evaluateMethodParameters(Method method) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        List<Object> parameters = method.getParameters();

        for (int i = 0; i < parameters.size(); i++) {
            Object parameter = parameters.get(i);
            if (parameter instanceof Statement) {
                Statement statement = (Statement) parameter;
                Object value = evaluateParameter(statement); // indirect recursion
                parameters.set(i, new Variable(value, value.getClass()));
            }
        }
    }

    private void prepare(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        for (Method method: statement.getMethods()) {
            evaluateMethodParameters(method);
        }
    }

    private void execute(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, AWTException, InterruptedException {
        executionFacade.execute(statement);
    }

    public void run(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException, AWTException {
        // evaluate parameters for all methods in statement
        prepare(statement);
        // perform action
        execute(statement);
    }
}
