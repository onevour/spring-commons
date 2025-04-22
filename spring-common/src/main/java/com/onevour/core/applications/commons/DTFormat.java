package com.onevour.core.applications.commons;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;

@Slf4j
public class DTFormat {

    private DTFormat() {
        throw new IllegalStateException("Utility class");
    }

    private static String y4m2d2 = "^\\d{4}-\\d{2}-\\d{2}$";

    private static String d2m2y4 = "^\\d{2}-\\d{2}-\\d{4}$";
    private static String ektp = "^((0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-(19|2[0-9])[0-9]{2})$";


    public static boolean isMatchPattern(String text) {
        return Pattern.compile(y4m2d2).matcher(text).matches();
    }

    public static boolean isValidFormat(String format, String dateString) {
        if (ValueOf.isNull(format, dateString)) {
            log.warn("format or dateString is null or empty");
            return false;
        }
        try {
            new SimpleDateFormat(format).parse(dateString);
            return true;
        } catch (ParseException e) {
            log.error("error convert date format {} {}", format, dateString, e);
        }
        return false;
    }

    public static String convertFormatEKTP(String formatTarget, String dateString) {
        if (ValueOf.isNull(formatTarget, dateString)) {
            log.warn("formatSource or formatTarget or dateString is null or empty");
            return null;
        }
        if (!Pattern.compile(d2m2y4).matcher(dateString).matches()) {
            return null;
        }
        if (!Pattern.compile(ektp).matcher(dateString).matches()) {
            return null;
        }
        return convertFormat("dd-MM-yyyy", formatTarget, dateString);

    }

    public static String convertFormat(String formatSource, String formatTarget, String dateString) {
        if (ValueOf.isNull(formatSource, formatTarget, dateString)) {
            log.warn("formatSource or formatTarget or dateString is null or empty");
            return null;
        }
        boolean isEktpFormat = Pattern.compile(ektp).matcher(dateString).matches();
        if (!isEktpFormat) {
            return null;
        }
        try {
            Date date = new SimpleDateFormat(formatSource).parse(dateString);
            return new SimpleDateFormat(formatTarget).format(date);
        } catch (ParseException e) {
            log.error("error convert date format {} {}", formatSource, dateString, e);
        }
        return null;
    }

    public static String determineDateFormat(String dateText) {
        // List of potential date formats
        String[] formats = {
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                "MM-dd-yyyy",
                "yyyy/MM/dd",
                "dd-MM-yyyy",
                "yyyy.MM.dd",
                "MMM dd, yyyy",
                "dd MMM yyyy"
                // Add more formats as needed
        };

        for (String format : formats) {
            try {
                DateTimeFormatter formatter = DateTimeFormat.forPattern(format);
                // Attempt to parse the date text
                formatter.parseDateTime(dateText);
                // If no exception, return the format
                return format;
            } catch (IllegalArgumentException | DateTimeParseException e) {
                // If parsing fails, continue with the next format
            }
        }
        // Return null if no format matches
        return null;
    }

    public static String convertToFormat(String dateString, String pattern) {
        if (ValueOf.isNull(dateString, pattern)) {
            return null;
        }
        String existPattern = determineDateFormat(dateString);
        if (ValueOf.isNull(existPattern)) {
            return null;
        }
        // Parse the date text to a DateTime object
        Assert.notNull(existPattern, "determine format date cannot be null");
        DateTime dateTime = DateTime.parse(dateString, DateTimeFormat.forPattern(existPattern));

        return DateTimeFormat.forPattern(pattern).print(dateTime);
    }
}
