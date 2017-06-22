package cz.osu.core.facade;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import cz.osu.core.model.Statement;
import cz.osu.core.strategy.execution.ExecutionStrategy;
import cz.osu.core.strategy.execution.ExecutionWithAction;
import cz.osu.core.strategy.execution.ExecutionWithoutAction;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
@Component
public class ExecutionFacade {

    private final ExecutionWithAction executionWithAction;

    private final ExecutionWithoutAction executionWithoutAction;

    @Inject
    public ExecutionFacade(ExecutionWithAction executionWithAction, ExecutionWithoutAction executionWithoutAction) {
        this.executionWithAction = executionWithAction;
        this.executionWithoutAction = executionWithoutAction;
    }

    private Map<Boolean, ExecutionStrategy> getStrategies() {
        return ImmutableMap.<Boolean, ExecutionStrategy>builder()
                .put(true, executionWithAction)
                .put(false, executionWithoutAction)
                .build();
    }

    public void execute(Statement statement) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, InterruptedException, AWTException {
        Map<Boolean, ExecutionStrategy> strategies = getStrategies();
        Boolean applyActionFlag = statement.getApplyActionFlag();
        ExecutionStrategy strategy = strategies.get(applyActionFlag);

        strategy.execute(statement);
    }
}