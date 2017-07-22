package cz.osu.core.runner.facade.action;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import cz.osu.core.model.Method;
import cz.osu.core.model.Variable;
import cz.osu.core.runner.facade.EvaluationFacade;

/**
 * Project: thesis
 * Created by Jakub on 16. 6. 2017.
 */
@Component
public class ActionFacade {

    private MoveAction moveAction;

    private KeyDownAction keyDownAction;

    private HighlightAction highlightAction;

    private EvaluationFacade evaluationFacade;

    private WebDriver driver;

    @Inject
    public ActionFacade(MoveAction moveAction, KeyDownAction keyDownAction, HighlightAction highlightAction, EvaluationFacade evaluationFacade) {
        this.moveAction = moveAction;
        this.keyDownAction = keyDownAction;
        this.highlightAction = highlightAction;
        this.evaluationFacade = evaluationFacade;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    private void setUpActionFacadeComponents() {
        moveAction.setDriver(driver);
        highlightAction.setDriver(driver);
    }

    private String getKeys(List<Variable> variables) {
        if (variables.size() != 1) {
            throw new IllegalArgumentException("Wrong number of argument passed to sendKeys method");
        }
        if (!String.class.isAssignableFrom(variables.get(0).getValue().getClass())) {
            throw new IllegalArgumentException("Wrong type of argument passed to sendKeys method");
        }
        return (String) variables.get(0).getValue();
    }

    private void apply(Method method, WebElement element) throws AWTException,
                                        NoSuchMethodException, InstantiationException,
                                    IllegalAccessException, InvocationTargetException {
        // use key down down action if method name is sendKeys, move action otherwise
        if (method.getName().equals("sendKeys")) {
            // try get keys from variable object
            String keys = getKeys(method.getVariables());
            // do key down action for all keys
            keyDownAction.sendKeys(keys);
        } else {
            // move action
            moveAction.move(element);
            // evaluate the rest part of statement (finish action)
            evaluationFacade.evaluate(method);
        }
    }

    public void apply(Method method) throws InterruptedException, AWTException,
                                NoSuchMethodException, InstantiationException,
                                IllegalAccessException, InvocationTargetException {
        // get web element
        WebElement element = (WebElement) method.getScope().getScopeValue();
        // set up Actions facade
        setUpActionFacadeComponents();
        // highlight action
        highlightAction.highLight(element);
        // apply action based on action type
        apply(method, element);
    }


}
