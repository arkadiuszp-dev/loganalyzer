package ap.cs.util;

import ap.cs.domain.LogEntry;
import org.junit.Test;

import java.util.Optional;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class LogEntryParserTest {

    @Test
    public void logEventCreatedForCorrectInput() {
        String input = "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";
        Optional<LogEntry> result = LogEntryParser.parse(input);

        assertTrue(result.isPresent());
        assertEquals("scsmbstgra", result.get().getId());
        assertEquals("STARTED", result.get().getState());
        assertEquals("APPLICATION_LOG", result.get().getType());
        assertEquals("12345", result.get().getHost());
        assertEquals(1491377495212L, result.get().getTimestamp());
    }

    @Test
    public void noResultForEmptyLine() {
        String input = "";
        assertEquals(Optional.empty(), LogEntryParser.parse(input));
    }

    @Test
    public void noResultForMissingMandatoryField() {
        String input1 = "{\"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";
        String input2 = "{\"id\":\"scsmbstgra\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";
        String input3 = "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\"}";

        assertEquals(Optional.empty(), LogEntryParser.parse(input1));
        assertEquals(Optional.empty(), LogEntryParser.parse(input2));
        assertEquals(Optional.empty(), LogEntryParser.parse(input3));
    }

    @Test
    public void noResultForBrokenLine() {
        String input = "\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}";
        assertEquals(Optional.empty(), LogEntryParser.parse(input));
    }

}