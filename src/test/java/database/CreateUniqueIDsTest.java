package database;

import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static database.CreateUniqueIDs.generateUniqueID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Thomas on 22-12-2016.
 */
public class CreateUniqueIDsTest {

    /**
     * Make sure the database is clean before we do anything else.
     */
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
        Constructor<CreateUniqueIDs> constructor = CreateUniqueIDs.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Generate a new ID.
     */
    @Test
    public void generateUniqueIDTest() {
        assertNotNull(generateUniqueID("device", "auth_token"));
    }

    /**
     * Generate a new ID for a different table.
     */
    @Test
    public void generateUniqueIDTest2() {
        assertNotNull(generateUniqueID("session_player", "player_id"));
    }

    /**
     * Generate a new ID for a different table.
     */
    @Test
    public void generateUniqueIDTest3() {
        assertNotNull(generateUniqueID("session", "session_id"));
    }

}