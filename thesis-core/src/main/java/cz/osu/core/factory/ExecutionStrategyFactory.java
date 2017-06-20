package cz.osu.core.factory;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

import cz.osu.core.strategy.execution.ExecutionStrategy;
import cz.osu.core.strategy.execution.ExecutionWithAction;
import cz.osu.core.strategy.execution.ExecutionWithoutAction;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
public class ExecutionStrategyFactory {

    private static Map<Boolean, ExecutionStrategy> strategies = ImmutableMap.<Boolean, ExecutionStrategy>builder()
            .put(true, new ExecutionWithAction())
            .put(false, new ExecutionWithoutAction())
            .build();

    private ExecutionStrategyFactory() {
    }

    public static ExecutionStrategy getExecutionStrategy(Boolean applyAction) {
        return strategies.get(applyAction);
    }
}