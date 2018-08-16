package io.github.robertograham.cellularautomata.parsers.life106;

import io.github.robertograham.cellularautomata.parsers.common.model.CellularAutomataPattern;

public class Life106Pattern extends CellularAutomataPattern {

    public Long getWidth() {
        return (Long) properties().getOrDefault(PropertyKey.WIDTH, null);
    }

    public void setWidth(long width) {
        properties().put(PropertyKey.WIDTH, width);
    }

    public Long getHeight() {
        return (Long) properties().getOrDefault(PropertyKey.HEIGHT, null);
    }

    public void setHeight(long height) {
        properties().put(PropertyKey.HEIGHT, height);
    }

    public enum PropertyKey {
        WIDTH, HEIGHT
    }
}
