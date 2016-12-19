package database;

import com.google.gson.Gson;
import com.sun.tools.javac.jvm.ClassFile;
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
 * Created by macbookpro on 03/12/2016.
 */
public class Database {

    private static Connection connection = null;

    private static String url = "jdbc:mysql://gi6kn64hu98hy0b6.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/z7vnfv6y27vhnelm";
    private static String user = "yum29ckgulepk404";
    private static String password = "xp5oc6vwuz4tijx4";

    /**
     * Open a connection with the storage DB.
     * @return null
     */
    public static Connection openConnectionToDb() {
        try {
            connection = getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Close the connection with the remote database if it is open.
     * @param con the connection
     */
    public static void closeConnectionToDb(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    /**
     * Edits values in the database with the supplied query.
     * @param query The query that will be executed.
     * @return The resultSet.
     */
    public static List<Map<String, Object>> excecuteUpdateQuery(final String query) {
        List<Map<String, Object>> listOfMaps = null;
        try {
            openConnectionToDb();
            QueryRunner queryRunner = new QueryRunner();
            listOfMaps = queryRunner.insert(connection, query, new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDb(connection);
        }
        return listOfMaps;
    }

    /**
     * Get values in the database with the supplied query.
     * @param query The query that will be executed.
     * @return A JSON object with the query results.
     */
    public static String excecuteSearchQuery(String query) {
        List<Map<String, Object>> listOfMaps = null;
        try {
            openConnectionToDb();
            QueryRunner queryRunner = new QueryRunner();
            listOfMaps = queryRunner.query(connection, query, new MapListHandler());
        } catch (SQLException se) {
            throw new RuntimeException("Couldn't query the database.", se);
        }
        finally {
            closeConnectionToDb(connection);
        }
        return new Gson().toJson(listOfMaps);
    }

}
