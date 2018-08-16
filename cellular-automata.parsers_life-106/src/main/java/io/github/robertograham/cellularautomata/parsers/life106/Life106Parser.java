package io.github.robertograham.cellularautomata.parsers.life106;

import io.github.robertograham.cellularautomata.parsers.common.model.Cell;
import io.github.robertograham.cellularautomata.parsers.common.model.Coordinate;
import io.github.robertograham.cellularautomata.parsers.common.parser.concrete.CellularAutomataPatternParser;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Life106Parser extends CellularAutomataPatternParser<Life106Pattern> {

    private static final String REQUIRED_HEADER_REGEX = "#Life\\s+1\\.06";
    private static final Pattern COORDINATE_LINE_PATTERN = Pattern.compile("(-?[0-9]+)\\s+(-?[0-9]+)");

    @Override
    public Life106Pattern parse(InputStream inputStream) {
        List<String> lines = readInputStreamToStringList(inputStream);

        List<String> trimmedNonEmptyLines = lines.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        if (trimmedNonEmptyLines.isEmpty())
            throw new IllegalArgumentException("No important lines");

        checkHeader(trimmedNonEmptyLines.get(0));

        Life106Pattern life106Pattern = new Life106Pattern();

        extractAndSetLiveCells(trimmedNonEmptyLines, life106Pattern);

        return life106Pattern;
    }

    private void checkHeader(String header) {
        if (!header.matches(REQUIRED_HEADER_REGEX))
            throw new IllegalArgumentException("Header did not match \"" + REQUIRED_HEADER_REGEX + "\"");
    }

    private void extractAndSetLiveCells(List<String> lines, Life106Pattern life106Pattern) {
        Set<String> coordinateLines = lines.stream()
                .filter(line -> line.matches(COORDINATE_LINE_PATTERN.pattern()))
                .collect(Collectors.toSet());

        Set<Cell> cells = new HashSet<>();
        Long minX = null, minY = null, maxX = null, maxY = null;

        for (String coordinateLine : coordinateLines) {
            Cell cell = new Cell(getCoordinateFromCoordinateLine(coordinateLine), 1);
            cells.add(cell);
            long x = cell.coordinate().x(), y = cell.coordinate().y(), nextX = x + 1, nextY = y + 1;
            minX = minX == null ? x : x < minX ? x : minX;
            minY = minY == null ? y : y < minY ? y : minY;
            maxX = maxX == null ? nextX : nextX > maxX ? nextX : maxX;
            maxY = maxY == null ? nextY : nextY > maxY ? nextY : maxY;
        }

        minX = minX != null ? minX : 0;
        minY = minY != null ? minY : 0;

        life106Pattern.setWidth((maxX != null ? maxX : minX) - minX);
        life106Pattern.setHeight((maxY != null ? maxY : minY) - minY);
        life106Pattern.setOrigin(new Coordinate(minX, minY));
        life106Pattern.cells().addAll(cells);
    }

    private Coordinate getCoordinateFromCoordinateLine(String coordinateLine) {
        Matcher matcher = COORDINATE_LINE_PATTERN.matcher(coordinateLine);

        if (matcher.find())
            return new Coordinate(
                    Long.parseLong(matcher.group(1)),
                    Long.parseLong(matcher.group(2))
            );

        throw new IllegalArgumentException("Impossible for this to be thrown");
    }
}
