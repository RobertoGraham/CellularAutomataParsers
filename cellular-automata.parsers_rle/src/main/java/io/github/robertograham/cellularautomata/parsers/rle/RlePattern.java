package io.github.robertograham.cellularautomata.parsers.rle;

import io.github.robertograham.cellularautomata.parsers.common.model.CellularAutomataPattern;

public class RlePattern extends CellularAutomataPattern {

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

    public String getConceptionDetails() {
        return (String) properties().getOrDefault(PropertyKey.CONCEPTION_DETAILS, null);
    }

    public void setConceptionDetails(String conceptionDetails) {
        properties().put(PropertyKey.CONCEPTION_DETAILS, conceptionDetails);
    }

    public enum PropertyKey {
        RULE, WIDTH, HEIGHT, CONCEPTION_DETAILS
    }
}
