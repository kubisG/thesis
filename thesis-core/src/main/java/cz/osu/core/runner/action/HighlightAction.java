package cz.osu.core.runner.action;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
@Component
public class HighlightAction {

    private WebDriver driver;

    private WebElement previousWebElement;

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    private void setPreviousWebElement(WebElement previousWebElement) {
        this.previousWebElement = previousWebElement;
    }

    private boolean isAttachedToDOM() {
        boolean displayed;

        try {
            displayed = previousWebElement.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException | SessionNotCreatedException ex) {
            displayed = false;
        }
        return displayed;
    }

    // TODO: 22. 6. 2017 will be refactored
    public void highLight(WebElement currentWebElement) {
        // only once by test case
        if (previousWebElement == null) {
            setPreviousWebElement(currentWebElement);
            ((JavascriptExecutor)driver).executeScript("arguments[0].style.cssText='border: 3px solid red; border-radius: 10px; padding: 5px'", currentWebElement);
        } else {
            if (isAttachedToDOM()) {
                ((JavascriptExecutor)driver).executeScript("arguments[0].style.border=''", previousWebElement);
                ((JavascriptExecutor)driver).executeScript("arguments[0].style.cssText='border: 3px solid red; border-radius: 10px; padding: 5px'", currentWebElement);
            } else {
                ((JavascriptExecutor)driver).executeScript("arguments[0].style.cssText='border: 3px solid red; border-radius: 10px; padding: 5px'", currentWebElement);
            }
            previousWebElement = currentWebElement;
        }
    }
}
