package http;

import com.google.gson.JsonObject;
import database.SessionQueries;
import models.Player;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;

import static http.Routes.validateDevice;
import static http.Routes.validatePlayer;
import static spark.Spark.*;
import static util.Halt.halter;

/**
 * Could not be prevented easily.
 */
@SuppressWarnings("PMD.TooManyStaticImports")

public final class RoutesSession {
    /**
     * Private constructor.
     */
    private RoutesSession() {
    }

    /**
     * Setup the route that allows the creation of a new session.
     */
    static void setupCreateSessionRoute() {
        post("/session/username/:username", validateDevice((request, device) ->
                SessionQueries.createSession(device, request.params("username"))));
    }

    /**
     * Setup the route that allows clients to join a session.
     */
    static void setupJoinSessionRoute() {
        post("/session/join/:joinToken/username/:username", validateDevice((request, device) -> {
            Player player = SessionQueries.playerJoinSession(request.params("joinToken"),
                    device, request.params("username"));
            if (player == null) {
                halter(HttpStatus.UNAUTHORIZED_401, "Could not add you to the session.");
                return null;
            }
            //TODO: Notify admin (webSocket) that a player joined.
            return player.toJson();
        }));
    }

    /**
     * Setup the route that allows clients to get their scores.
     * The client needs to provide a player ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    static void setupGetSessionRoute() {
        get("/session", validatePlayer((request, player) -> player.getSession().toJson()));
    }

    /**
     * Setup the route that allows clients to get all players and their scores in their session.
     * The client needs to provide a session ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    static void setupGetSessionPlayersRoute() {
        get("/session/players", validatePlayer((request, player) ->
                SessionQueries.getPlayers(player.getSession().getSessionID())));
    }

    /**
     * Setup the routes to manage the sessions (play, pause, delete).
     */
    static void setupSessionManagementRoutes() {

        put("/session/play", validatePlayer((request, player) -> setSessionStatus(player, 1)));

        put("/session/pause", validatePlayer((request, player) -> setSessionStatus(player, 2)));

        delete("/session/delete", validatePlayer((request, player) -> setSessionStatus(player, 0)));
    }

    /**
     * Helper class for play/pause/delete session requests.
     *
     * @param player    The player making the request.
     * @param status    Status to change the session to.
     * @return SessionID and status.
     */
    private static JsonObject setSessionStatus(Player player, int status) {
        if (!player.isAdmin()) {
            halter(HttpStatus.FORBIDDEN_403, "You are not an administrator.");
        }
        String sessionID = player.getSession().getSessionID();
        SessionQueries.updateSessionStatus(sessionID, status);
        return Session.getSession(sessionID).toJson();
    }
}
