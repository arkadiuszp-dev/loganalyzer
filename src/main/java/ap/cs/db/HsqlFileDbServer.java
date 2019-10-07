package ap.cs.db;

import org.hsqldb.server.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Class responsible for starting and stopping file-based HSQL db server
 */
public class HsqlFileDbServer implements AutoCloseable {

    private static final Logger log = Logger.getLogger(HsqlFileDbServer.class.getName());

    private static final String DB_NAME = "alertDB";
    private static final String DB_FILE_NAME = "file:" + DB_NAME;
    private static final String DB_CONNECTION_NAME = "jdbc:hsqldb:hsql://localhost/" + DB_NAME;

    private final Server hsqlServer;

    public HsqlFileDbServer() {
        this.hsqlServer = new Server();
        // Configure file based db
        hsqlServer.setLogWriter(null);
        hsqlServer.setSilent(true);
        hsqlServer.setDatabaseName(0, DB_NAME);
        hsqlServer.setDatabasePath(0, DB_FILE_NAME);

        // Get connection
        hsqlServer.start();
        log.info("HSQL DB server started");
    }

    public static Connection createConnection()  throws SQLException {
         return DriverManager.getConnection(DB_CONNECTION_NAME, "sa", "");
    }

    public void close() {
        if (hsqlServer != null) {
            hsqlServer.stop();
            log.info("HSQL DB server stopped");
        }
    }
}
