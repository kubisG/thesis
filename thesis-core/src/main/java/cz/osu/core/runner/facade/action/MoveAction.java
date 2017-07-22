package cz.osu.core.runner.facade.action;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
@Component
public class MoveAction {

    // TODO: 22. 6. 2017 change offset according to browser (currently is set on Firefox)
    private static final int DEFAULT_OFFSET = 70;

    private static final int DEFAULT_SCROLL_DISTANCE = 120;

    private static final int SCROLL_UP = -1;

    private static final int NUMBER_OF_STEPS_LD = 30000;

    private static final int NUMBER_OF_STEPS_SD = 15000;

    private static final int NUMBER_OF_STEPS_VSD = 9000;

    // TODO: 22. 6. 2017 change according to screen resolution (currently is set on ntb resolution)
    private int screenWidth = 1366;

    // TODO: 22. 6. 2017 change according to screen resolution (currently is set on ntb resolution)
    private int screenHeight = 768;

    // TODO: 22. 6. 2017 change according to screen resolution (currently is set on ntb resolution)
    private Point defaultPosition = new Point(((screenWidth / 2) - DEFAULT_OFFSET), (screenHeight - (2 * DEFAULT_OFFSET)));

    private Point previousPosition = new Point((screenWidth / 2), (screenHeight / 2));

    private final Robot robot;

    private WebDriver driver;

    private WebElement previousWebElement;

    @Inject
    public MoveAction(Robot robot) {
        this.robot = robot;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    private void setPreviousWebElement(WebElement currentWebElement) {
        this.previousWebElement = currentWebElement;
    }

    private void setPreviousPosition(Point currentPosition) {
        this.previousPosition = currentPosition;
    }

    private boolean isAttachedToDOM() {
        boolean displayed = false;
        try {
            if (previousWebElement != null) {
                displayed = previousWebElement.isDisplayed();
            }
        } catch (NoSuchElementException | StaleElementReferenceException | SessionNotCreatedException ex) {
            displayed = false;
        }
        return displayed;
    }

    private boolean isVisible(WebElement webElement) {
        return webElement.getLocation().getY() < screenHeight;
    }

    private Point getWebElementCenter(WebElement webElement) {
        Point leftHandCorner = webElement.getLocation();
        int height = webElement.getSize().getHeight();
        int width = webElement.getSize().getWidth();
        Point webElementCenter;
        int x;
        int y;

        if (leftHandCorner.getY() > screenHeight) {
            x = leftHandCorner.getX() + (width / 2);
            y = DEFAULT_SCROLL_DISTANCE;
            webElementCenter = new Point(x, y);
        } else {
            x = leftHandCorner.getX() + (width / 2);
            y = leftHandCorner.getY() + (height / 2);
            webElementCenter = new Point(x, y);
        }
        return webElementCenter;
    }

    private void scrollToWebElementTop(WebElement webElement) {
        int elementPosition = webElement.getLocation().getY();
        String js = String.format("window.scroll(0, %s)", elementPosition);
        ((JavascriptExecutor) driver).executeScript(js);
    }

    private void scroll(WebElement webElement) {
        if (!isVisible(webElement)) {
            scrollToWebElementTop(webElement);
            robot.mouseWheel(SCROLL_UP);
        }
    }

    private double calculateDistance(int x1, int x2, int y1, int y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    private int getNumberOfSteps(Point previousPosition, Point currentPosition) {
        double distance = calculateDistance(previousPosition.getX(), currentPosition.getX(),
                previousPosition.getY(), currentPosition.getY());

        if (distance < 150) {
            return NUMBER_OF_STEPS_VSD;
        } else if (distance > 150 && distance < 300) {
            return NUMBER_OF_STEPS_SD;
        }
        return NUMBER_OF_STEPS_LD;
    }

    private int calculateCoordinate(int numberOfSteps, int startCoordinate, int endCoordinate, int step, int offset) {
        return (startCoordinate * (numberOfSteps - step) / numberOfSteps)
                + (((endCoordinate + offset) * step) / numberOfSteps);
    }

    private void mouseMove(Point currentPosition) {
        int currentX = 0;
        int currentY = 0;

        int numberOfSteps = getNumberOfSteps(previousPosition, currentPosition);

        for (int i = 0; i < numberOfSteps; i++) {
            currentX = calculateCoordinate(numberOfSteps, previousPosition.getX(), currentPosition.getX(), i,0);
            currentY = calculateCoordinate(numberOfSteps, previousPosition.getY(), currentPosition.getY(), i, DEFAULT_OFFSET);
            robot.mouseMove(currentX, currentY);
        }
        robot.delay(1000);
        setPreviousPosition(new Point(currentX, currentY));
    }

    private void moveToDefaultPosition(Point defaultPosition) {
        if (!isAttachedToDOM()) {
            mouseMove(defaultPosition);
        }
    }

    public void move(WebElement webElement) throws AWTException {
        Point webElementCenter = getWebElementCenter(webElement);
        // if url is changed then move to default position
        moveToDefaultPosition(defaultPosition);
        // if web element is not visible then scroll, otherwise do nothing
        scroll(webElement);
        // perform mouse move to center of web element
        mouseMove(webElementCenter);
        // set current web element to previous to be able check page reload and so on
        setPreviousWebElement(webElement);
    }

}
