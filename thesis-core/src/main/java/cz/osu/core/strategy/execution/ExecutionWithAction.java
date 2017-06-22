package cz.osu.core.strategy.execution;

import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import cz.osu.core.action.ActionFacade;
import cz.osu.core.enums.ScopeType;
import cz.osu.core.facade.EvaluationFacade;
import cz.osu.core.model.Method;
import cz.osu.core.model.Statement;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
@Component
public class ExecutionWithAction implements ExecutionStrategy {

    private final EvaluationFacade evaluationFacade;

    private final ActionFacade actionFacade;

    @Inject
    public ExecutionWithAction(EvaluationFacade evaluationFacade, ActionFacade actionFacade) {
        this.evaluationFacade = evaluationFacade;
        this.actionFacade = actionFacade;
    }

    private Method evaluate(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Method currentMethod;

        while(statement.hasAtLeastTwoMethods()) {
            currentMethod = statement.removeMethod();
            Object value = evaluationFacade.evaluate(currentMethod);
            statement.getMethod().getScope().setScopeValue(value);
        }
        return statement.getMethod();
    }

    @Override
    public void execute(Statement statement) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException, AWTException, InterruptedException {
        // evaluate part of statement to set action method scope
        Method actionMethod = evaluate(statement);
        // apply move and highlight action on webElement (scopeValue equals webElement)
        Object webElement = actionMethod.getScope().getScopeValue();
        actionFacade.apply((WebElement) webElement);
        // evaluate the rest part of statement (finish action)
        evaluationFacade.evaluate(actionMethod);
    }
}

