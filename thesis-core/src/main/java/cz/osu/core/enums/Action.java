package cz.osu.core.enums;

/**
 * Project: thesis
 * Created by Jakub on 20. 6. 2017.
 */
public enum Action {
    CLEAR("clear"), CLICK("click"), SEND_KEYS("sendKeys"), SUBMIT("submit");

    private final String value;

    Action(String value) {
        this.value = value;
    }

    public boolean equals(String s) {
        return this.value.equals(s);
    }
}
