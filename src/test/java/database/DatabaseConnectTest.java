//package database;
//
//import org.junit.Test;
//import org.mockito.Mockito;
//
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
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
//        Database.openConnectionToDb();
//        //verify(dM, times(1)).getConnection(url, DATABASE_USERNAME, DATABASE_PASSWORD);
//    }
//}