package database;

import models.Device;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static database.Database.executeManipulationQuery;
import static org.junit.Assert.assertTrue;

/**
 * Tests the Database class.
 */
public class DatabaseTest {
    public static final String DEVICE_ID = "deviceID_42";
    public static final String DEVICE_ID_2 = "deviceID_43";
    public static final String SESSION_ID = "sessionID_42";
    public static final String PLAYER_ID = "playerID_42";
    public static final String PLAYER_ID_2 = "playerID_43";
    public static final String USER_JOIN_ID = "user42";
    public static final String MOD_JOIN_ID = "mod42";
    public static final String TOKEN = "token_42";
    public static final String TOKEN_2 = "token_43";
    public static final String USERNAME = "username_42";
    public static final String ADMIN_USERNAME = "admin_username_42";

    /**
     * Make sure the database is clean before we do anything else.
     */
    @BeforeClass
    public static void clean() {
        String url = "jdbc:mysql://127.0.0.1:3306/mundus?useSSL=false";
        String user = "root";
        String password = "";

        Database.setConnection(url, user, password);
        cleanDatabase();
    }

    /**
     * Method that removes all entries that have been added testing purposes.
     */
    public static void cleanDatabase() {
        executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + PLAYER_ID_2 + "';");
        executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + PLAYER_ID + "';");
        executeManipulationQuery("DELETE FROM session_player WHERE device_id='" + DEVICE_ID + "';");
        executeManipulationQuery("DELETE FROM session_player WHERE device_id='" + DEVICE_ID + "';");
        executeManipulationQuery("DELETE FROM session_player WHERE username='" + USERNAME + "';");
        executeManipulationQuery("DELETE FROM session_player WHERE username='" + ADMIN_USERNAME + "';");
        executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + USER_JOIN_ID + "';");
        executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + MOD_JOIN_ID + "';");
        executeManipulationQuery("DELETE FROM session WHERE session_id='" + SESSION_ID + "';");
        executeManipulationQuery("DELETE FROM device WHERE device_id='" + DEVICE_ID_2 + "';");
        executeManipulationQuery("DELETE FROM device WHERE device_id='" + DEVICE_ID + "';");
    }

    /**
     * Method that inserts a device into the database for testing purposes.
     * @return returns the device that was inserted.
     */
    public static Device setupDevice() {
        executeManipulationQuery("INSERT INTO device VALUES ('" + DEVICE_ID + "','" + TOKEN + "');");
        return new Device(DEVICE_ID, TOKEN);
    }

    /**
     * Method that inserts a device into the database for testing purposes.
     * @return returns the device that was inserted.
     */
    public static Device setupDevice2() {
        executeManipulationQuery("INSERT INTO device VALUES ('" + DEVICE_ID_2 + "','" + TOKEN_2 + "');");
        return new Device(DEVICE_ID_2, TOKEN_2);
    }

    /**
     * Method that inserts a session into the database for testing purposes.
     */
    public static void setupSession() {
        String query = "INSERT INTO `session` VALUES (?, ?, ?, ?)";
        executeManipulationQuery(query, SESSION_ID, PLAYER_ID, 1, LocalDateTime.now());

        query = "INSERT INTO `session_token` VALUES (?, ?, ?)";
        executeManipulationQuery(query, MOD_JOIN_ID, SESSION_ID, 1);
        executeManipulationQuery(query, USER_JOIN_ID, SESSION_ID, 2);

        query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?, ?)";
        executeManipulationQuery(query, PLAYER_ID, DEVICE_ID, SESSION_ID, 0, 0, ADMIN_USERNAME);
    }

    /**
     * Method that inserts a player into the database for testing purposes.
     */
    public static void setupPlayer() {
        String query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?, ?)";
        executeManipulationQuery(query, PLAYER_ID_2, DEVICE_ID, SESSION_ID, 1, 0, USERNAME);
    }


    /**
     * Test whether constructor is private and does not raise any exceptions.
     *
     * @throws NoSuchMethodException     The method must be there.
     * @throws IllegalAccessException    The method must be accessible.
     * @throws InvocationTargetException The method must be invocable.
     * @throws InstantiationException    The method must be instantiationable.
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<Database> constructor = Database.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }


}