package ap.cs.util;

import ap.cs.domain.LogEntry;
import ap.cs.domain.LogEvent;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EntryMatcherTest {

    @Test
    public void testMatchingBasic() {
        List<LogEntry> entries = new ArrayList<>();
        entries.add(new LogEntry("1", "STARTED","", "", 12345));
        entries.add(new LogEntry("2", "STARTED","", "", 12347));
        entries.add(new LogEntry("1", "FINISHED","", "", 12349));
        entries.add(new LogEntry("2", "FINISHED","", "", 12352));

        Map<String, LogEvent> result = new HashMap<>();

        EntryMatcher entryMatcher = new EntryMatcher();
        for (LogEntry e : entries) {
            entryMatcher.matchEntry(e)
                    .ifPresent(event -> result.put(event.getId(), event));
        }

        assertEquals(4, result.get("1").getDuration());
        assertFalse(result.get("1").isAlert());
        assertEquals(5, result.get("2").getDuration());
        assertTrue(result.get("2").isAlert());
    }

    @Test
    public void testMatchingRevertedOrder() {
        List<LogEntry> entries = new ArrayList<>();
        entries.add(new LogEntry("1", "FINISHED","", "", 12349));
        entries.add(new LogEntry("2", "STARTED","", "", 12347));
        entries.add(new LogEntry("1", "STARTED","", "", 12345));

        Map<String, LogEvent> result = new HashMap<>();

        EntryMatcher entryMatcher = new EntryMatcher();
        for (LogEntry e : entries) {
            entryMatcher.matchEntry(e)
                    .ifPresent(event -> result.put(event.getId(), event));
        }

        assertEquals(4, result.get("1").getDuration());
    }

    @Test
    public void testIgnoringRepeatedEntry() {
        List<LogEntry> entries = new ArrayList<>();
        entries.add(new LogEntry("1", "STARTED","", "", 12345));
        entries.add(new LogEntry("1", "STARTED","", "", 12347));
        entries.add(new LogEntry("1", "FINISHED","", "", 12349));

        Map<String, LogEvent> result = new HashMap<>();

        EntryMatcher entryMatcher = new EntryMatcher();
        for (LogEntry e : entries) {
            entryMatcher.matchEntry(e)
                    .ifPresent(event -> result.put(event.getId(), event));
        }

        assertEquals(4, result.get("1").getDuration());
    }
}


