package database;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.sql.DriverManager.getConnection;

/**
 * Connect and disconnect from server and executing queries.
 */
public final class Database {

    private static Connection connection = null;

    private static String url =
            "jdbc:mysql://gi6kn64hu98hy0b6.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/z7vnfv6y27vhnelm?useSSL=false";
    private static String user = "yum29ckgulepk404";
    private static String password = "xp5oc6vwuz4tijx4";

    /**
     * Private constructor.
     */
    private Database() {
    }

    /**
     * Open a connection with the storage DB.
     */
    protected static void openConnectionToDb() {
        try {
            connection = getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Close the connection with the remote database if it is open.
     */
    protected static void closeConnectionToDb() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Logger.getGlobal().log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Get values in the database with the supplied query.
     *
     * @param sql    The query that will be executed.
     * @param params Optional parameters.
     * @return A JSON object with the query results.
     */
    public static List<Map<String, Object>> executeSearchQuery(String sql, Object... params) {
        List<Map<String, Object>> listOfMaps;
        try {
            openConnectionToDb();
            QueryRunner queryRunner = new QueryRunner();
            listOfMaps = queryRunner.query(connection, sql, new MapListHandler(), params);
        } catch (SQLException se) {
            throw new RuntimeException("Couldn't query the database.", se);
        } finally {
            closeConnectionToDb();
        }
        return listOfMaps;
    }

    /**
     * Manipulates the data in the database with the supplied query and values.
     *
     * @param sql    the query template
     * @param params the values that should be inserted
     * @return <code>true</code> if query has been successfully executed, otherwise <code>false</code>
     */
    public static boolean executeManipulationQuery(String sql, Object... params) {
        boolean result;
        try {
            openConnectionToDb();

            QueryRunner runner = new QueryRunner();
            result = runner.update(connection, sql, params) > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Couldn't query the database.", e);
        } finally {
            closeConnectionToDb();
        }
        return result;
    }
}
