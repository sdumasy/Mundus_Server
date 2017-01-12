package framework;

import com.google.gson.JsonObject;
import http.Routes;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.Request;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Tests the aldo class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Routes.class})
public class AldoTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

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
        Constructor<Aldo> constructor = Aldo.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Bad weather test for addRoute.
     */
    @Test
    public void testAddRouteBad() {
        Runnable spyRunnable = spy(Runnable.class);

        Aldo.addRoute(spyRunnable);

        verify(spyRunnable, times(0)).run();
    }

    /**
     * Good weather test for addRoute.
     */
    @Test
    public void testAddRouteGood() {
        Runnable spyRunnable = spy(Runnable.class);

        Aldo.addRoute(spyRunnable);
        Aldo.start();

        verify(spyRunnable, times(1)).run();
    }


    /**
     * Bad weather test for setupGameLoop.
     */
    @Test
    public void testSetupGameLoopBad() {
        Runnable spyRunnable = spy(Runnable.class);

        exception.expect(IllegalArgumentException.class);
        Aldo.setupGameLoop(spyRunnable, 0);

        reset(spyRunnable);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(spyRunnable, after(30).atLeast(2)).run();
    }

    /**
     * Good weather test for setupGameLoop.
     */
    @Test
    public void testSetupGameLoopGood() {
        Runnable spyRunnable = spy(Runnable.class);

        Aldo.setupGameLoop(spyRunnable, 10);

        verify(spyRunnable, timeout(40).atLeast(2)).run();

        Aldo.stopGameLoop();
        reset(spyRunnable);
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        verify(spyRunnable, after(30).atMost(1)).run();
    }

    /**
     * Isolates the toRoute method from the validatePlayer method.
     *
     * @param requestHandler The requestHandler that is given to the toRoute method.
     * @return The ValidPlayerRoute that is given to the validatePlayer method.
     */
    private Routes.ValidPlayerRoute isolateToRoute(RequestHandler requestHandler) {
        PowerMockito.mockStatic(Routes.class);

        ArgumentCaptor<Routes.ValidPlayerRoute> validPlayerRouteCaptor =
                ArgumentCaptor.forClass(Routes.ValidPlayerRoute.class);

        when(Routes.validatePlayer(validPlayerRouteCaptor.capture())).thenReturn(null);

        Aldo.toRoute(requestHandler);
        return validPlayerRouteCaptor.getValue();
    }

    /**
     * Tests whether the json is actually empty when no body is given.
     */
    @Test
    public void testToRouteBodyBad() {
        Routes.ValidPlayerRoute validPlayerRoute = isolateToRoute((player, json) -> {
            assertEquals(json.size(), 0);
            return new JsonObject();
        });

        Request spyRequest = mock(Request.class);
        when(spyRequest.body()).thenReturn("");
        validPlayerRoute.handle(spyRequest, null);
    }

    /**
     * Tests whether the body is put in the json object.
     */
    @Test
    public void testToRouteBodyGood() {
        Routes.ValidPlayerRoute validPlayerRoute = isolateToRoute((player, json) -> {
            assertTrue(json.has("body"));
            assertEquals("value", json.get("body").getAsString());
            return new JsonObject();
        });

        Request spyRequest = mock(Request.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("body", "value");
        when(spyRequest.body()).thenReturn(jsonObject.toString());
        validPlayerRoute.handle(spyRequest, null);
    }

    /**
     * Tests whether the json is actually empty when no parameters are given.
     */
    @Test
    public void testToRouteParamsBad() {
        Routes.ValidPlayerRoute validPlayerRoute = isolateToRoute((player, json) -> {
            assertEquals(json.size(), 0);
            return new JsonObject();
        });

        Request spyRequest = mock(Request.class);
        when(spyRequest.body()).thenReturn("");
        Map<String, String> map = new HashMap<>();
        when(spyRequest.params()).thenReturn(map);
        validPlayerRoute.handle(spyRequest, null);
    }

    /**
     * Tests whether the parameters are put in the json object.
     */
    @Test
    public void testToRouteParamsGood() {
        Routes.ValidPlayerRoute validPlayerRoute = isolateToRoute((player, json) -> {
            assertEquals(json.size(), 2);
            assertTrue(json.has("key1"));
            assertEquals("value1", json.get("key1").getAsString());
            assertTrue(json.has("key2"));
            assertEquals("value2", json.get("key2").getAsString());
            return new JsonObject();
        });

        Request spyRequest = mock(Request.class);
        when(spyRequest.body()).thenReturn("");
        Map<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        when(spyRequest.params()).thenReturn(map);
        validPlayerRoute.handle(spyRequest, null);
    }
}