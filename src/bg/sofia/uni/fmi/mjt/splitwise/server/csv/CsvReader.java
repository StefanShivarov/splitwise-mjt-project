package bg.sofia.uni.fmi.mjt.splitwise.server.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CsvReader implements AutoCloseable {

    private final BufferedReader reader;
    private static final String SPLIT_REGEX = ",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)";

    public CsvReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public List<String[]> readAllLines() {
        List<String[]> lines = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.split(SPLIT_REGEX));
            }

            return lines;
        } catch (IOException e) {
            throw new RuntimeException("There was an error while reading the file!", e);
        }
    }

    public List<String> readAllLinesRaw() {
        List<String> rawLines = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                rawLines.add(line);
            }

            return rawLines;
        } catch (IOException e) {
            throw new RuntimeException("There was an error while reading the file!", e);
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException("There was an error closing the reader!", e);
        }
    }

}
