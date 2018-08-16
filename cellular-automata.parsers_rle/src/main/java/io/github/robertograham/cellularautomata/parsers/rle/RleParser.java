package io.github.robertograham.cellularautomata.parsers.rle;

import io.github.robertograham.cellularautomata.parsers.common.model.Cell;
import io.github.robertograham.cellularautomata.parsers.common.model.Coordinate;
import io.github.robertograham.cellularautomata.parsers.common.parser.concrete.CellularAutomataPatternParser;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RleParser extends CellularAutomataPatternParser<RlePattern> {

    private static final String HEADER_REGEX = "x\\s*=\\s*[0-9]+\\s*,\\s*y\\s*=\\s*[0-9]+(\\s*,\\s*rule\\s*=\\s*.*)?";
    private static final String RULE_REGEX = "#r\\s+.*";
    private static final String COMMENT_REGEX = "#C|c(|(\\s+.*))";
    private static final Pattern ORIGIN_COORDINATE_PATTERN = Pattern.compile("#P|R\\s+(-?[0-9]+)\\s+(-?[0-9]+)");
    private static final String ENCODED_CELL_DATA_LINE_REGEX = "((\\d*[ob$])+!?)|!";
    private static final Pattern ENCODED_STATUS_RUN_PATTERN = Pattern.compile("(\\d*)([ob$])");

    @Override
    public RlePattern parse(InputStream inputStream) {
        var lines = readInputStreamToStringList(inputStream);
        var trimmedNonEmptyLines = lines.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());

        if (trimmedNonEmptyLines.isEmpty())
            throw new IllegalArgumentException("No important lines");

        var rlePattern = new RlePattern();

        extractAndSetWidthAndHeightAndRule(trimmedNonEmptyLines, rlePattern);
        extractAndSetComments(trimmedNonEmptyLines, rlePattern);
        extractAndSetOrigin(trimmedNonEmptyLines, rlePattern);
        extractAndSetCells(trimmedNonEmptyLines, rlePattern);

        return rlePattern;
    }

    private void extractAndSetWidthAndHeightAndRule(List<String> lines, RlePattern rlePattern) {
        var headerLine = lines.stream()
                .filter(line -> line.matches(HEADER_REGEX))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No valid header line"));

        headerLine = headerLine.replaceAll("\\s+", "");

        var headerLineComponents = headerLine.split(",", 3);

        rlePattern.setWidth(Long.parseLong(headerLineComponents[0].substring(2)));
        rlePattern.setHeight(Long.parseLong(headerLineComponents[1].substring(2)));

        if (headerLineComponents.length == 3)
            rlePattern.setRule(headerLineComponents[2].substring(5));
        else
            lines.stream()
                    .filter(line -> line.matches(RULE_REGEX))
                    .findFirst()
                    .ifPresent(ruleLine -> rlePattern.setRule(ruleLine.substring(2).trim()));
    }

    private void extractAndSetComments(List<String> lines, RlePattern rlePattern) {
        rlePattern.comments().addAll(
                lines.stream()
                        .filter(line -> line.matches(COMMENT_REGEX))
                        .map(commentLine -> commentLine.substring(2).trim())
                        .collect(Collectors.toList())
        );
    }

    private void extractAndSetOrigin(List<String> trimmedNonEmptyLines, RlePattern rlePattern) {
        var originCoordinateLine = trimmedNonEmptyLines.stream()
                .filter(line -> line.matches(ORIGIN_COORDINATE_PATTERN.pattern()))
                .findFirst()
                .orElse(null);

        if (originCoordinateLine != null) {
            var matcher = ORIGIN_COORDINATE_PATTERN.matcher(originCoordinateLine);

            if (matcher.find())
                rlePattern.setOrigin(new Coordinate(
                        Long.parseLong(matcher.group(1)),
                        Long.parseLong(matcher.group(2))
                ));
        }
    }

    private void extractAndSetCells(List<String> trimmedNonEmptyLines, RlePattern rlePattern) {
        var encodedCellData = trimmedNonEmptyLines.stream()
                .filter(line -> line.matches(ENCODED_CELL_DATA_LINE_REGEX))
                .collect(Collectors.joining());

        if (!encodedCellData.endsWith("!"))
            throw new IllegalArgumentException("Encoded cell data was not terminated");

        var matcher = ENCODED_STATUS_RUN_PATTERN.matcher(encodedCellData);
        var cells = new HashSet<Cell>();
        var coordinate = rlePattern.origin();

        while (matcher.find()) {
            var lengthString = matcher.group(1);
            var length = lengthString.isEmpty() ? 1L : Long.parseLong(lengthString);
            var status = matcher.group(2);

            if ("$".equals(status))
                coordinate = new Coordinate(rlePattern.origin().x(), coordinate.y() + length);
            else {
                if ("o".equals(status))
                    for (long x = coordinate.x(); x < coordinate.x() + length; x++)
                        cells.add(new Cell(coordinate.withX(x), 1));

                coordinate = coordinate.plusToX(length);
            }
        }

        rlePattern.cells().addAll(cells);
    }
}
