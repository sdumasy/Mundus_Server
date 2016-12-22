package models;

import com.google.gson.JsonObject;
import database.DatabaseTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import spark.HaltException;

import static database.Database.executeManipulationQuery;
import static models.Player.getPlayer;
import static models.Role.Admin;
import static models.Role.getById;
import static org.junit.Assert.*;

/**
 * Test of the player class.
 */
public class PlayerTest {
    private Player player;
    private Device device;
    private String deviceID = "deviceID_42";
    private String token = "token_42";
    private String playerID = "playerID_42";
    private String sessionID = "sessionID_42";

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @BeforeClass
    public static void clean() {
        DatabaseTest.cleanDatabase();
    }

    /**
     * Initialize the player constructor.
     */
    @Before
    public void setupPlayer() {
        device = new Device(deviceID, token);
        player = new Player(playerID, sessionID, device, Admin, 42);
    }

    /**
     * Test addition of a new Player to a session.
     */
    @Test
    public void newPlayerTest() {
        DatabaseTest.setupDevice();
        DatabaseTest.setupSession();

        Player newPlayer = Player.newPlayer(DatabaseTest.SESSION_ID, 2,
                new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN), 42);
        assertEquals(newPlayer.getDevice().getDeviceID(), DatabaseTest.DEVICE_ID);
        assertEquals(newPlayer.getSessionID(), DatabaseTest.SESSION_ID);
        assertEquals(newPlayer.getRoleID(), 2);

        executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + newPlayer.getPlayerID() + "';");
        DatabaseTest.cleanDatabase();
    }

    /**
     * Test addition of a new Player to a session.
     */
    @Test
    public void newPlayerDuplicateTest() {
        DatabaseTest.setupDevice();
        DatabaseTest.setupSession();
        try {
            exception.expect(HaltException.class);
            Player newPlayer = Player.newPlayer(DatabaseTest.SESSION_ID, 0,
                    new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN), 42);
        } finally {
            DatabaseTest.cleanDatabase();
        }

    }

    /**
     * Test whether player properlyID is set properly.
     */
    @Test
    public void getPlayerIDTest() {
        assertEquals(player.getPlayerID(), "playerID_42");
    }

    /**
     * Test whether player sessionID is set properly.
     */
    @Test
    public void getSessionIDTest() {
        assertEquals(player.getSessionID(), "sessionID_42");
    }

    /**
     * Test whether player deviceID is set properly.
     */
    @Test
    public void getDeviceIDTest() {
        assertEquals(player.getDevice().getDeviceID(), "deviceID_42");
    }

    /**
     *  Test whether player role is set properly.
     */
    @Test
    public void getAndSetRoleIDTest() {
        player.setRoleID(0);
        assertEquals(player.getRoleID(), 0);
    }

    /**
     *  Test whether player role name is returned properly.
     */
    @Test
    public void getRoleTest() {
        player.setRoleID(0);
        assertEquals(player.getRole(), getById(0));
    }

    /**
     *  Test whether player is an admin.
     */
    @Test
    public void isAdminTrueTest() {
        player.setRoleID(0);
        assertTrue(player.isAdmin());
    }

    /**
     * Test whether player is an admin.
     */
    @Test
    public void isAdminFalseTest() {
        player.setRoleID(1);
        assertFalse(player.isAdmin());
    }

    /**
     * Test the setting and getting of a players score.
     */
    @Test
    public void getAndSetScoreTest() {
        player.setScore(42);
        assertEquals(player.getScore(), 42);
    }

    /**
     * Test the return of a players attributes as a JsonObject.
     */
    @Test
    public void toJsonTest() {
        JsonObject jsonObject = player.toJson();

        assertEquals("playerID_42", jsonObject.get("playerID").getAsString());
        assertEquals("sessionID_42", jsonObject.get("sessionID").getAsString());
        assertEquals("Admin", jsonObject.get("role").getAsString());
        assertEquals("42", jsonObject.get("score").getAsString());
    }

    /**
     * Test getting a player object by ID.
     */
    @Test
    public void getPlayerTest() {
        DatabaseTest.setupDevice();
        DatabaseTest.setupSession();

        Player player = new Player(DatabaseTest.PLAYER_ID, DatabaseTest.SESSION_ID,
                new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN), Admin, 0);
        assertEquals(player.getSessionID(), getPlayer(DatabaseTest.PLAYER_ID).getSessionID());
        assertEquals(player.getDevice().getDeviceID(), getPlayer(DatabaseTest.PLAYER_ID).getDevice().getDeviceID());
        assertEquals(player.getRole(), getPlayer(DatabaseTest.PLAYER_ID).getRole());
        assertEquals(player.getScore(), getPlayer(DatabaseTest.PLAYER_ID).getScore());

        DatabaseTest.cleanDatabase();

    }

}