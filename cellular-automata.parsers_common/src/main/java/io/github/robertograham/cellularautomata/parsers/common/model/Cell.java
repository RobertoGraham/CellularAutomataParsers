package io.github.robertograham.cellularautomata.parsers.common.model;

import java.util.Objects;

public class Cell {

    private final Coordinate coordinate;
    private final long state;

    public Cell(Coordinate coordinate, long state) {
        this.coordinate = coordinate;
        this.state = state;
    }

    public Coordinate coordinate() {
        return coordinate;
    }

    public long state() {
        return state;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (!(object instanceof Cell))
            return false;

        Cell cell = (Cell) object;

        return state == cell.state &&
                Objects.equals(coordinate, cell.coordinate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(coordinate, state);
    }
}
