package com.onevour.core.applications.commons;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.util.Date;

public class DTCommons {

    private DTCommons() {
        throw new IllegalStateException("Utility class");
    }

    public static DateTime startOfDay() {
        return DateTime.now().withTimeAtStartOfDay();
    }

    public static DateTime endOfDay() {
        return DateTime.now().withTime(23, 59, 59, 999);
    }

    public static DateTime startOfDayYesterday() {
        return DateTime.now().minusDays(1).withTimeAtStartOfDay();
    }

    public static DateTime endOfDayYesterday() {
        return DateTime.now().minusDays(1).withTime(23, 59, 59, 999);
    }

    public static DateTime startOfDay(Date date) {
        return new DateTime(date).withTimeAtStartOfDay();
    }

    public static DateTime startOfDay(DateTime date) {
        return date.withTimeAtStartOfDay();
    }

    public static DateTime endOfDay(Date date) {
        return new DateTime(date).withTime(23, 59, 59, 999);
    }

    public static DateTime endOfDay(DateTime date) {
        return date.withTime(23, 59, 59, 999);
    }

    public static DateTime startOfThisMonth() {
        return startOfMonth(new DateTime());
    }

    public static DateTime endOfThisMonth() {
        return endOfMonth(new DateTime());
    }

    public static DateTime startOfMonth(DateTime dateTime) {
        return dateTime.dayOfMonth().withMinimumValue().withTimeAtStartOfDay();
    }

    public static DateTime endOfMonth(DateTime dateTime) {
        return dateTime.dayOfMonth().withMaximumValue().withTime(23, 59, 59, 999);
    }

    public static DateTime startOfThisYear() {
        return startOfThisYear(new DateTime());
    }

    public static DateTime endOfThisYear() {
        return endOfThisYear(new DateTime());
    }

    public static DateTime startOfThisYear(Date dateTime) {
        return startOfThisYear(new DateTime(dateTime));
    }

    public static DateTime startOfThisYear(DateTime dateTime) {
        DateTime date = dateTime.withMonthOfYear(1);
        return startOfMonth(date);
    }

    public static DateTime endOfThisYear(Date dateTime) {
        return endOfThisYear(new DateTime(dateTime));
    }

    public static DateTime endOfThisYear(DateTime dateTime) {
        DateTime date = dateTime.withMonthOfYear(12);
        return endOfMonth(date);
    }


    public static int diffInSecond(Date startTime, Date finishTime) {
        DateTime now = new DateTime(finishTime);
        DateTime dateTime = new DateTime(startTime);
        Seconds seconds = Seconds.secondsBetween(now, dateTime);
        return seconds.getSeconds();
    }
}
