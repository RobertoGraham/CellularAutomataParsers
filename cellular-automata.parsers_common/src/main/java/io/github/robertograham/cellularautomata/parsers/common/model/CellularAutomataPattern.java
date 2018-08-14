package io.github.robertograham.cellularautomata.parsers.common.model;

import java.util.*;

public abstract class CellularAutomataPattern {

    private final Properties properties = new Properties();
    private final Set<Cell> cells = new HashSet<>();
    private final List<String> comments = new ArrayList<>();
    private Coordinate origin = new Coordinate(0, 0);

    public Properties properties() {
        return properties;
    }

    public Set<Cell> cells() {
        return cells;
    }

    public List<String> comments() {
        return comments;
    }

    public Coordinate origin() {
        return origin;
    }

    public void setOrigin(Coordinate origin) {
        this.origin = origin;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (!(object instanceof CellularAutomataPattern))
            return false;

        CellularAutomataPattern cellularAutomataPattern = (CellularAutomataPattern) object;

        return Objects.equals(properties, cellularAutomataPattern.properties) &&
                Objects.equals(cells, cellularAutomataPattern.cells) &&
                Objects.equals(comments, cellularAutomataPattern.comments) &&
                Objects.equals(origin, cellularAutomataPattern.origin);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, cells, comments, origin);
    }
}
