package http;

import com.google.gson.JsonObject;
import models.Device;
import models.Player;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;

import static database.SessionQueries.*;
import static models.Player.getPlayer;
import static spark.Spark.*;

/**
 * Routes concerning session.
 */
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
        post("/session/username/:username", (request, response) ->
                createSession(request.attribute("device"), request.params("username")));
    }

    /**
     * Setup the route that allows clients to join a session.
     */
    static void setupJoinSessionRoute() {
        post("/session/join/:joinToken/username/:username", (request, response) -> {
            Player player = playerJoinSession(request.params("joinToken"),
                    request.attribute("device"), request.params("username"));
            if (player != null) {
                //TODO: Notify admin (websocket) that a player joined.
                return player.toJson();
            } else {
                halt(HttpStatus.UNAUTHORIZED_401, "Could not add you to the session.");
                return null;
            }
        });
    }

    /**
     * Creates a player model for all in game requests.
     */
    static void setupSessionRoutes() {
        before("/session/:sessionID/*", (request, response) -> {
            if (!(request.params("sessionID").equals("join")
                    || request.params("sessionID").equals("username"))) {
                request.attribute("session",
                        validateSession(request.attribute("device"), request.params("sessionID")));
            }
        });
    }

    /**
     * Validates that the given device is a member of the given sessionID.
     *
     * @param device    The users device.
     * @param sessionID The given sessionID.
     * @return Whether the sessionID corresponds with the device.
     */
    protected static Session validateSession(Device device, String sessionID) {
        Session session = Session.getSession(sessionID);
        if (isMember(sessionID, device)) {
            return session;
        } else {
            halt(HttpStatus.BAD_REQUEST_400,
                    "You are trying to access a session that you are not a member of.");
        }
        //Unreachable code, halt() will stop request.
        return null;
    }

    /**
     * Setup the route that allows clients to get their scores.
     * The client needs to provide a player ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    static void setupGetSessionRoute() {
        get("/session/:sessionID", (request, response) -> {
            Session session = validateSession(request.attribute("device"), request.params("sessionID"));
            return session.toJson();
        });
    }

    /**
     * Setup the route that allows clients to get all players and their scores in their session.
     * The client needs to provide a session ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    static void setupGetSessionPlayersRoute() {
        post("/session/:sessionID/players", (request, response) -> getPlayers(request.params("sessionID")));
    }

    /**
     * Setup the routes to manage the sessions (play, pause, delete).
     */
    static void setupSessionManagementRoutes() {
        before("/session/:sessionID/manage/*", (request, response) -> {
            String[] stringArray = request.headers("Authorization").split(":");
            Device device = new Device(stringArray[0], stringArray[1]);
            if (!getPlayer(getSession(request.params("sessionID")).getAdminID()).getDevice()
                    .equals(device)) {
                halt(HttpStatus.FORBIDDEN_403, "You are not an administrator.");
            }
        });

        put("/session/:sessionID/manage/play", (request, response) ->
                setSessionStatus(request.params("sessionID"), 1));

        put("/session/:sessionID/manage/pause", (request, response) ->
                setSessionStatus(request.params("sessionID"), 2));

        delete("/session/:sessionID/manage/delete", (request, response) ->
                setSessionStatus(request.params("sessionID"), 0));
    }

    /**
     * Helper class for play/pause/delete session requests.
     *
     * @param sessionID The ID of the session that needs to be set.
     * @param status    Status to change the session to.
     * @return SessionID and status.
     */
    private static JsonObject setSessionStatus(String sessionID, int status) {
        updateSessionStatus(sessionID, status);
        return Session.getSession(sessionID).toJson();
    }
}
