package http;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a WebSocket connection which sends the current time to al subscribers.
 */
@WebSocket
public class TimeWebSocket {

    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

    /**
     * Sends a message to all the sessions.
     *
     * @param message the message to be sent.
     */
    public static void send(String message) {
        for (Session session : SESSIONS) {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WebSocketException e) {
                Logger.getGlobal().log(Level.WARNING, e.getMessage());
            }
        }
    }

    /**
     * Adds a session to the connected websockets.
     *
     * @param session The session
     */
    @OnWebSocketConnect
    public void connected(Session session) {
        SESSIONS.add(session);
    }

    /**
     * Closes and removes session from connected websockets.
     *
     * @param session    The session to close.
     * @param statusCode The status to close it with.
     * @param reason     The reason to close it.
     */
    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        SESSIONS.remove(session);
    }

}
