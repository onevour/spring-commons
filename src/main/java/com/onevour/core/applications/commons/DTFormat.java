package com.onevour.core.applications.commons;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Slf4j
public class DTFormat {

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
            Date date = new SimpleDateFormat(format).parse(dateString);
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

}
