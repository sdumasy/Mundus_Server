package validation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.sql.Connection;

import static database.Database.executeUpdateQuery;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static validation.Validation.authenticateDevice;
import static validation.Validation.createToken;
import static validation.Validation.hasToken;


/**
 * Created by Thomas on 18-12-2016.
 */
public class ValidationTest {
    private Connection connection;


    public void setup() {
        executeUpdateQuery("INSERT INTO device VALUES ('" + 42 + "','" + "true" + "');");
    }

    public void tearDown() {
        executeUpdateQuery("DELETE FROM device WHERE device_id='42';");
    }

    /**
     * Test a successful authentication.
     */
    @Test
    public void authenticateDeviceTestTrue() {
        setup();
        assertTrue(authenticateDevice("42", "true"));
        tearDown();
    }

    /**
     * Test a failed authentication.
     */
    @Test
    public void authenticateDeviceTestFalse() {
        setup();
        assertFalse(authenticateDevice("123", "false"));
        tearDown();
    }

    /**
     * Test another failed authentication.
     */
    @Test
    public void authenticateDeviceTestFalseNonExistent() {
        tearDown();
        assertFalse(authenticateDevice("42", anyString()));
    }

    /**
     * Test the creation of a token.
     */
    @Test
    public void createTokenTest() {
        String expectedToken = createToken("42");
        assertTrue(authenticateDevice("42", expectedToken));
        tearDown();
    }

    /**
     * Test whether a device has a token when it should have one.
     */
    @Test
    public void hasTokenTestTrue() {
        setup();
        assertTrue(hasToken("42"));
        tearDown();
    }

    /**
     * Test whether a device has a token when it should have none.
     */
    @Test
    public void hasTokenTestFalse() {
        tearDown();
        assertFalse(hasToken("42"));
    }

    /**
     * Test whether constructor is private and does not raise any exceptions.
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<Validation> constructor = Validation.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

}