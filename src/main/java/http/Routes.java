package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import database.SessionQueries;
import models.Device;
import models.Player;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.SessionQueries.createSession;
import static database.SessionQueries.playerJoinSession;
import static spark.Spark.*;

/**
 * Declares the API routes.
 */
public final class Routes {

    /**
     * Private constructor.
     */
    private Routes() {
    }

    /**
     * Setup all route hooks.
     */
    public static void setupRoutes() {
        setupWebsocketRoutes();
        convertJson();
        setupTokenValidation();
        setupCreateSessionRoute();
        setupJoinSessionRoutes();
        setupGetScoreRoute();
        setupInGameRoutes();
        setupSessionManagementRoutes();
        // TODO: 23/12/16 Manage players: delete, change name, etc.
    }

    /**
     * Convert the body from a request to attributes that can be accessed easily.
     */
    private static void convertJson() {
        before(((request, response) -> {
            Type type = new TypeToken<HashMap<String, Object>>() {
            }.getType();
            HashMap<String, Object> map = new Gson().fromJson(request.body(), type);
            for (Map.Entry<String, Object> e : map.entrySet()) {
                request.attribute(e.getKey(), e.getValue().toString());
            }
        }));
    }

    /**
     * Setup the route for new tokens and intercept all other requests that don't come with a proper deviceID and token.
     */
    private static void setupTokenValidation() {
        post("/token", (request, response) -> {
            Device device = Device.newDevice(request.attribute("deviceID"));
            if (device != null) {
                return device.toJson();
            } else {
                halt(HttpStatus.UNAUTHORIZED_401, "Already have an authentication token.");
                return null;
            }
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri()
                    + ", body: " + request.body());
            if (!request.uri().equals("/token")) {
                Device device = new Device(request.attribute("deviceID"), request.attribute("token"));
                if (!device.authenticate()) {
                    halt(HttpStatus.UNAUTHORIZED_401, "Invalid Token or deviceID.");
                } else {
                    request.attribute("device", device);
                }
            }
        });

        after(((request, response) -> Logger.getGlobal().log(Level.INFO, "Response: " + response.raw())));
    }

    /**
     * Setup the route that allows the creation of a new session.
     */
    private static void setupCreateSessionRoute() {
        post("/session/create", (request, response) -> createSession(request.attribute("device")));
    }

    /**
     * Setup the route that allows clients to join a session.
     */
    private static void setupJoinSessionRoutes() {
        post("/session/join", (request, response) -> {
            Player player = playerJoinSession(request.attribute("joinToken"), request.attribute("device"));
            if (player != null) {
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
    private static void setupInGameRoutes() {
        before("/session/:sessionID/*", (request, response) -> {
            String playerID = request.attribute("playerID");
            Device device = request.attribute("device");
            if (playerID != null && device != null) {
                Player player = Player.getPlayer(playerID);
                if (player.getDevice().equals(device)
                        && player.getSession().getSessionID().equals(request.params("sessionID"))) {
                    request.attribute("player", player);
                } else {
                    halt(HttpStatus.UNAUTHORIZED_401, "Invalid deviceID,sessionID, or playerID.");
                }
                if (player.getSession().getStatus() != 1) {
                    halt(HttpStatus.LOCKED_423, "Session is paused or stopped.");
                }
            } else {
                halt(HttpStatus.BAD_REQUEST_400, "Invalid playerID or deviceID.");
            }
        });
    }

    /**
     * Setup the route that allows clients to get their scores.
     */
    private static void setupGetScoreRoute() {
        post("/session/:sessionID/score", (request, response) -> {
            Player player = request.attribute("player");

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("playerID", player.getPlayerID());
            responseObject.addProperty("score", player.getScore());
            return responseObject;
        });
    }

    /**
     * Setup the routes to manage the sessions (play, pause, delete).
     */
    private static void setupSessionManagementRoutes() {
        before("/session/:sessionID/manage/*", (request, response) -> {
            if (!((Player) request.attribute("player")).isAdmin()) {
                halt(HttpStatus.FORBIDDEN_403, "You are not an administrator.");
            }
        });

        put("/session/:sessionID/play", (request, response) -> updateSessionStatus(request, 1));

        put("/session/:sessionID/pause", (request, response) -> updateSessionStatus(request, 2));

        delete("/session/:sessionID/delete", (request, response) -> updateSessionStatus(request, 0));
    }

    /**
     * Helper class for play/pause/delete session requests.
     *
     * @param request Request made.
     * @param status  Status to change the session to.
     * @return SessionID and status.
     */
    private static JsonObject updateSessionStatus(Request request, int status) {
        Player player = request.attribute("player");
        SessionQueries.updateSessionStatus(player, status);

        return Session.getSession(player.getSession().getSessionID()).toJson();
    }

    /**
     * Setup the route that allows the use of websockets.
     */
    private static void setupWebsocketRoutes() {
        webSocket("/echo", EchoWebSocket.class);
    }


}

