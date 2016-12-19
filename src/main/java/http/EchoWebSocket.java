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
 * Created by macbookpro on 03/12/2016.
 */


@WebSocket
public class EchoWebSocket {

    // Store SESSIONS if you want to, for example, broadcast a message to all users
    private static final Queue<Session> SESSIONS = new ConcurrentLinkedQueue<>();

    /**
     *
     * @param session
     */
    @OnWebSocketConnect
    public void connected(Session session) {
        SESSIONS.add(session);
    }

    /**
     *
     * @param session
     * @param statusCode
     * @param reason
     */
    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        SESSIONS.remove(session);
    }

    /**
     *
     * @param session
     * @param message
     * @throws IOException
     */
    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        System.out.println("Got: " + message);   // Print message
        session.getRemote().sendString(message); // and send it back
    }

}
