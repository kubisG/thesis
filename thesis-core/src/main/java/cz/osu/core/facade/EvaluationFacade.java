package cz.osu.core.facade;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import cz.osu.core.enums.ScopeType;
import cz.osu.core.model.Method;
import cz.osu.core.model.Scope;
import cz.osu.core.strategy.evaluation.ClassLevelEvaluation;
import cz.osu.core.strategy.evaluation.CreationLevelEvaluation;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;
import cz.osu.core.strategy.evaluation.InstanceLevelEvaluation;

/**
 * Project: thesis
 * Created by Jakub on 16. 6. 2017.
 */
@Component
public class EvaluationFacade {

    private final ClassLevelEvaluation classLevelEvaluation;

    private final InstanceLevelEvaluation instanceLevelEvaluation;

    private final CreationLevelEvaluation creationLevelEvaluation;

    @Inject
    public EvaluationFacade(ClassLevelEvaluation classLevelEvaluation, InstanceLevelEvaluation instanceLevelEvaluation, CreationLevelEvaluation creationLevelEvaluation) {
        this.classLevelEvaluation = classLevelEvaluation;
        this.instanceLevelEvaluation = instanceLevelEvaluation;
        this.creationLevelEvaluation = creationLevelEvaluation;
    }

    private Map<ScopeType, EvaluationStrategy> getStrategies() {
        return ImmutableMap.<ScopeType, EvaluationStrategy>builder()
                .put(ScopeType.CLASS, classLevelEvaluation)
                .put(ScopeType.CLASS_INSTANCE, instanceLevelEvaluation)
                .put(ScopeType.NEW_CLASS_INSTANCE, creationLevelEvaluation)
                .build();
    }

    public Object evaluate(Method method) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Map<ScopeType, EvaluationStrategy> strategies = getStrategies();
        Scope scope = method.getScope();
        EvaluationStrategy strategy = strategies.get(scope.getScopeType());

        return strategy.evaluate(method);
    }
}
