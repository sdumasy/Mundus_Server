package http;

import database.PlayerQueries;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a WebSocket connection which users can subscribe to.
 */
@WebSocket
public class SubscriptionWebSocket {
    private Map<String, Queue<Session>> sessions = new ConcurrentHashMap<>();
    private String prefix;

    /**
     * A new subscription webSocket to a specific path.
     *
     * @param path The path to subscribe with.
     */
    public SubscriptionWebSocket(String path) {
        this.prefix = path;
    }

    /**
     * Sends a message to all the sessions.
     *
     * @param id      The id of the session to connect to.
     * @param message the message to be sent.
     */
    public void send(String id, String message) {
        if (sessions.containsKey(id)) {
            for (Session session : sessions.get(id)) {
                try {
                    session.getRemote().sendString(message);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WebSocketException e) {
                    Logger.getGlobal().log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    /**
     * Adds a session to the connected web sockets.
     *
     * @param session The session
     */
    @OnWebSocketConnect
    public void connected(Session session) {
        String id = getSessionID(session);
        if (!sessions.containsKey(id)) {
            sessions.put(id, new ConcurrentLinkedQueue<>());
        }
        sessions.get(id).add(session);
    }

    /**
     * Closes and removes session from connected web sockets.
     *
     * @param session    The session to close.
     * @param statusCode The status to close it with.
     * @param reason     The reason to close it.
     */
    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        String id = getSessionID(session);
        sessions.get(id).remove(session);
        if (sessions.get(id).isEmpty()) {
            sessions.remove(id);
        }
    }

    /**
     * Retrieves the sessionID of the session.
     *
     * @param session The session.
     * @return The sessionID.
     */
    protected String getSessionID(Session session) {
        String[] authorizationValues = session.getUpgradeRequest().getHeader("Authorization").split(":");
        if (authorizationValues.length == 3) {
            return PlayerQueries.getPlayer(authorizationValues[2]).getSession().getSessionID();
        } else {
            session.close(HttpStatus.BAD_REQUEST_400, "Invalid Authorization header.");
            return null;
        }
    }

}
