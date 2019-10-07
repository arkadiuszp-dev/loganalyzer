package ap.cs.domain;

/**
 * Represents a record to be saved in a database
 */
public class LogEvent {
    private final String id;
    private final String type;
    private final String host;
    private final long duration;
    private final boolean alert;

    public LogEvent(final String id, final String type, final String host,
                    final long duration, final boolean alert) {
        this.id = id;
        this.type = type;
        this.host = host;
        this.duration = duration;
        this.alert = alert;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getHost() {
        return host;
    }

    public long getDuration() {
        return duration;
    }

    public boolean isAlert() {
        return alert;
    }

    @Override
    public String toString() {
        return "LogEvent{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", host='" + host + '\'' +
                ", duration=" + duration +
                ", alert=" + alert +
                '}';
    }
}
