//package database;
//
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//import static database.DatabaseConnect.DATABASE_PASSWORD;
//import static database.DatabaseConnect.DATABASE_URL;
//import static database.DatabaseConnect.DATABASE_USERNAME;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
///**
// * Created by Thomas on 5-12-2016.
// */
//public class DatabaseConnectTest {
//
//    @Test
//    public void testConnectToDB() throws SQLException, InterruptedException {
//        DriverManager dM = Mockito.mock(DriverManager.class);
//        DatabaseConnect.open();
//        verify(dM, times(1)).getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
//    }
//}