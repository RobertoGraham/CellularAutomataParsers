package io.github.robertograham.cellularautomata.parsers.common.model;

import java.util.Objects;

public final class Coordinate {

    private final long x;
    private final long y;

    public Coordinate(long x, long y) {
        this.x = x;
        this.y = y;
    }

    public long x() {
        return x;
    }

    public long y() {
        return y;
    }

    public Coordinate withX(long x) {
        return new Coordinate(x, y);
    }

    public Coordinate withY(long y) {
        return new Coordinate(x, y);
    }

    public Coordinate plusToX(long amount) {
        return new Coordinate(x + amount, y);
    }

    public Coordinate plusToY(long amount) {
        return new Coordinate(x, y + amount);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (!(object instanceof Coordinate))
            return false;

        Coordinate coordinate = (Coordinate) object;

        return x == coordinate.x &&
                y == coordinate.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
