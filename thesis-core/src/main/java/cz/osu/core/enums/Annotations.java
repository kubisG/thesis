package cz.osu.core.enums;

/**
 * Project: thesis
 * Created by Jakub on 11. 4. 2017.
 */
public enum Annotations {
    BEFORE("Before"), BEFORE_CLASS("BeforeClass"), AFTER("After"), TEST("Test"), IGNORE("Ignore");

    private final String value;

    Annotations(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
