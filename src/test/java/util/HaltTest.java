package util;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import spark.HaltException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;
import static util.Halt.halter;

/**
 * Created by Thomas on 9-1-2017.
 */
public class HaltTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test whether halter throws a halt exception like it should.
     */
    @Test
    public void halterTest() {
        exception.expect(HaltException.class);
        halter(HttpStatus.INTERNAL_SERVER_ERROR_500, "Test exception.");
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
        Constructor<Halt> constructor = Halt.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}
