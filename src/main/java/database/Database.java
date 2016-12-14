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

    private static String url = "jdbc:mysql://localhost:3306/mundus";
    private static String user = "admin";
    private static String password = "123456";

    public static Connection openConnectionToDb() {
        try {
            connection = getConnection(url, user, password);
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

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

    public static List<Map<String, Object>> excecuteUpdateQuery(final String query) {
        List<Map<String, Object>> listOfMaps = null;
        try {
            openConnectionToDb();
            System.out.println("delete working");
            QueryRunner queryRunner = new QueryRunner();
            listOfMaps = queryRunner.insert(connection, query, new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnectionToDb(connection);
        }
        return listOfMaps;
    }

    public static String excecuteSearchQuery(Connection connection, String query) {
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
