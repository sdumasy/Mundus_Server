package http;

import application.App;
import com.google.gson.JsonObject;
import database.AuthenticationTokenQueries;
import database.DatabaseTest;
import database.PlayerQueries;
import database.SessionQueries;
import models.Device;
import models.Player;
import models.Role;
import models.Session;
import org.apache.http.HttpResponse;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.HaltException;
import spark.Spark;

import java.io.IOException;
import java.time.LocalDateTime;

import static http.RoutesSession.validateSession;
import static http.RoutesTest.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 3-1-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SessionQueries.class, AuthenticationTokenQueries.class, PlayerQueries.class})
@PowerMockIgnore("javax.net.ssl.*")
public class RoutesSessionTest {
    private static final String MOCKED_TOKEN = "some_token";
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Start the spark framework.
     */
    @BeforeClass
    public static void beforeAll() {
        App.main(null);
    }

    /**
     * Stop the spark framework.
     */
    @AfterClass
    public static void after() {
        Spark.stop();
    }

    /**
     * Mock the token validation and always return the same token.
     */
    @Before
    public  void before() {
        PowerMockito.mockStatic(AuthenticationTokenQueries.class);
        when(AuthenticationTokenQueries.selectAuthorizationToken(anyString())).thenReturn(MOCKED_TOKEN);
    }

    /**
     * Try creating a session.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupCreateSessionTest() throws IOException {
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.createSession(any(), any())).thenReturn(new JsonObject());

        String uri = "/session/username/some_username";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("Device_ID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Try joining a session with invalid credentials.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupJoinSessionFailureTest() throws IOException {
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.createSession(any(), any())).thenReturn(new JsonObject());
        when(SessionQueries.playerJoinSession(any(), any(), any())).thenReturn(null);

        String uri = "/session/join/some_token/username/some_username";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("Device_ID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 401 Unauthorized", response.getStatusLine().toString());
    }

    /**
     * Try joining a session.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupJoinSessionSuccesTest() throws IOException {
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.createSession(any(), any())).thenReturn(new JsonObject());
        Device device = new Device("Device_ID", MOCKED_TOKEN);
        Session session = new Session("", "", 1, LocalDateTime.now());
        when(SessionQueries.playerJoinSession(any(), any(), any())).thenReturn(
                new Player("", session, device, Role.Admin, 0, ""));

        String uri = "/session/join/some_token/username/some_username";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("Device_ID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Try performing an action on a session you are not a part of.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupUnauthorizedSessionTest() throws IOException {
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(any())).thenReturn(null);
        String uri = "/session/some_id/some_action";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("Device_ID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 400 Bad Request", response.getStatusLine().toString());
    }

    /**
     * Try getting information about a session by sessionID.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupGetSessionTest() throws IOException {
        PowerMockito.mockStatic(SessionQueries.class);
        Session session = new Session(DatabaseTest.SESSION_ID, DatabaseTest.PLAYER_ID, 1, LocalDateTime.now());
        when(SessionQueries.getSession(any())).thenReturn(session);
        when(SessionQueries.isMember(any(), any())).thenReturn(true);

        String uri = "/session/some_id";
        HttpResponse response = processAuthorizedGetRoute(uri, new Device("Device_ID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Verify that only admins can change the session status.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void validateAuthorizedSessionManageTest() throws IOException {
        Device device = new Device("DeviceID", MOCKED_TOKEN);
        Session session = new Session("SomeID", "AdminID", 1, LocalDateTime.now());
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(anyString())).thenReturn(session);
        when(SessionQueries.isMember(any(), any())).thenReturn(true);

        PowerMockito.mockStatic(PlayerQueries.class);
        when(PlayerQueries.getPlayer(any())).thenReturn(new Player("AdminID", session, device, Role.Admin, 0, ""));

        String uri = "/session/some_id/manage/some_action";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("DeviceID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 405 HTTP method POST is not supported by this URL", response.getStatusLine().toString());
    }

    /**
     * Verify that only admins can change the session status.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void validateUnauthorizedSessionManageTest() throws IOException {
        Device device = new Device("OtherID", MOCKED_TOKEN);
        Session session = new Session("SomeID", "AdminID", 1, LocalDateTime.now());
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(anyString())).thenReturn(session);
        when(SessionQueries.isMember(any(), any())).thenReturn(true);

        PowerMockito.mockStatic(PlayerQueries.class);
        when(PlayerQueries.getPlayer(any())).thenReturn(new Player("AdminID", session, device, Role.Admin, 0, ""));

        String uri = "/session/some_id/manage/some_action";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("DeviceID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 403 Forbidden", response.getStatusLine().toString());
    }

    /**
     * Verify that only admins can change the session status.
     * @throws IOException Throws an exception if the request execution fails.
     */
    public void setSessionStatusSetup() {
        Device device = new Device("DeviceID", MOCKED_TOKEN);
        Session session = new Session("SomeID", "AdminID", 1, LocalDateTime.now());
        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(anyString())).thenReturn(session);
        when(SessionQueries.isMember(any(), any())).thenReturn(true);
        PowerMockito.mockStatic(PlayerQueries.class);
        when(PlayerQueries.getPlayer(any())).thenReturn(new Player("AdminID", session, device, Role.Admin, 0, ""));
    }

    /**
     * Try to resume session.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setSessionStatusPlayTest() throws IOException {
        setSessionStatusSetup();

        String uri = "/session/some_id/manage/play";
        HttpResponse response = processAuthorizedPutRoute(uri, new Device("DeviceID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Try to pause an active session.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setSessionStatusPauseTest() throws IOException {
        setSessionStatusSetup();

        String uri = "/session/some_id/manage/pause";
        HttpResponse response = processAuthorizedPutRoute(uri, new Device("DeviceID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Try to delete an active session.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setSessionStatusDeleteTest() throws IOException {
        setSessionStatusSetup();

        String uri = "/session/some_id/manage/delete";
        HttpResponse response = processAuthorizedDeleteRoute(uri, new Device("DeviceID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Test a successful session validation.
     * @throws IOException Throws an exception if the request execution fails.
     */
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

    /**
     * Test a failing session validation.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void validateSessionFalseTest() throws IOException {
        Device device = new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN);

        PowerMockito.mockStatic(SessionQueries.class);
        when(SessionQueries.getSession(anyString())).thenReturn(null);
        when(SessionQueries.isMember(DatabaseTest.SESSION_ID, device)).thenReturn(false);

        exception.expect(HaltException.class);
        validateSession(device, DatabaseTest.SESSION_ID);
    }
}
