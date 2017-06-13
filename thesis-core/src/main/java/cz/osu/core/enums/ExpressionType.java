package cz.osu.core.enums;

/**
 * Project: thesis
 * Created by Jakub on 13. 6. 2017.
 */
public enum ExpressionType {
    METHOD_CALL("MethodCallExpr"), OBJECT_CREATION("ObjectCreationExpr"), FIELD_ACCESS("FieldAccessExpr"), NAME("NameExpr");

    private final String value;

    ExpressionType(String value) {
        this.value = value;
    }

    public boolean equals(String s) {
        return this.value.equals(s);
    }
}
