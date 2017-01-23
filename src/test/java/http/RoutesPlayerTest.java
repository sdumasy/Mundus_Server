package http;

import application.App;
import database.*;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static http.RoutesTest.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Tests player routes.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SessionQueries.class, AuthenticationTokenQueries.class, PlayerQueries.class})
@PowerMockIgnore("javax.net.ssl.*")
public class RoutesPlayerTest {
    private static final String MOCKED_TOKEN = "some_token";
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Start the spark framework.
     */
    @BeforeClass
    public static void beforeAll() {
        DatabaseTest.clean();
        App.main(null);
    }

    /**
     * Stop the spark framework.
     */
    @AfterClass
    public static void after() {
        App.stop();
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
     *  Try getting all players coupled to your device.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupGetAllPlayersTest() throws IOException {
        PowerMockito.mockStatic(PlayerQueries.class);
        Device device = new Device("Device_ID", MOCKED_TOKEN);
        Session session = new Session("", "", 1, LocalDateTime.now());
        List players = new ArrayList<Player>();
        players.add(new Player("1", session, device, Role.Admin, 0, ""));
        players.add(new Player("2", session, device, Role.Admin, 0, ""));
        when(PlayerQueries.getAllPlayers(any())).thenReturn(players);
        String uri = "/player/all";
        HttpResponse response = processAuthorizedGetRoute(uri, "Device_ID", MOCKED_TOKEN);
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Try to perform an action with a certain player object.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupPlayerValidationSuccessTest() throws IOException {
        PowerMockito.mockStatic(PlayerQueries.class);
        Device device = new Device("Device_ID", MOCKED_TOKEN);
        Session session = new Session("", "", 1, LocalDateTime.now());
        when(PlayerQueries.getPlayer(any())).thenReturn(
                new Player("", session, device, Role.Admin, 0, ""));

        String uri = "/player/some_playerID/some_action";
        HttpResponse response = processAuthorizedPostRoute(uri, new Device("Device_ID", MOCKED_TOKEN));
        assertEquals("HTTP/1.1 405 HTTP method POST is not supported by this URL", response.getStatusLine().toString());
    }

    /**
     * Try to get the player object corresponding with the provided ID.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupGetPlayerTest() throws IOException {
        PowerMockito.mockStatic(PlayerQueries.class);
        Device device = new Device("Device_ID", MOCKED_TOKEN);
        Session session = new Session("some_playerID", "", 1, LocalDateTime.now());
        when(PlayerQueries.getPlayer(any())).thenReturn(
                new Player("", session, device, Role.Admin, 0, ""));

        String uri = "/player";
        HttpResponse response = processAuthorizedGetRoute(uri, "Device_ID", MOCKED_TOKEN, "playerID");
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }

    /**
     * Try to get a player object that does not exist.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupGetPlayerNullTest() throws IOException {
        PowerMockito.mockStatic(PlayerQueries.class);
        when(PlayerQueries.getPlayer(any())).thenReturn(null);

        String uri = "/player";
        HttpResponse response = processAuthorizedGetRoute(uri, "Device_ID", MOCKED_TOKEN, null);
        assertEquals("HTTP/1.1 500 Server Error", response.getStatusLine().toString());
    }

    /**
     * Try to change username but it fails (for instance due to a duplicate entry).
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupChangeUsernameFailureTest() throws IOException {
        PowerMockito.mockStatic(PlayerQueries.class);
        Device device = new Device("Device_ID", MOCKED_TOKEN);
        Session session = new Session("some_playerID", "", 1, LocalDateTime.now());
        when(PlayerQueries.getPlayer(any())).thenReturn(
                new Player("", session, device, Role.Admin, 0, ""));
        when(PlayerQueries.setUsername(any(), any())).thenReturn(false);

        String uri = "/player/username/some_username";
        HttpResponse response = processAuthorizedPutRoute(uri, "Device_ID", MOCKED_TOKEN, "player");
        assertEquals("HTTP/1.1 500 Server Error", response.getStatusLine().toString());
    }

    /**
     * Try to change the username of your player.
     * @throws IOException Throws an exception if the request execution fails.
     */
    @Test
    public void setupChangeUsernameSuccessTest() throws IOException {
        PowerMockito.mockStatic(PlayerQueries.class);
        Device device = new Device("Device_ID", MOCKED_TOKEN);
        Session session = new Session("some_playerID", "", 1, LocalDateTime.now());
        when(PlayerQueries.getPlayer(any())).thenReturn(
                new Player("", session, device, Role.Admin, 0, ""));
        when(PlayerQueries.setUsername(any(), any())).thenReturn(true);

        String uri = "/player/username/some_username";
        HttpResponse response = processAuthorizedPutRoute(uri, "Device_ID", MOCKED_TOKEN, "player");
        assertEquals("HTTP/1.1 200 OK", response.getStatusLine().toString());
    }
}
