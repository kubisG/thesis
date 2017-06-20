package cz.osu.core.action;

import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;

import cz.osu.core.enums.ScopeType;
import cz.osu.core.factory.EvaluationStrategyFactory;
import cz.osu.core.model.Method;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;

/**
 * Project: thesis
 * Created by Jakub on 16. 6. 2017.
 */
@Component
public class ActionFacade {

    /*private MoveAction moveAction;

    private HighlighAction highlighAction;*/

    private EvaluationStrategy evaluationStrategy;

    public ActionFacade() {
        this.evaluationStrategy = EvaluationStrategyFactory.getEvaluationStrategy(ScopeType.CLASS_INSTANCE);
    }

    public void apply(Method method) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        evaluationStrategy.evaluate(method);
    }
}
