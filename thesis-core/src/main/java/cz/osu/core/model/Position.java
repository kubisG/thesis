package cz.osu.core.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Project: thesis
 * Created by Jakub on 12. 4. 2017.
 */
public class Position {

    /**
     * Number of line where error occurred.
     */
    private Integer line;

    /**
     * Number of column where error occurred.
     */
    private Integer column;

    public Position() {

    }

    public Position(Integer line, Integer column) {
        this.line = line;
        this.column = column;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getLine() {
        return line;
    }

    public Integer getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        return new EqualsBuilder()
                .append(line, position.line)
                .append(column, position.column)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(line)
                .append(column)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("line", line)
                .append("column", column)
                .toString();
    }
}
