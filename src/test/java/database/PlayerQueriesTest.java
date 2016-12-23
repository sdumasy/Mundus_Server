package database;

import models.Player;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static database.PlayerQueries.getPlayer;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas on 23-12-2016.
 */
public class PlayerQueriesTest {
    @BeforeClass
    public static void clean() {
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
}