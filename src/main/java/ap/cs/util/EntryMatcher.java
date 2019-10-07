package ap.cs.util;

import ap.cs.domain.LogEntry;
import ap.cs.domain.LogEvent;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Stateful class for iterating over log entries,
 * matching start with end events and calculating duration
 */
public class EntryMatcher {

    private static final Logger log = Logger.getLogger(EntryMatcher.class.getName());

    private static final String STATE_STARTED = "STARTED";
    private static final int ALERT_THRESHOLD = 4;

    private Map<String, LogEntry> openEvents = new ConcurrentHashMap<>();

    /**
     * @param entry Subsequent log entry
     * @return A new event if corresponding entry was found
     */
    public Optional<LogEvent> matchEntry(LogEntry entry) {
        log.fine(() -> "New entry: " + entry);
        log.fine(() -> "Currently unmatched entries: " + openEvents.keySet());
        LogEntry firstEntry = openEvents.putIfAbsent(entry.getId(), entry);
        if (firstEntry == null) {
            return Optional.empty();
        } else if (!firstEntry.getState().equals(entry.getState())){
            // Don't keep matched entries, to save memory
            openEvents.remove(entry.getId());
            // calculate duration and correct sign if first event stored was "FINISHED"
            long duration = (entry.getTimestamp() - firstEntry.getTimestamp())
                    * (STATE_STARTED.equals(entry.getState()) ? -1 : 1);
            return Optional.of(new LogEvent(entry.getId(), entry.getType(), entry.getHost(),
                            duration, duration > ALERT_THRESHOLD));
        } else {
            log.warning("Repeated id with same state. Second entry ignored: " + entry);
            return Optional.empty();
        }
    }

}
