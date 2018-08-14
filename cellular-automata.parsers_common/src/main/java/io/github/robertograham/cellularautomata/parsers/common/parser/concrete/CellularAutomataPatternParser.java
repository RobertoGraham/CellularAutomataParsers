package io.github.robertograham.cellularautomata.parsers.common.parser.concrete;

import io.github.robertograham.cellularautomata.parsers.common.model.CellularAutomataPattern;
import io.github.robertograham.cellularautomata.parsers.common.parser.ICellularAutomataPatternParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CellularAutomataPatternParser<T extends CellularAutomataPattern> implements ICellularAutomataPatternParser<T> {

    protected final List<String> readInputStreamToStringList(InputStream inputStream) {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return buffer.lines()
                    .collect(Collectors.toList());
        } catch (IOException ignored) {
            return new ArrayList<>();
        }
    }
}
