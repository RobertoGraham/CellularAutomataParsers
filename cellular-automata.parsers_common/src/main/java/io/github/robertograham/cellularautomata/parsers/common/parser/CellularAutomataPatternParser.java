package io.github.robertograham.cellularautomata.parsers.common.parser;

import io.github.robertograham.cellularautomata.parsers.common.model.CellularAutomataPattern;

import java.io.InputStream;

public interface CellularAutomataPatternParser {

    CellularAutomataPattern parse(InputStream inputStream);
}
