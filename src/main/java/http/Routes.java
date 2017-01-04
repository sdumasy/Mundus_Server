package http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.PlayerQueries;
import models.Device;
import models.Player;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.SessionQueries.*;
import static models.Player.getPlayer;
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
//        setupWebsocketRoutes();
        setupTokenValidation();

        setupCreateSessionRoute();
        setupJoinSessionRoute();
        setupGetSessionRoute();
        setupGetSessionPlayersRoute();
        setupSessionManagementRoutes();

        setupGetAllPlayersOfDevice();
        setupPlayerRoutes();
        setupGetPlayerRoute();
        setupSessionRoutes();
        setupChangeUsername();
    }

    /**
     * Setup the route that allows the use of websockets.
     */
    private static void setupWebsocketRoutes() {
    }

    /**
     * Setup the route for new tokens and intercept all other requests that don't come with a proper deviceID and token.
     */
    private static void setupTokenValidation() {
        post("/token", (request, response) -> {
            Device device = Device.newDevice(request.headers("Authorization").split(":")[0]);
            if (device != null) {
                response.body(device.toJson().toString());
                return null;
            } else {
                halt(HttpStatus.UNAUTHORIZED_401, "Already have an authentication token.");
                return null;
            }
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri());
            if (!request.uri().equals("/token") && !request.uri().startsWith("/subscribe/")) {
                // TODO: 04/01/17 remove socket surpassing validation.
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
        post("/session/username/:username", (request, response) ->
                createSession(request.attribute("device"), request.params("username")));
    }

    /**
     * Setup the route that allows clients to join a session.
     */
    private static void setupJoinSessionRoute() {
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
    private static void setupSessionRoutes() {
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
    private static void setupGetSessionRoute() {
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
    private static void setupGetSessionPlayersRoute() {
        post("/session/:sessionID/players", (request, response) -> getPlayers(request.params("sessionID")));
    }

    /**
     * Setup the routes to manage the sessions (play, pause, delete).
     */
    private static void setupSessionManagementRoutes() {
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
     * @param status  Status to change the session to.
     * @return SessionID and status.
     */
    private static JsonObject setSessionStatus(String sessionID, int status) {
        updateSessionStatus(sessionID, status);
        return Session.getSession(sessionID).toJson();
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
        before("/player/:playerID/*", (request, response) -> request.attribute("player",
                validatePlayer(request.attribute("device"), request.params("playerID"))));
    }

    /**
     * Validates that the given playerID is from the given device.
     *
     * @param device   The users device.
     * @param playerID The given playerID.
     * @return Whether the playerID corresponds with the device.
     */
    public static Player validatePlayer(Device device, String playerID) {
        Player player = getPlayer(playerID);
        if (player.getDevice().equals(device)) {
            return player;
        } else {
            halt(HttpStatus.BAD_REQUEST_400, "You are trying to access a player that is not yours.");
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

            assert player != null;
            return player.toJson();
        });
    }

    private static void setupChangeUsername() {
        put("/player/:playerID/username/:username", (request, response) -> {
            Player player = request.attribute("player");
            if (PlayerQueries.setUsername(player, request.params("username"))) {
                return getPlayer(player.getPlayerID()).toJson();
            } else {
                halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "Failed to set username.");
                return null;
            }
        });
    }
}

