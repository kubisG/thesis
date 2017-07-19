package cz.osu.core.runner.action;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;

/**
 * Project: thesis
 * Created by Jakub on 16. 6. 2017.
 */
@Component
public class ActionFacade {

    private MoveAction moveAction;

    private HighlightAction highlightAction;

    private WebDriver driver;

    @Inject
    public ActionFacade(MoveAction moveAction, HighlightAction highlightAction) {
        this.moveAction = moveAction;
        this.highlightAction = highlightAction;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    private void setUpActionFacadeComponents() {
        moveAction.setDriver(driver);
        highlightAction.setDriver(driver);
    }

    public void apply(WebElement webElement) throws InterruptedException, AWTException {
        // set up Actions facade
        setUpActionFacadeComponents();
        // highlight action
        highlightAction.highLight(webElement);
        // move action
        moveAction.move(webElement);
        /*Actions actions = new Actions(driver);
        actions.moveToElement(webElement).perform();*/
    }

}
