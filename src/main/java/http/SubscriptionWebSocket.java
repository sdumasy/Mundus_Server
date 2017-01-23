package http;

import models.Player;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

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

    /**
     * Sends a message to the specified sessions.
     *
     * @param id      The id of the session to connect to.
     * @param message The message to be sent.
     */
    public void send(String id, String message) {
        if (sessions.containsKey(id)) {
            for (Session session : sessions.get(id)) {
                try {
                    session.getRemote().sendString(message);
                } catch (Exception e) {
                    Logger.getGlobal().log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    /**
     * Sends a message to all sessions.
     *
     * @param message The message to be sent.
     */
    public void sendAll(String message) {
        for (String id : sessions.keySet()) {
            send(id, message);
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
     * @param code       code of the close reason.
     * @param reason    Reason message of close.
     */
    @OnWebSocketClose
    public void closed(Session session, int code, String reason) {
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
        if (authorizationValues.length < 3) {
            session.close(HttpStatus.BAD_REQUEST_400, "Invalid Authorization header.");
        }
        return Player.getPlayer(authorizationValues[2]).getSession().getSessionID();

    }

    /**
     * Getter for sessions.
     *
     * @return The sessions.
     */
    protected Map<String, Queue<Session>> getSessions() {
        return sessions;
    }
}
