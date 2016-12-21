package database;

import com.google.gson.JsonObject;
import models.Player;
import org.junit.Test;
import validation.Validation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static database.Database.executeSearchQuery;
import static database.SessionQueries.*;
import static org.junit.Assert.*;

/**
 * Created by Thomas on 20-12-2016.
 */
public class SessionQueriesTest {
    private String deviceID = "deviceID_42";
    private String localDevice = "device_ID43";
    private String playerID;
    private String userToken;
    private String modToken;
    private String sessionID;


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
            createSessionSetup();
            List<Map<String, Object>> verify;

            verify = Database
                    .executeSearchQuery("SELECT player_id FROM session_player WHERE player_id='" + playerID + "';");
            assertEquals(verify.size(), 1);

            verify = Database
                    .executeSearchQuery("SELECT join_token FROM session_token WHERE join_token='" + userToken + "';");
            assertEquals(verify.size(), 1);

            verify = Database
                    .executeSearchQuery("SELECT join_token FROM session_token WHERE join_token='" + modToken + "';");
            assertEquals(verify.size(), 1);

            verify = Database
                    .executeSearchQuery("SELECT session_id FROM session WHERE session_id='" + sessionID + "';");
            assertEquals(verify.size(), 1);
        } finally {
            createSessionTearDown();
        }
    }

    /**
     * Setup a session so tests can be run within a session setting.
     */
    private void createSessionSetup() {
        Validation.createToken(deviceID);
        JsonObject jsonObject = createSession(deviceID);
        playerID = jsonObject.get("playerID").getAsString();
        userToken = jsonObject.get("userToken").getAsString();
        modToken = jsonObject.get("modToken").getAsString();
        sessionID = jsonObject.get("sessionID").getAsString();
    }

    /**
     * Clean up a session after testing is done.
     */
    private void createSessionTearDown() {
        Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + playerID + "';");
        Database.executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + userToken + "';");
        Database.executeManipulationQuery("DELETE FROM session_token WHERE join_token='" + modToken + "';");
        Database.executeManipulationQuery("DELETE FROM session WHERE session_id='" + sessionID + "';");
        Database.executeManipulationQuery("DELETE FROM device WHERE device_id='" + deviceID + "';");
    }

    /**
     * Test adding a new player to a running session.
     */
    @Test
    public void getNewPlayerOfSessionTest() {
        String localDevice = "device_ID43";
        Player player = new Player("", "", "");
        try {
            Validation.createToken(localDevice);
            createSessionSetup();
            player = getNewPlayerOfSession(userToken, localDevice);
            String query = "SELECT * FROM session_player WHERE device_id='" + localDevice + "';";
            List<Map<String, Object>> verify = executeSearchQuery(query);
            assertEquals(verify.size(), 1);

        } finally {
            Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + player.getPlayerID() + "';");
            createSessionTearDown();
            Database.executeManipulationQuery("DELETE FROM device WHERE device_id='" + localDevice + "';");
        }
    }

    /**
     * Test adding an extra player to a session and verifying its values.
     */
    @Test
    public void setGetPlayer() {

        try {
            Player player = addNewPlayerSetup();

            Map<String, Object> map = getPlayerData(player);
            assertEquals("playerID_42", (String) map.get("player_id"));
            assertEquals(sessionID, (String) map.get("session_id"));
            assertEquals(localDevice, (String) map.get("device_id"));
        } finally {
            addNewPlayerTearDown();
        }
    }

    /**
     * Creates a session setup with one extra player added.
     * @return The extra player
     */
    public Player addNewPlayerSetup() {
        Validation.createToken(localDevice);
        createSessionSetup();
        Player player = new Player("playerID_42", sessionID, localDevice);
        player.setRoleID(0);
        player.setScore(42);
        addNewPlayer(player);
        return player;
    }

    /**
     * Deletes the extra player and tears down the session.
     */
    private void addNewPlayerTearDown() {
        Database.executeManipulationQuery("DELETE FROM session_player WHERE player_id='" + "playerID_42" + "';");
        createSessionTearDown();
        Database.executeManipulationQuery("DELETE FROM device WHERE device_id='" + localDevice + "';");
    }

    /**
     * Test getting the role of a player.
     */
    @Test
    public void getRoleIdTest() {
        try {
            Player player = addNewPlayerSetup();
            assertEquals(0, (int) getRoleId(player));
        } finally {
            addNewPlayerTearDown();
        }
    }

    /**
     * Test getting the score of a player.
     */
    @Test
    public void getScoreTest() {
        try {
            Player player = addNewPlayerSetup();
            assertEquals(42, (int) getScore(player));
        } finally {
            addNewPlayerTearDown();
        }
    }

    /**
     * Get the current state of the session player is in.
     */
    @Test
    public void getSessionStatusTest() {
        try {
            Player player = addNewPlayerSetup();
            assertEquals(1, (int) getSessionStatus(player));
        } finally {
            addNewPlayerTearDown();
        }
    }

    /**
     * Test if the administrator can change the state of the session.
     */
    @Test
    public void updateSessionStatusTest() {
        try {
            createSessionSetup();
            Player player = new Player(playerID, sessionID, deviceID);
            player.setRoleID(0);
            player.setScore(42);
            updateSessionStatus(player, 2);
            assertEquals(2, (int) getSessionStatus(player));
        } finally {
            createSessionTearDown();
        }
    }

}