//package validation;
//
//import org.junit.Test;
//
//import java.lang.reflect.Constructor;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Modifier;
//import java.sql.Connection;
//
//import static database.Database.executeManipulationQuery;
//import static org.junit.Assert.assertFalse;
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Matchers.anyString;
//import static validation.Validation.authenticateDevice;
//import static validation.Validation.createToken;
//import static validation.Validation.hasToken;
//
//
///**
// * Created by Thomas on 18-12-2016.
// */
//public class ValidationTest {
//    private Connection connection;
//
//
//
//    /**
//     * Test a successful authentication.
//     */
//    @Test
//    public void authenticateDeviceTestTrue() {
//        setup();
//        assertTrue(authenticateDevice("42", "true"));
//        tearDown();
//    }
//
//    /**
//     * Test a failed authentication.
//     */
//    @Test
//    public void authenticateDeviceTestFalse() {
//        setup();
//        assertFalse(authenticateDevice("123", "false"));
//        tearDown();
//    }
//
//    /**
//     * Test another failed authentication.
//     */
//    @Test
//    public void authenticateDeviceTestFalseNonExistent() {
//        tearDown();
//        assertFalse(authenticateDevice("42", anyString()));
//    }
//
//    /**
//     * Test the creation of a token.
//     */
//    @Test
//    public void createTokenTest() {
//        String expectedToken = createToken("42");
//        assertTrue(authenticateDevice("42", expectedToken));
//        tearDown();
//    }
//
//    /**
//     * Test whether a device has a token when it should have one.
//     */
//    @Test
//    public void hasTokenTestTrue() {
//        setup();
//        assertTrue(hasToken("42"));
//        tearDown();
//    }
//
//    /**
//     * Test whether a device has a token when it should have none.
//     */
//    @Test
//    public void hasTokenTestFalse() {
//        tearDown();
//        assertFalse(hasToken("42"));
//    }
//
//    /**
//     * Test whether constructor is private and does not raise any exceptions.
//     * @throws NoSuchMethodException The method must be there.
//     * @throws IllegalAccessException The method must be accessible.
//     * @throws InvocationTargetException The method must be invocable
//     * @throws InstantiationException The method must be instantiationable.
//     */
//    @Test
//    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
//            InvocationTargetException, InstantiationException {
//        Constructor<Validation> constructor = Validation.class.getDeclaredConstructor();
//        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
//        constructor.setAccessible(true);
//        constructor.newInstance();
//    }
//
//}