package com.onevour.core.applications.commons;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DTCommonsTest {

    private final DateTime ref = new DateTime(2026, 2, 14, 13, 45, 30, 500);

    @Test
    void startAndEndOfDay() {
        assertEquals(new DateTime(2026, 2, 14, 0, 0, 0, 0), DTCommons.startOfDay(ref));
        assertEquals(new DateTime(2026, 2, 14, 23, 59, 59, 999), DTCommons.endOfDay(ref));
        assertEquals(DTCommons.startOfDay(ref), DTCommons.startOfDay(ref.toDate()));
        assertEquals(DTCommons.endOfDay(ref), DTCommons.endOfDay(ref.toDate()));
    }

    @Test
    void startAndEndOfMonth() {
        assertEquals(new DateTime(2026, 2, 1, 0, 0, 0, 0), DTCommons.startOfMonth(ref));
        assertEquals(new DateTime(2026, 2, 28, 23, 59, 59, 999), DTCommons.endOfMonth(ref));
    }

    @Test
    void startAndEndOfYear() {
        assertEquals(new DateTime(2026, 1, 1, 0, 0, 0, 0), DTCommons.startOfThisYear(ref));
        assertEquals(new DateTime(2026, 12, 31, 23, 59, 59, 999), DTCommons.endOfThisYear(ref));
        assertEquals(DTCommons.startOfThisYear(ref), DTCommons.startOfThisYear(ref.toDate()));
    }

    @Test
    void todayHelpersAreConsistent() {
        DateTime start = DTCommons.startOfDay();
        DateTime end = DTCommons.endOfDay();
        assertEquals(0, start.getMillisOfDay());
        assertEquals(start.toLocalDate(), end.toLocalDate());
        assertEquals(1, start.minus(DTCommons.startOfDayYesterday().getMillis()).getMillis() / (24 * 3600 * 1000));
        assertEquals(start.toLocalDate().minusDays(1), DTCommons.endOfDayYesterday().toLocalDate());
        assertEquals(DTCommons.startOfMonth(DateTime.now()), DTCommons.startOfThisMonth());
        assertEquals(DTCommons.endOfMonth(DateTime.now()), DTCommons.endOfThisMonth());
    }
}
