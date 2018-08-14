package io.github.robertograham.cellularautomata.parsers.common.parser;

import io.github.robertograham.cellularautomata.parsers.common.model.CellularAutomataPattern;

import java.io.IOException;
import java.io.InputStream;

public interface ICellularAutomataPatternParser<T extends CellularAutomataPattern> {

    T parse(InputStream inputStream) throws IOException;
}
