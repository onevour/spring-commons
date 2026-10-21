package com.onevour.core.applications.commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StopWatchTest {

    @Test
    void startingNewTaskStopsPreviousOne() {
        StopWatch watch = new StopWatch("t");
        watch.start("one");
        watch.start("two");          // Spring StopWatch biasa akan melempar IllegalStateException
        watch.stop();
        assertEquals(2, watch.getTaskCount());
        assertEquals("one", watch.getTaskInfo()[0].getTaskName());
        assertEquals("two", watch.getTaskInfo()[1].getTaskName());
    }

    @Test
    void callableAndRunnableWrappers() throws Exception {
        StopWatch watch = new StopWatch("t");
        int result = watch.start("calc", () -> 40 + 2);
        watch.start("run", () -> { });
        assertEquals(42, result);
        assertEquals(2, watch.getTaskCount());
        assertFalse(watch.isRunning());
    }

    @Test
    void callableFailureStillStopsTask() {
        StopWatch watch = new StopWatch("t");
        assertThrows(IllegalStateException.class, () -> watch.start("fail", () -> { throw new IllegalStateException("x"); }));
        assertFalse(watch.isRunning());
        assertEquals(1, watch.getTaskCount());
    }

    @Test
    void prettyPrintsContainTaskNames() throws Exception {
        StopWatch watch = new StopWatch("report");
        watch.start("load", () -> 1);
        watch.start("open-ended");   // masih berjalan, prettyPrint harus menghentikannya dulu
        String millis = watch.prettyPrintMillis();
        String seconds = watch.prettyPrintSecond();
        assertTrue(millis.contains("StopWatch 'report'"));
        assertTrue(millis.contains("load"));
        assertTrue(millis.contains("open-ended"));
        assertTrue(seconds.contains("Task name"));
        assertTrue(watch.shortSummaryMillis().endsWith("ms"));
        assertTrue(watch.shortSummarySecond().endsWith("s"));
    }

    @Test
    void manualModeDoesNotAutoStop() {
        StopWatch watch = new StopWatch("manual", false);
        watch.start("one");
        assertThrows(IllegalStateException.class, () -> watch.start("two"));
    }
}
