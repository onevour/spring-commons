package com.onevour.core.applications.base;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class BaseSpecTest {

    private final BaseSpec spec = new BaseSpec();

    @Test
    void nullChecksTreatBlankAsNull() {
        assertTrue(spec.hasNull("a", " "));
        assertTrue(spec.hasNull((Object) null));
        assertFalse(spec.hasNull("a", 1));
        assertTrue(spec.hasNotNull("a", 1));
        assertFalse(spec.hasNotNull("a", ""));
    }

    @Test
    void likeParams() {
        assertEquals("%abc%", spec.paramLike("abc"));
        assertEquals("%%", spec.paramLike(null));
        assertEquals("abc%", spec.paramStartWith("abc"));
        assertEquals("%abc", spec.paramEndWith("abc"));
    }

    @Test
    void numberChecks() {
        assertTrue(spec.isGTZero(1));
        assertFalse(spec.isGTZero(0));
        assertFalse(spec.isGTZero((Integer) null));
        assertTrue(spec.isGTEqZero(0));
        assertTrue(spec.isGTZero(5L));
        assertFalse(spec.isGTZero((Long) null));
    }

    @Test
    void distinctAndDuplicates() {
        List<String> items = Arrays.asList("a", "b", "a", "c", "b");
        List<String> distinct = items.stream().filter(spec.distinctByKey(s -> s)).collect(Collectors.toList());
        assertEquals(Arrays.asList("a", "b", "c"), distinct);
        Set<String> dup = spec.findDuplicateBySetAdd(items);
        assertEquals(Set.of("a", "b"), dup);
    }
}
