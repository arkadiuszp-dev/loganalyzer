package ap.cs;

import ap.cs.domain.LogEvent;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static junit.framework.Assert.assertEquals;

public class LogAnalyzerTest {

    private String[] sampleInput = {
            "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}",
            "{\"id\":\"scsmbstgrb\", \"state\":\"STARTED\", \"timestamp\":1491377495213}",
            "{\"id\":\"scsmbstgrc\", \"state\":\"FINISHED\", \"timestamp\":1491377495218}",
            "{\"id\":\"scsmbstgra\", \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495217}",
            "{\"id\":\"scsmbstgrc\", \"state\":\"STARTED\", \"timestamp\":1491377495210}",
            "{\"id\":\"scsmbstgrb\", \"state\":\"FINISHED\", \"timestamp\":1491377495216}"
    };

    @Test
    public void testSampleScenario() {

        Map<String, LogEvent> result = new HashMap<>();
        Consumer<LogEvent> output = event -> result.put(event.getId(), event);

        LogAnalyzer logAnalyzer = new LogAnalyzer(Arrays.stream(sampleInput), output);
        logAnalyzer.process();

        assertEquals(3, result.size());
        assertEquals(3, result.get("scsmbstgrb").getDuration());
        assertEquals(8, result.get("scsmbstgrc").getDuration());
    }

}
