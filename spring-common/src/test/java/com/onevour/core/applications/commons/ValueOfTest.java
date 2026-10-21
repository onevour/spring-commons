package com.onevour.core.applications.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueOfTest {

    @Test
    void isNullTreatsBlankStringAsNull() {
        assertTrue(ValueOf.isNull((Object) null));
        assertTrue(ValueOf.isNull(""));
        assertTrue(ValueOf.isNull("   "));
        assertFalse(ValueOf.isNull("null"));
        assertFalse(ValueOf.isNull(0));
        // varargs: true bila salah satu null/kosong
        assertTrue(ValueOf.isNull("a", "", "b"));
        assertFalse(ValueOf.isNull("a", "b"));
        assertTrue(ValueOf.nonNull("a"));
        assertFalse(ValueOf.nonNull(" "));
    }

    @Test
    void initReturnsDefaultOnlyWhenNullOrBlank() {
        assertEquals("d", ValueOf.init((String) null, "d"));
        assertEquals("d", ValueOf.init(" ", "d"));
        assertEquals("v", ValueOf.init("v", "d"));
        assertEquals(5, ValueOf.init((Integer) null, 5));
        assertEquals(3L, ValueOf.init(3L, 9L));
        assertEquals(Boolean.TRUE, ValueOf.init((Boolean) null, true));
    }

    @Test
    void ofNullableTreatsBlankAsEmpty() {
        assertTrue(ValueOf.ofNullable(" ").isEmpty());
        assertEquals("x", ValueOf.ofNullable("x").orElseThrow());
        assertTrue(ValueOf.ofNullable((Integer) null).isEmpty());
        assertEquals(2, ValueOf.ofNullable(2).orElseThrow());
    }

    @Test
    void textComparisons() {
        assertTrue(ValueOf.equalText("abc", "x", "ABC"));
        assertFalse(ValueOf.equalText(null, "abc"));
        assertFalse(ValueOf.equalText("abc", (String) null));
        assertTrue(ValueOf.notEqualText("abc", "x"));
        assertFalse(ValueOf.notEqualText("abc", "ABC"));
        assertTrue(ValueOf.containsText("hello world", "wor"));
        assertFalse(ValueOf.containsText("hello", "WOR"));
        assertTrue(ValueOf.containsTextIgnoreCase("hello", "ELL"));
        assertTrue(ValueOf.notContainsTextIgnoreCase("hello", "xyz"));
        assertTrue(ValueOf.notContainsTextIgnoreCase(null, "xyz"));
        assertTrue(ValueOf.startWithText("0812", "08", "+62"));
        assertFalse(ValueOf.startWithText("0812", "+62"));
        assertTrue(ValueOf.equalInt(200, 200, 201));
        assertFalse(ValueOf.equalInt(null, 200));
    }

    @Test
    void lengthAndNumberChecks() {
        assertTrue(ValueOf.minMaxLength(2, 4, " abc "));   // di-trim dulu
        assertFalse(ValueOf.minMaxLength(2, 4, "abcde"));
        assertFalse(ValueOf.minMaxLength(2, 4, null));
        assertTrue(ValueOf.lengthEquals("1234", 4));
        assertTrue(ValueOf.lengthEquals(null, 4));           // null dianggap cocok (perilaku saat ini)
        assertTrue(ValueOf.notLength("123", 4));
        assertTrue(ValueOf.isNumber("0123"));
        assertFalse(ValueOf.isNumber("12a"));
        assertFalse(ValueOf.isNumber(null));
        assertTrue(ValueOf.isNotNumber("-1"));
        assertTrue(ValueOf.isDouble("1.5"));
        assertFalse(ValueOf.isDouble("x"));
        assertTrue(ValueOf.isValidRegex("^[A-Z]{3}$", "ABC"));
        assertFalse(ValueOf.isValidRegex("^[A-Z]{3}$", null));
        assertFalse(ValueOf.inverse(true));
    }
}
