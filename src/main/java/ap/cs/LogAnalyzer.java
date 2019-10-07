package ap.cs;

import ap.cs.db.HsqlFileDbServer;
import ap.cs.db.LogEventDbClient;
import ap.cs.domain.LogEvent;
import ap.cs.util.EntryMatcher;
import ap.cs.util.LogEntryParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Class reading log entries from given input stream
 * matching start and finish entries
 * and writing results using provided db connection
 */
public class LogAnalyzer {
    private static final Logger log = Logger.getLogger(LogAnalyzer.class.getName());

    private final Stream<String> inputStream;
    private final Consumer<LogEvent> output;
    private final EntryMatcher matcher;

    /**
     * @param inputStream input data
     * @param output consumer result
     */
    public LogAnalyzer(Stream<String> inputStream, Consumer<LogEvent> output) {
        this.inputStream = inputStream;
        this.output = output;
        matcher = new EntryMatcher();
    }

    /**
     * Run the algorithm over all entries and write the result
     */
    public void process() {
        inputStream.map(LogEntryParser::parse).filter(Optional::isPresent)
                .forEach(entry -> matcher.matchEntry(entry.get()).ifPresent(output::accept));
    }

    /**
     *   Opens the file, starts database and db connections and executes the algorithm
     */
    public static void main(String[] args) {

            if (args.length != 1) {
                log.severe("Please provide path to the input file");
                return;
            }

            try (Stream<String> inputStream = Files.lines(Paths.get(args[0]));
                 HsqlFileDbServer dbServer = new HsqlFileDbServer();
                 Connection dbConnection = HsqlFileDbServer.createConnection()) {

                LogEventDbClient dbClient = new LogEventDbClient(dbConnection);
                LogAnalyzer logAnalyzer = new LogAnalyzer(inputStream, dbClient);
                logAnalyzer.process();

                log.info(() -> "Number of events in alert table after: " + dbClient.quantityCheck());
            } catch (IOException e) {
                log.severe("Failure in opening an input file:" + e.getMessage());
            } catch (SQLException e) {
                log.severe("Failure accessing database: " + e.getMessage());
            }
        }
}
