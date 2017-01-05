package database;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connect and disconnect from server and executing queries.
 */
public final class Database {

//    private static String url = "jdbc:mysql://gi6kn64hu98hy0b6.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/"
//            + "z7vnfv6y27vhnelm?useSSL=false";
//    private static String user = "yum29ckgulepk404";
//    private static String password = "xp5oc6vwuz4tijx4";

    private static String url = "jdbc:mysql://127.0.0.1:3306/mundus?useSSL=false";
    private static String user = "root";
    private static String password = "";

    private static Database instance = null;
    private Connection connection = null;
    private QueryRunner queryRunner = null;

    private Thread thread = null;
    private ConcurrentLinkedQueue<Query> queries = null;
    private boolean disconnect = false;

    /**
     * Private constructor.
     */
    private Database() {
        try {
            queries = new ConcurrentLinkedQueue<>();
            queryRunner = new QueryRunner();
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an instance of the database.
     *
     * @return Returns the instance of the database.
     */
    protected static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Close the connection with the remote database if it is open.
     */
    public static void closeDatabase() {
        if (instance != null) {
            instance.kill();
            instance = null;
        }
    }

    /**
     * Get values in the database with the supplied query.
     *
     * @param sql    The query that will be executed.
     * @param params Optional parameters.
     * @return result of query.
     */
    public static List<Map<String, Object>> executeSearchQuery(String sql, Object... params) {
        CountDownLatch latch = new CountDownLatch(1);
        Search search = new Search(sql, result1 -> latch.countDown(), params);
        getInstance().add(search);

        while (search.getResult() == null) {
            getInstance().executeThread();
        }
        return search.getResult();
    }

    /**
     * Manipulates the data in the database with the supplied query and values.
     *
     * @param sql    the query template
     * @param params the values that should be inserted
     * @return <code>true</code> if query has been successfully executed, otherwise <code>false</code>
     */
    public static Boolean executeManipulationQuery(String sql, Object... params) {
        Update update = new Update(sql, result1 -> {
        }, params);
        getInstance().add(update);

        while (update.getResult() == null) {
            getInstance().executeThread();
        }
        return update.getResult();
    }

    /**
     * Execute the thread that queries the database.
     */
    protected void executeThread() {
        if (thread == null || !thread.isAlive()) {
            thread = new Thread(() -> {
                while (!queries.isEmpty() && !disconnect) {
                    queries.remove().execute(queryRunner, connection);
                }
                if (disconnect) {
                    disconnect();
                }
            });
            thread.start();
        }
    }

    /**
     * Adds a new query to the query queue.
     *
     * @param query The query to be added.
     */
    protected void add(Query query) {
        queries.add(query);
        executeThread();
    }

    /**
     * Disconnects from the database.
     */
    protected void disconnect() {
        try {
            if (connection != null) {
                connection.close();
            }
            connection = null;
            queryRunner = null;
            thread = null;
            queries = null;
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Queues the thread to kill the connection with the database.
     */
    protected void kill() {
        disconnect = true;
    }
}
