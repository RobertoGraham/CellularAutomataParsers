package io.github.robertograham.cellularautomata.parsers.life105;

import io.github.robertograham.cellularautomata.parsers.common.model.CellularAutomataPattern;

public class Life105Pattern extends CellularAutomataPattern {

    public String getRule() {
        return (String) properties().getOrDefault(PropertyKey.RULE, null);
    }

    public void setRule(String rule) {
        properties().setProperty(PropertyKey.RULE.name(), rule);
    }

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
        RULE, WIDTH, HEIGHT
    }
}
