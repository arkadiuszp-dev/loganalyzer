package ap.cs.db;

import ap.cs.domain.LogEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * DB client for handling LogEvent objects
 */
public class LogEventDbClient implements Consumer<LogEvent> {

    private static final Logger log = Logger.getLogger(LogEventDbClient.class.getName());

    private final Connection connection;
    private final PreparedStatement insertStatement;

    private static final String CREATE_TABLE_STATEMENT =
            "create table alert ( id VARCHAR(96), duration INTEGER, type VARCHAR(32), host VARCHAR(32), alert BOOLEAN);";
    private static final String INSERT_EVENT_STATEMENT =
            "insert into alert(id, duration, type, host, alert) values (?, ?, ?, ?, ?);";
    private static final String COUNT_EVENTS_STATEMENT =
            "select count(*) from alert";

    public LogEventDbClient(Connection connection)  throws SQLException {
        this.connection = connection;

        // Create table
        try {
            connection.prepareStatement(CREATE_TABLE_STATEMENT).execute();
        }
        catch (SQLException e) {
            // Table already exists. Ignore failure
            log.fine("Table alert couldn't be created");
        }

        // Prepare insert statement
        insertStatement = connection.prepareStatement(INSERT_EVENT_STATEMENT);
    }

    /**
     * Inserts logEvent into the database
     * @param logEvent row to be inserted into db
     */
    public void accept(LogEvent logEvent) {
        try {
            log.fine(()-> "Inserting: " + logEvent);
            insertStatement.setString(1, logEvent.getId());
            insertStatement.setLong(2, logEvent.getDuration());
            insertStatement.setString(3, logEvent.getType());
            insertStatement.setString(4, logEvent.getHost());
            insertStatement.setBoolean(5, logEvent.isAlert());
            insertStatement.execute();

        }
        catch (SQLException e) {
            log.warning("Failure executing insert query for:" + logEvent);
        }
    }

    /**
     * Counts number of rows in the alert table
     * @return number of rows
     */
    public int quantityCheck() {
        try {
            ResultSet set = connection.prepareStatement(COUNT_EVENTS_STATEMENT).executeQuery();
            set.next();
            return set.getInt(1);
        } catch (SQLException e) {
            log.warning("Failure executing count query");
            return -1;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
