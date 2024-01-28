package bg.sofia.uni.fmi.mjt.splitwise.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements AutoCloseable {

    private final BufferedReader reader;

    public CSVReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public List<String[]> readAllLines() {
        List<String[]> lines = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line.split(",(?=(?:(?:[^\"]*\"){2})*[^\"]*$)"));
            }

            return lines;
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
