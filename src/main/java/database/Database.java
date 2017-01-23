package database;

import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Connect and disconnect from server and executing queries.
 */
public final class Database {

    private static String url = "jdbc:mysql://127.0.0.1:3306";
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
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the database connection URL and authentication.
     *
     * @param url a database url of the form
     * <code>jdbc:<em>subprotocol</em>:<em>subname</em></code>
     * @param user the database user on whose behalf the connection is being
     *   made
     * @param password the user's password
     */
    public static void setConnection(String url, String user, String password) {
        Database.url = url;
        Database.user = user;
        Database.password = password;
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
        Search search = new Search(sql, result1 -> {
        }, params);
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
        Update update = new Update(sql, result -> {
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
