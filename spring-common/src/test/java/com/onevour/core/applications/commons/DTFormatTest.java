package com.onevour.core.applications.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DTFormatTest {

    @Test
    void patternAndFormatChecks() {
        assertTrue(DTFormat.isMatchPattern("2026-02-14"));
        assertFalse(DTFormat.isMatchPattern("14-02-2026"));
        assertTrue(DTFormat.isValidFormat("dd-MM-yyyy", "14-02-2026"));
        assertFalse(DTFormat.isValidFormat("yyyy-MM-dd", "abc"));
        assertFalse(DTFormat.isValidFormat(null, "x"));
    }

    @Test
    void ektpConversion() {
        assertEquals("2026-02-14", DTFormat.convertFormatEKTP("yyyy-MM-dd", "14-02-2026"));
        assertNull(DTFormat.convertFormatEKTP("yyyy-MM-dd", "2026-02-14")); // bukan dd-MM-yyyy
        assertNull(DTFormat.convertFormatEKTP("yyyy-MM-dd", "32-01-2026")); // tanggal tidak valid
        assertNull(DTFormat.convertFormatEKTP(null, "14-02-2026"));
    }

    @Test
    void determineAndConvert() {
        assertEquals("yyyy-MM-dd", DTFormat.determineDateFormat("2026-02-14"));
        assertEquals("dd/MM/yyyy", DTFormat.determineDateFormat("14/02/2026"));
        assertNull(DTFormat.determineDateFormat("not a date"));
        assertEquals("14/02/2026", DTFormat.convertToFormat("2026-02-14", "dd/MM/yyyy"));
        assertNull(DTFormat.convertToFormat("nope", "dd/MM/yyyy"));
        assertNull(DTFormat.convertToFormat(null, "dd/MM/yyyy"));
    }
}
