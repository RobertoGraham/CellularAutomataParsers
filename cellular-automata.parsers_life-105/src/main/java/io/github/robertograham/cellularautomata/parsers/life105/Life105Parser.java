package io.github.robertograham.cellularautomata.parsers.life105;

import io.github.robertograham.cellularautomata.parsers.common.model.Cell;
import io.github.robertograham.cellularautomata.parsers.common.model.Coordinate;
import io.github.robertograham.cellularautomata.parsers.common.parser.concrete.CellularAutomataPatternParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Life105Parser extends CellularAutomataPatternParser<Life105Pattern> {

    private static final String REQUIRED_HEADER_REGEX = "#Life 1\\.05";
    private static final String NORMAL_RULES_REGEX = "#N";
    private static final String RULES_REGEX = "#R [0-8]+/[0-8]+";
    private static final String COMMENT_REGEX = "#D(|( .*))";
    private static final Pattern CELL_BLOCK_PATTERN = Pattern.compile("(#P (-?[0-9]+) (-?[0-9]+))|([.*]+)");

    public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
        Life105Pattern life105Pattern = new Life105Parser().parse(new FileInputStream(new File(Life105Parser.class.getClassLoader().getResource("glider.lif").toURI())));


        System.out.print(LongStream.range(0, life105Pattern.getHeight())
                .mapToObj(y ->
                        life105Pattern.cells().stream()
                                .filter(cell -> y == cell.coordinate().y())
                                .collect(Collectors.toSet())
                )
                .map(coordinatesFilteredByY ->
                        LongStream.range(0, life105Pattern.getWidth())
                                .mapToObj(x ->
                                        coordinatesFilteredByY.stream()
                                                .filter(cell -> x == cell.coordinate().x())
                                                .collect(Collectors.toSet())
                                )
                                .map(coordinatesFilteredByYAndX -> coordinatesFilteredByYAndX.size() == 0 ? "." : "@")
                                .collect(Collectors.joining())
                )
                .collect(Collectors.joining("\n")));
    }

    @Override
    public Life105Pattern parse(InputStream inputStream) {
        List<String> lines = readInputStreamToStringList(inputStream);

        List<String> trimmedNonEmptyLines = lines.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        if (trimmedNonEmptyLines.isEmpty())
            throw new IllegalArgumentException("No important lines");

        checkHeader(trimmedNonEmptyLines.get(0));

        Life105Pattern life105Pattern = new Life105Pattern();

        Optional.ofNullable(getRule(trimmedNonEmptyLines))
                .ifPresent(life105Pattern::setRule);

        life105Pattern.comments().addAll(getComments(trimmedNonEmptyLines));

        List<String> encodedCellDataLines = trimmedNonEmptyLines.stream()
                .filter(line -> line.matches(CELL_BLOCK_PATTERN.pattern()))
                .collect(Collectors.toList());

        List<Cell> cells = new ArrayList<>();
        Coordinate coordinate = new Coordinate(0, 0);

        for (String encodedCellDataLine : encodedCellDataLines)
            if (encodedCellDataLine.startsWith("#"))
                coordinate = getCoordinateFromCellBlockHeader(encodedCellDataLine);
            else {
                for (String statusRun : encodedCellDataLine.split("(?<=(.))(?!\\1)")) {
                    long state = statusRun.startsWith("*") ? 1 : 0;
                    for (int i = 0; i < statusRun.length(); i++)
                        cells.add(new Cell(coordinate.plusToX(i), state));
                    coordinate = coordinate.plusToX(statusRun.length());
                }
                coordinate = new Coordinate(coordinate.x() - encodedCellDataLine.length(), coordinate.y() + 1);
            }

        if (cells.size() > 0) {
            cells.sort(Comparator.comparingLong(cell -> cell.coordinate().x()));
            long startX = cells.get(0).coordinate().x();
            long nextX = cells.get(cells.size() - 1).coordinate().x() + 1;

            cells.sort(Comparator.comparingLong(cell -> cell.coordinate().y()));
            long startY = cells.get(0).coordinate().y();
            long nextY = cells.get(cells.size() - 1).coordinate().y() + 1;

            life105Pattern.setWidth(nextX - startX);
            life105Pattern.setHeight(nextY - startY);
            life105Pattern.setOrigin(new Coordinate(startX, startY));

            life105Pattern.cells().addAll(
                    cells.stream()
                            .filter(cell -> cell.state() == 1)
                            .map(cell -> new Cell(new Coordinate(cell.coordinate().x() - startX, cell.coordinate().y() - startY), 1))
                            .collect(Collectors.toList())
            );
        } else {
            life105Pattern.setWidth(0);
            life105Pattern.setHeight(0);
        }

        return life105Pattern;
    }

    private void checkHeader(String header) {
        if (!header.matches(REQUIRED_HEADER_REGEX))
            throw new IllegalArgumentException("Header did not match \"" + REQUIRED_HEADER_REGEX + "\"");
    }

    private String getRule(List<String> lines) {
        String normalRules = lines.stream()
                .filter(line -> line.matches(NORMAL_RULES_REGEX))
                .findFirst()
                .orElse(null);

        String rules = lines.stream()
                .filter(line -> line.matches(RULES_REGEX))
                .findFirst()
                .orElse(null);

        if (normalRules == null && rules == null)
            return null;
        if (normalRules != null && rules != null)
            throw new IllegalArgumentException("Both #N and #R lines found");
        if (normalRules != null)
            return "23/3";

        return rules.substring(3).trim();
    }

    private List<String> getComments(List<String> lines) {
        return lines.stream()
                .filter(line -> line.matches(COMMENT_REGEX))
                .map(line -> line.substring(2).trim())
                .collect(Collectors.toList());
    }

    private Coordinate getCoordinateFromCellBlockHeader(String cellBlockHeader) {
        Matcher matcher = CELL_BLOCK_PATTERN.matcher(cellBlockHeader);

        if (matcher.find())
            return new Coordinate(
                    Long.parseLong(matcher.group(2)),
                    Long.parseLong(matcher.group(3))
            );

        throw new IllegalArgumentException("Impossible for this to be thrown");
    }
}