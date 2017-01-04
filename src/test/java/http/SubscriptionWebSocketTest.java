package http;


import database.PlayerQueries;
import models.Player;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests the subscriptionWebSocket class.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Session.class, PlayerQueries.class})
@PowerMockIgnore("javax.net.ssl.*")
public class SubscriptionWebSocketTest {
    private static final String ID = "id";
    private Session sessionMock;

    /**
     * Builds a mock of a session.
     */
    @Before
    public void setUp() {
        sessionMock = PowerMockito.mock(Session.class);
        UpgradeRequest upgradeRequest = PowerMockito.mock(UpgradeRequest.class);
        when(sessionMock.getUpgradeRequest()).thenReturn(upgradeRequest);
        when(upgradeRequest.getHeader(any())).thenReturn("device:token:player");

        PowerMockito.mockStatic(PlayerQueries.class);
        Player player = PowerMockito.mock(Player.class);
        when(PlayerQueries.getPlayer("player")).thenReturn(player);
        models.Session session = PowerMockito.mock(models.Session.class);
        when(player.getSession()).thenReturn(session);
        when(session.getSessionID()).thenReturn(ID);

    }

    /**
     * Tests retrieving a sessionID from a session.
     */
    @Test
    public void getSessionIDTest() {
        SubscriptionWebSocket socket = new SubscriptionWebSocket();
        assertEquals(socket.getSessionID(sessionMock), ID);
    }

    /**
     * Tests the connecting of a session.
     */
    @Test
    public void connectedTest() {
        SubscriptionWebSocket socket = new SubscriptionWebSocket();
        assertEquals(socket.getSessions().size(), 0);
        assertFalse(socket.getSessions().keySet().contains(ID));

        socket.connected(sessionMock);

        assertEquals(socket.getSessions().size(), 1);
        assertTrue(socket.getSessions().keySet().contains(ID));
        assertTrue(socket.getSessions().get(ID).contains(sessionMock));
    }

    /**
     * Tests the closing of a session.
     */
    @Test
    public void closedTest() {
        SubscriptionWebSocket socket = new SubscriptionWebSocket();
        socket.getSessions().put(ID, new ConcurrentLinkedQueue<>());
        socket.getSessions().get(ID).add(sessionMock);
        assertTrue(socket.getSessions().keySet().contains(ID));
        assertTrue(socket.getSessions().get(ID).contains(sessionMock));

        socket.closed(sessionMock, 0, "");

        assertFalse(socket.getSessions().keySet().contains(ID));
    }

    /**
     * Good weather test for sending a message.
     *
     * @throws IOException SendString might throw a error.
     */
    @Test
    public void sendTestGood() throws IOException {
        RemoteEndpoint remote = spy(RemoteEndpoint.class);
        when(sessionMock.getRemote()).thenReturn(remote);

        SubscriptionWebSocket socket = new SubscriptionWebSocket();
        socket.connected(sessionMock);
        socket.send(ID, "message");

        verify(remote, times(1)).sendString("message");
    }

    /**
     * Bad weather test for sending a message.
     *
     * @throws IOException SendString might throw a error.
     */
    @Test
    public void sendTestBad() throws IOException {
        RemoteEndpoint remote = spy(RemoteEndpoint.class);
        when(sessionMock.getRemote()).thenReturn(remote);

        SubscriptionWebSocket socket = new SubscriptionWebSocket();
        socket.send(ID, "message");

        verify(remote, times(0)).sendString("message");
    }
}
