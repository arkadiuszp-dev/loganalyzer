package ap.cs.util;

import ap.cs.domain.LogEntry;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * JSON parser of LogEntry
 */
public class LogEntryParser {

    private static final Logger log = Logger.getLogger(LogEntryParser.class.getName());

    /**
     * @param json String representing LogEntry json
     * @return object containing data from input String
     */
    public static Optional<LogEntry> parse(String json) {
        try {
            JSONObject jo = (JSONObject) new JSONParser().parse(json);

            String id =  (String) jo.get("id");
            String state = (String) jo.get("state");
            Long timestamp = (Long) jo.get("timestamp");

            if (id == null || state == null || timestamp == null) {
                log.warning("Missing mandatory field(s): [" + json + "]. Skipped");
                return Optional.empty();
            }

            LogEntry logEntry = new LogEntry(
                id, state, (String) jo.get("type"), (String) jo.get("host"), timestamp);
            return Optional.of(logEntry);
        }catch (ParseException pe) {
            log.warning("Could not parse line: [" + json + "]. Skipped");
            return Optional.empty();
        }
    }
}
