package ap.cs;

import ap.cs.domain.LogEntry;
import ap.cs.domain.LogEvent;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static junit.framework.Assert.assertEquals;

public class ParallelLogAnalyzerTest {

    private String[] sampleInput = {
            "{\"id\":\"scsmbstgra\", \"state\":\"STARTED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495212}",
            "{\"id\":\"scsmbstgrb\", \"state\":\"STARTED\", \"timestamp\":1491377495213}",
            "{\"id\":\"scsmbstgrc\", \"state\":\"FINISHED\", \"timestamp\":1491377495218}",
            "{\"id\":\"scsmbstgra\", \"state\":\"FINISHED\", \"type\":\"APPLICATION_LOG\", \"host\":\"12345\", \"timestamp\":1491377495217}",
            "{\"id\":\"scsmbstgrc\", \"state\":\"STARTED\", \"timestamp\":1491377495210}",
            "{\"id\":\"scsmbstgrb\", \"state\":\"FINISHED\", \"timestamp\":1491377495216}"
    };

    @Test
    public void testSampleScenario() throws InterruptedException{
        int workers = 4;
        Map<String, LogEvent> result = new ConcurrentHashMap<>();
        Consumer[] outputConsumers = new Consumer[workers];
        Consumer<LogEvent> consumer = event -> result.put(event.getId(), event);
        Arrays.fill(outputConsumers, consumer);
        ParallelLogAnalyzer.process(Arrays.stream(sampleInput), outputConsumers);

        assertEquals(3, result.size());
        assertEquals(3, result.get("scsmbstgrb").getDuration());
        assertEquals(8, result.get("scsmbstgrc").getDuration());
    }

    @Test
    public void verifyExecutionOn1KRecords() throws InterruptedException{

        int testSize = 1000;
        int workers = 4;
        List<String> input = new ArrayList<>();
        for (int i = 0; i < testSize; i++) {
            input.add("{\"id\":\"" + i + "\", \"state\":\"STARTED\", \"type\":\"A\", \"timestamp\":" + i + "}");
            input.add("{\"id\":\"" + i + "\", \"state\":\"FINISHED\", \"type\":\"A\", \"timestamp\":" + (i + 2) + "}");
        }
        Collections.shuffle(input);

        Map<String, LogEvent> result = new ConcurrentHashMap<>();
        Consumer[] outputConsumers = new Consumer[workers];
        Consumer<LogEvent> consumer = event -> result.put(event.getId(), event);
        Arrays.fill(outputConsumers, consumer);
        ParallelLogAnalyzer.process(input.stream(), outputConsumers);

        assertEquals(testSize, result.size());
    }

    @Test
    public void verifyThatSameIndexIsSelectedForEntriesWithSameId() {
        LogEntry started = new LogEntry("a", "STARTED", "A", "B", 10);
        LogEntry finished = new LogEntry("a", "FINISHED", "A", "B", 12);
        assertEquals(ParallelLogAnalyzer.selectWorkerIndex(started), ParallelLogAnalyzer.selectWorkerIndex(finished));
    }
}
