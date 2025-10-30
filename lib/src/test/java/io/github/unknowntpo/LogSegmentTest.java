package io.github.unknowntpo;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogSegmentTest {
    @Test
    void testAppendToLogSegment() {
        var segment = new LogSegment("0000.log", new ArrayList<>());
        Record record0 = new Record("hello", "world");
        Record record1 = new Record("abc", "def");

        segment.appendRecord(record0);
        segment.appendRecord(record1);

        assertEquals(2, segment.recordSize());
        assertEquals(record0, segment.getRecord(0));
        assertEquals(record0, segment.getRecord(0));
    }
}