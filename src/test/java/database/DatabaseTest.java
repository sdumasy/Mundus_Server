package database;

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



    @BeforeClass
    public static void clean() {
        cleanDatabase();
    }

    public static void cleanDatabase() {
        executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + PLAYER_ID_2 + "';");
        executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + PLAYER_ID + "';");
        executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + USER_JOIN_ID + "';");
        executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + MOD_JOIN_ID + "';");
        executeManipulationQuery("DELETE FROM session WHERE session_id='" + SESSION_ID + "';");
        executeManipulationQuery("DELETE FROM device WHERE device_id='" + DEVICE_ID_2 + "';");
        executeManipulationQuery("DELETE FROM device WHERE device_id='" + DEVICE_ID + "';");
    }


    public static void setupDevice() {
        executeManipulationQuery("INSERT INTO device VALUES ('" + DEVICE_ID + "','" + TOKEN + "');");
    }

    public static void setupDevice2() {
        executeManipulationQuery("INSERT INTO device VALUES ('" + DEVICE_ID_2 + "','" + TOKEN_2 + "');");
    }

    public static void setupSession() {
        String query = "INSERT INTO `session` VALUES (?, ?, ?, ?)";
        executeManipulationQuery(query, SESSION_ID, PLAYER_ID, 1, LocalDateTime.now());

        query = "INSERT INTO `session_token` VALUES (?, ?, ?)";
        executeManipulationQuery(query, MOD_JOIN_ID, SESSION_ID, 1);
        executeManipulationQuery(query, USER_JOIN_ID, SESSION_ID, 2);

        query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?)";
        executeManipulationQuery(query, PLAYER_ID, DEVICE_ID, SESSION_ID, 0, 0);
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