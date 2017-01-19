package database;

import models.Device;
import models.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;

import static database.PlayerQueries.getPlayer;
import static database.PlayerQueries.setUsername;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the player queries.
 */
public class PlayerQueriesTest {

    /**
     * Make sure the database is clean before we do anything else.
     */
    @BeforeClass
    public static void clean() {
        DatabaseTest.clean();
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
        Constructor<PlayerQueries> constructor = PlayerQueries.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Test adding an extra player to a session and verifying its values.
     */
    @Test
    public void getPlayerTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();
            Player player = getPlayer(DatabaseTest.SESSION_ID, DatabaseTest.ADMIN_USERNAME);

            assertEquals(DatabaseTest.PLAYER_ID, player.getPlayerID());
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Test adding an extra player to a session and verifying its values.
     */
    @Test
    public void getPlayerByIDTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();
            Player player = getPlayer(DatabaseTest.PLAYER_ID);

            assertEquals(DatabaseTest.ADMIN_USERNAME, player.getUsername());
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Tests retrieving all players of a device.
     */
    @Test
    public void getAllPlayersTest() {
        try {
            Device device = DatabaseTest.setupDevice();
            DatabaseTest.setupSession();
            DatabaseTest.setupPlayer();
            List<Player> list = PlayerQueries.getAllPlayers(device);

            assertEquals(2, list.size());
            assertTrue(list.contains(getPlayer(DatabaseTest.PLAYER_ID)));
            assertTrue(list.contains(getPlayer(DatabaseTest.PLAYER_ID_2)));
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }

    /**
     * Tests setting the username of a player.
     */
    @Test
    public void setUsernameTest() {
        try {
            DatabaseTest.setupDevice();
            DatabaseTest.setupSession();
            Player player = getPlayer(DatabaseTest.PLAYER_ID);

            assertEquals(getPlayer(player.getPlayerID()).getUsername(), DatabaseTest.ADMIN_USERNAME);

            setUsername(player, DatabaseTest.USERNAME);

            assertEquals(getPlayer(player.getPlayerID()).getUsername(), DatabaseTest.USERNAME);
        } finally {
            DatabaseTest.cleanDatabase();
        }
    }
}