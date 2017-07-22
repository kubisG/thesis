package cz.osu.core.runner.facade.action;

import javaslang.collection.Stream;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;


/**
 * Project: thesis
 * Created by Jakub on 22. 7. 2017.
 */

@Component
public class KeyDownAction {

    private final Robot robot;

    @Inject
    public KeyDownAction(Robot robot) {
        this.robot = robot;
    }

    public void sendKeys(String keys) {
        StringSelection stringSelection = new StringSelection(keys);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, stringSelection);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }
}
