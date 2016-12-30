package http;

import application.App;
import database.DatabaseTest;
import database.SessionQueries;
import models.Device;
import models.Session;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.HaltException;
import spark.Spark;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;

import static http.Routes.validateSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests the Routes class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Device.class, SessionQueries.class})
@PowerMockIgnore("javax.net.ssl.*")
public class RoutesTest {

    @BeforeClass
    public static void beforeClass() {
        App.main(null);
    }

    @AfterClass
    public static void afterClass() {
        Spark.stop();
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();


    /**
     * Method that makes requests and executes them.
     * @param uri The uri with the route that is supposed to be triggered.
     * @param device The json device making the request.
     * @return An http response object.
     * @throws IOException Throws an exception if the request execution fails.
     */
    public HttpResponse processAuthorizedRoute(String uri, Device device) throws IOException {
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:4567" + uri);
        httpPost.addHeader("Authorization", device.getDeviceID() + ":" + device.getToken());
        return httpClient.execute(httpPost);
    }

    /**
     * Test the route that generates a token for a new device.
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void createNewTokenTest() throws IOException {
        PowerMockito.mockStatic(Device.class);
        when(Device.newDevice(anyString())).thenReturn(new Device("some_id", "some_token"));

        String uri = "/token";
        HttpResponse response = processAuthorizedRoute(uri, new Device("deviceID", "some_id"));

        String result = EntityUtils.toString(response.getEntity());
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Test the route that generates a token for a new device, when a token already exists.
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void createDuplicateTokenTest() throws IOException {
        PowerMockito.mockStatic(Device.class);
        when(Device.newDevice(anyString())).thenReturn(null);

        String uri = "/token";
        HttpResponse response = processAuthorizedRoute(uri, new Device("deviceID", "some_id"));

        String result = EntityUtils.toString(response.getEntity());
        assertEquals("HTTP/1.1 401 Unauthorized", response.getStatusLine().toString());
    }

    @Test
    public void validateSessionTest() throws IOException {
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);
        Session session1 = new Session(DatabaseTest.SESSION_ID, DatabaseTest.PLAYER_ID, 1, LocalDateTime.now());

        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(anyString())).thenReturn(session1);
        when(SessionQueries.isMember(DatabaseTest.SESSION_ID, device)).thenReturn(true);

        Session session2 = validateSession(device, DatabaseTest.SESSION_ID);
        assertEquals(session1, session2);
    }

    @Test
    public void validateSessionFalseTest() throws IOException {
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);

        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(anyString())).thenReturn(null);
        when(SessionQueries.isMember(DatabaseTest.SESSION_ID, device)).thenReturn(false);

        exception.expect(HaltException.class);
        validateSession(device, DatabaseTest.SESSION_ID);

    }

    @Test
    public void validateSessionFalseTest2() throws IOException {
        Device device = new Device(null, null);

        exception.expect(HaltException.class);
        validateSession(device, null);

    }

    /**
     * Test whether constructor is private and does not raise any exceptions.
     *
     * @throws NoSuchMethodException     The method must be there.
     * @throws IllegalAccessException    The method must be accessible.
     * @throws InvocationTargetException The method must be invocable
     * @throws InstantiationException    The method must be instantiationable.
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<Routes> constructor = Routes.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}