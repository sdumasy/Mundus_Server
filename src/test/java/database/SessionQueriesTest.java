package database;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.Device;
import models.Player;
import models.Role;
import models.Session;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static database.Database.executeSearchQuery;
import static database.SessionQueries.*;
import static org.junit.Assert.*;

/**
 * Tests the class SessionQueries.
 */
public class SessionQueriesTest {

    /**
     * Make sure the database is clean before we do anything else.
     */
    @BeforeClass
    public static void clean() {
        DatabaseTest.clean();
    }

    /**
     * Make sure the database is cleaned after we are done.
     */
    @After
    public void tearDown() {
        DatabaseTest.cleanDatabase();
    }

    /**
     * Test whether constructor is private and does not raise any exceptions.
     * @throws NoSuchMethodException The method must be there.
     * @throws IllegalAccessException The method must be accessible.
     * @throws InvocationTargetException The method must be invocable
     * @throws InstantiationException The method must be instantiationable.
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<SessionQueries> constructor = SessionQueries.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Verify whether a session is setup properly.
     */
    @Test
    public void createSessionTest() {
        try {
            DatabaseTest.setupDevice();
            createSession(new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN), DatabaseTest.ADMIN_USERNAME);
            List<Map<String, Object>> verify;

            verify = Database
                    .executeSearchQuery("SELECT player_id FROM session_player WHERE username='"
                            + DatabaseTest.ADMIN_USERNAME + "';");
            assertNotEquals(verify.size(), 0);

            verify = Database
                    .executeSearchQuery("SELECT player_id FROM session_player WHERE device_id='"
                            + DatabaseTest.DEVICE_ID + "';");
            assertNotEquals(verify.size(), 0);
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }


    /**
     * Test adding a new player to a running session.
     */
    @Test
    public void getNewPlayerOfSessionTest() {
        Player player;
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();
            DatabaseTest.setupDevice2();
            Device joinDevice = new Device(DatabaseTest.DEVICE_ID_2, DatabaseTest.TOKEN);
            player = playerJoinSession(DatabaseTest.USER_JOIN_ID, joinDevice, DatabaseTest.USERNAME);

            String query = "SELECT * FROM session_player WHERE device_id='" + DatabaseTest.DEVICE_ID_2 + "';";
            List<Map<String, Object>> verify = executeSearchQuery(query);
            assertEquals(verify.size(), 1);
            Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='"
                    + player.getPlayerID() + "';");
        } finally {
                DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Get the current state of the session player is in.
     */
    @Test
    public void getSessionTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();

            assertEquals(DatabaseTest.SESSION_ID, getSession(DatabaseTest.SESSION_ID).getSessionID());
            assertEquals(DatabaseTest.PLAYER_ID, getSession(DatabaseTest.SESSION_ID).getAdminID());
            assertEquals(1, getSession(DatabaseTest.SESSION_ID).getStatus());
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Gets the join tokens of a session.
     */
    @Test
    public void getSessionTokensTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();

            assertEquals(DatabaseTest.MOD_JOIN_ID,
                    getSessionTokens(DatabaseTest.SESSION_ID)
                    .get("moderator").getAsString());
            assertEquals(DatabaseTest.USER_JOIN_ID,
                    getSessionTokens(DatabaseTest.SESSION_ID).get("user").getAsString());
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Test if the administrator can change the state of the session.
     */
    @Test
    public void updateSessionStatusTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();
            Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);
            Session session = new Session(DatabaseTest.SESSION_ID, DatabaseTest.PLAYER_ID, 1, LocalDateTime.now());
            Player player = new Player(DatabaseTest.PLAYER_ID, session, device, Role.Admin, 0,
                    DatabaseTest.ADMIN_USERNAME);
            updateSessionStatus(DatabaseTest.SESSION_ID, 2);
            assertEquals(2, getSession(DatabaseTest.SESSION_ID).getStatus());
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Get the scores of all players in the session with sessionID.
     */
    @Test
    public void getSessionScoresTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();

            JsonObject jsonObject = getPlayers(DatabaseTest.SESSION_ID);
            JsonArray jsonArray = jsonObject.get("players").getAsJsonArray();
            assertEquals(1, jsonArray.size());
            assertEquals(DatabaseTest.PLAYER_ID, jsonArray.get(0).getAsJsonObject().get("playerID").getAsString());
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Tests isMember method.
     */
    @Test
    public void isMemberTest() {
        Device device = DatabaseTest.setupDevice();
        Device device2 = DatabaseTest.setupDevice2();
        DatabaseTest.setupSession();

        assertTrue(SessionQueries.isMember(DatabaseTest.SESSION_ID, device));
        assertFalse(SessionQueries.isMember(DatabaseTest.SESSION_ID, device2));

    }
}