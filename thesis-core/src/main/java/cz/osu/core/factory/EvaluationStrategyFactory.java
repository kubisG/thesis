package cz.osu.core.factory;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import cz.osu.core.enums.ScopeType;
import cz.osu.core.strategy.evaluation.ClassLevelEvaluation;
import cz.osu.core.strategy.evaluation.CreationLevelEvaluation;
import cz.osu.core.strategy.evaluation.EvaluationStrategy;
import cz.osu.core.strategy.evaluation.InstanceLevelEvaluation;

/**
 * Project: thesis
 * Created by Jakub on 16. 6. 2017.
 */
public class EvaluationStrategyFactory {

    private static Map<ScopeType, EvaluationStrategy> strategies = ImmutableMap.<ScopeType, EvaluationStrategy>builder()
            .put(ScopeType.CLASS, new ClassLevelEvaluation())
            .put(ScopeType.CLASS_INSTANCE, new InstanceLevelEvaluation())
            .put(ScopeType.NEW_CLASS_INSTANCE, new CreationLevelEvaluation())
            .build();

    private EvaluationStrategyFactory() {
    }

    public static EvaluationStrategy getEvaluationStrategy(ScopeType scopeType) {
        return strategies.get(scopeType);
    }
}
