package ap.cs;

import ap.cs.db.HsqlFileDbServer;
import ap.cs.db.LogEventDbClient;
import ap.cs.domain.LogEntry;
import ap.cs.domain.LogEvent;
import ap.cs.util.EntryMatcher;
import ap.cs.util.LogEntryParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Class executing the task of matching start and finish entries
 * and sending results to provided consumer
 * using a dedicated thread
 *
 * Segments input data into chunks basing on hashcode of id
 * to ensure that corresponding entries (STARTED - FINISHED) are processed by the same thread
 */
public class ParallelLogAnalyzer {
    private static final Logger log = Logger.getLogger(ParallelLogAnalyzer.class.getName());
    private static final int WORKERS = 4;
    private static final int MAX_WAIT_HOURS = 16;

    private final Consumer<LogEvent> output;
    private final EntryMatcher matcher;
    private final ExecutorService executorService;

    public ParallelLogAnalyzer(Consumer<LogEvent> output) {
        this.output = output;
        matcher = new EntryMatcher();
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Submit matching task and storing output to thread executor
     * @param entry Entry from the file
     */
    public void allocateToWorker(LogEntry entry) {
        executorService.submit(() -> matcher.matchEntry(entry).ifPresent(output::accept));
    }

    public void close() throws InterruptedException {
        executorService.shutdown();
        executorService.awaitTermination(MAX_WAIT_HOURS, TimeUnit.HOURS);
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
             HsqlFileDbServer dbServer = new HsqlFileDbServer()) {

            // Create output db connections
            LogEventDbClient[] dbClients = new LogEventDbClient[WORKERS];
            for (int i = 0; i < dbClients.length; i++) {
                dbClients[i] = new LogEventDbClient(HsqlFileDbServer.createConnection());
            }

            // Run logic
            process(inputStream, dbClients);
            log.info(() -> "Number of events in alert table after: " + dbClients[0].quantityCheck());

            // Clean up connections
            for (LogEventDbClient dbClient : dbClients) {
                dbClient.getConnection().close();
            }

        } catch (IOException e) {
            log.severe("Failure in opening an input file:" + e.getMessage());
        } catch (SQLException e) {
            log.severe("Failure accessing database: " + e.getMessage());
        } catch (InterruptedException e) {
            log.severe("Failure executing concurrent processing: " + e.getMessage());
        }
    }

    // Selects a dedicated worker thread basing on a hashcode of id
    // Assuring that "STARTED" and "FINISHED" events will end in the same thread
    // for improving the performance of a process of finding pairs
    protected static int selectWorkerIndex(LogEntry e) {
        return Math.abs(e.getId().hashCode()) % WORKERS;
    }

    // Process input in pool of workers
    protected static void process(Stream<String> inputStream, Consumer[] outputConsumers) throws InterruptedException {
        // Create pool of analyzers
        ParallelLogAnalyzer[] analyzers = new ParallelLogAnalyzer[outputConsumers.length];
        for (int i = 0; i < analyzers.length; i++) {
            analyzers[i] = new ParallelLogAnalyzer(outputConsumers[i]);
        }

        // Process input data passing execution to dedicated worker
        // Parallel stream to execute parsing also in parallel mode
        inputStream.map(LogEntryParser::parse).filter(Optional::isPresent).map(Optional::get)
                .parallel().forEach(e -> analyzers[selectWorkerIndex(e)].allocateToWorker(e));

        // Clean up worker threads
        for (ParallelLogAnalyzer analyzer : analyzers) {
            analyzer.close();
        }
    }
}
