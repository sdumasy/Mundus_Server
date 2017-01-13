package http;

import application.App;
import database.AuthenticationTokenQueries;
import database.SessionQueries;
import models.Device;
import org.apache.http.HttpResponse;
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

import java.io.IOException;

import static http.RoutesTest.processAuthorizedGetRoute;
import static http.RoutesTest.processAuthorizedPostRoute;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests the Routes class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Device.class, SessionQueries.class, AuthenticationTokenQueries.class})
@PowerMockIgnore("javax.net.ssl.*")
public class RoutesTokenValidationTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Start the spark framework.
     */
    @BeforeClass
    public static void beforeClass() {
        App.main(null);
    }

    /**
     * Stop the spark framework.
     */
    @AfterClass
    public static void afterClass() {
        App.stop();
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
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("deviceID", "some_id"));
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
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("deviceID", "some_id"));
        assertEquals("HTTP/1.1 401 Unauthorized", response.getStatusLine().toString());
    }

    /**
     * Test the route that generates a token for a new device, when a token already exists.
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void unauthorizedRequestTest() throws IOException {
        PowerMockito.mockStatic(AuthenticationTokenQueries.class);
        when(AuthenticationTokenQueries.selectAuthorizationToken(anyString())).thenReturn(null);

        String uri = "/player";
        HttpResponse response = processAuthorizedGetRoute(uri, "deviceID", "some_token");
        assertEquals("HTTP/1.1 401 Unauthorized", response.getStatusLine().toString());
    }

    /**
     * Test the route that generates a token for a new device, when a token already exists.
     *
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void authorizedRequestTest() throws IOException {
        PowerMockito.mockStatic(AuthenticationTokenQueries.class);
        when(AuthenticationTokenQueries.selectAuthorizationToken(anyString())).thenReturn("some_token");

        String uri = "/someRequest";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("deviceID", "some_token"));
        assertEquals("HTTP/1.1 405 HTTP method POST is not supported by this URL", response.getStatusLine().toString());
    }

}