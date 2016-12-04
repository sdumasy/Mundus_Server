package database;

import com.sun.tools.javac.jvm.ClassFile;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.get;

/**
 * Created by macbookpro on 03/12/2016.
 */
public class DatabaseConnect {

    private static Connection con = null;
    private static Statement st = null;
    private static ResultSet rs = null;

    private static String url = "jdbc:mysql://localhost:3306/mundus";
    private static String user = "admin";
    private static String password = "123456";

    public static void connectToDb() {

        try {
            con = DriverManager.getConnection(url, user, password);
            st = con.createStatement();
            rs = st.executeQuery("SELECT VERSION()");

            if (rs.next()) {

                System.out.print(rs.getString(1));
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

        } finally {

            try {

                if (rs != null) {
                    rs.close();
                }

                if (st != null) {
                    st.close();
                }

                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {

                Logger lgr = Logger.getLogger(ClassFile.Version.class.getName());
                lgr.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
    }

}
