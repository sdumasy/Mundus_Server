package http;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import database.PlayerQueries;
import database.SessionQueries;
import models.Device;
import models.Player;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.SessionQueries.*;
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
        setupJoinSessionRoute();
        setupGetSessionRoute();
        setupGetSessionScoresRoute();
        setupSessionManagementRoutes();

        setupGetAllPlayersOfDevice();
        setupPlayerRoutes();
        setupGetPlayerRoute();
        setupSessionRoutes();

        // TODO: 23/12/16 Manage players: delete, change name, etc.
    }

    /**
     * Setup the route that allows the use of websockets.
     */
    private static void setupWebsocketRoutes() {
        webSocket("/echo", EchoWebSocket.class);
    }

    /**
     * Convert the body from a request to attributes that can be accessed easily.
     */
    private static void convertJson() {
        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.headers("Authorization"));
            Type type = new TypeToken<HashMap<String, Object>>() {
            }.getType();
            HashMap<String, Object> map = new Gson().fromJson(request.body(), type);
            if (map != null) {
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    request.attribute(e.getKey(), e.getValue().toString());
                }
            }
        });
    }

    /**
     * Setup the route for new tokens and intercept all other requests that don't come with a proper deviceID and token.
     */
    private static void setupTokenValidation() {
        post("/token", (request, response) -> {
            Device device = Device.newDevice(request.headers("Authorization").split(":")[0]);
            if (device != null) {
                return device.toJson();
            } else {
                halt(HttpStatus.UNAUTHORIZED_401, "Already have an authentication token.");
                return null;
            }
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri());
            if (!request.uri().equals("/token")) {
                String[] stringArray = request.headers("Authorization").split(":");
                Device device = new Device(stringArray[0], stringArray[1]);
                if (!device.authenticate()) {
                    halt(HttpStatus.UNAUTHORIZED_401, "Invalid Token or deviceID.");
                } else {
                    request.attribute("device", device);
                }
            }
        });

        after((request, response) -> Logger.getGlobal().log(Level.INFO, "Response: " + response.raw()));
    }

    /**
     * Setup the route that allows the creation of a new session.
     */
    private static void setupCreateSessionRoute() {
        post("/createSession/username/:username", (request, response) ->
                createSession(request.attribute("device"), request.params("username")));
    }

    /**
     * Setup the route that allows clients to join a session.
     */
    private static void setupJoinSessionRoute() {
        post("/joinSession/:joinToken/username/:username", (request, response) -> {
            Player player = playerJoinSession(request.params("joinToken"),
                    request.attribute("device"), request.params("username"));
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
    private static void setupSessionRoutes() {
        before("/session/:sessionID/*", (request, response) -> {
            request.attribute("session",
                    validateSession(request.attribute("device"), request.params("sessionID")));
        });
    }

    /**
     * Validates that the given device is a member of the given sessionID.
     *
     * @param device    The users device.
     * @param sessionID The given sessionID.
     * @return Whether the sessionID corresponds with the device.
     */
    private static Session validateSession(Device device, String sessionID) {
        if (sessionID != null && device != null) {
            Session session = Session.getSession(sessionID);
            if (isMember(sessionID, device)) {
                return session;
            } else {
                halt(HttpStatus.BAD_REQUEST_400,
                        "You are trying to access a session that you are not a member of.");
            }
        } else {
            halt(HttpStatus.BAD_REQUEST_400, "Invalid sessionID or deviceID.");
        }
        //Unreachable code, halt() will stop request.
        return null;
    }

    /**
     * Setup the route that allows clients to get their scores.
     * The client needs to provide a player ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    private static void setupGetSessionRoute() {
        get("/session/:session", (request, response) -> {
            Session session = validateSession(request.attribute("device"), request.params("sessionID"));
            if (session != null) {
                return session.toJson();
            } else {
                //Unreachable code, session can not be null.
                return null;
            }
        });
    }

    /**
     * Setup the route that allows clients to get all scores in their session.
     * The client needs to provide a session ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    private static void setupGetSessionScoresRoute() {
        post("/session/:sessionID/scores", (request, response) -> getScores(request.params("sessionID")));
    }

    /**
     * Setup the routes to manage the sessions (play, pause, delete).
     */
    private static void setupSessionManagementRoutes() {
        before("/session/:sessionID/manage/*", (request, response) -> {
            if (((Session) request.attribute("session")).getAdminID()
                    .equals(((Device) request.attribute("device")).getDeviceID())) {
                halt(HttpStatus.FORBIDDEN_403, "You are not an administrator.");
            }
        });

        post("/session/:sessionID/manage/play", (request, response) -> updateSessionStatus(request, 1));

        post("/session/:sessionID/manage/pause", (request, response) -> updateSessionStatus(request, 2));

        post("/session/:sessionID/manage/delete", (request, response) -> updateSessionStatus(request, 0));
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

    private static void setupGetAllPlayersOfDevice() {
        get("/player/all", (request, response) -> {
            List<Player> list = PlayerQueries.getAllPlayers(request.attribute("device"));

            JsonArray jsonArray = new JsonArray();
            for (Player player : list) {
                jsonArray.add(player.toJson());
            }
            return jsonArray;
        });
    }

    /**
     * Validates the playerID and stores it in the request attributed for the specific path.
     */
    private static void setupPlayerRoutes() {
        before("/player/:playerID/*", (request, response) -> {
            request.attribute("player",
                    validatePlayer(request.attribute("device"), request.params("playerID")));
        });
    }

    /**
     * Validates that the given playerID is from the given device.
     *
     * @param device   The users device.
     * @param playerID The given playerID.
     * @return Whether the playerID corresponds with the device.
     */
    private static Player validatePlayer(Device device, String playerID) {
        if (playerID != null && device != null) {
            Player player = Player.getPlayer(playerID);
            if (player.getDevice().equals(device)) {
                return player;
            } else {
                halt(HttpStatus.BAD_REQUEST_400, "You are trying to access a player that is not yours.");
            }
        } else {
            halt(HttpStatus.BAD_REQUEST_400, "Invalid playerID or deviceID.");
        }
        //Unreachable code, halt() will stop request.
        return null;
    }

    /**
     * Setup the route that allows clients to get their scores.
     * The client needs to provide a player ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    private static void setupGetPlayerRoute() {
        get("/player/:playerID", (request, response) -> {
            Player player = validatePlayer(request.attribute("device"), request.params("playerID"));

            return player.toJson();
        });
    }
}

