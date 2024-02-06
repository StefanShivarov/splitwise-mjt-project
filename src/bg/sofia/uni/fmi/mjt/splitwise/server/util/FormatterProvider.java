package bg.sofia.uni.fmi.mjt.splitwise.server.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class FormatterProvider {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat(
            "#.00", DecimalFormatSymbols.getInstance(Locale.US));

    public static DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }

    public static DecimalFormat getDecimalFormat() {
        return DECIMAL_FORMAT;
    }

}
