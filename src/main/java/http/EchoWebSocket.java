package http;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Creates a WebSocket connection which sends the same message back.
 */
@WebSocket
public class EchoWebSocket {

    // Store SESSIONS if you want to, for example, broadcast a message to all users
    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

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

    /**
     * Receives a message over the websocket.
     *
     * @param session The session it receives it from.
     * @param message The message received.
     * @throws IOException Could fail at sending it back.
     */
    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }

}
