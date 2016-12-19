package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import models.Player;
import validation.Validation;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.SessionQueries.*;
import static spark.Spark.*;
import static validation.Validation.authenticateDevice;
import static validation.Validation.hasToken;

/**
 * Declares the API routes.
 */
public final class Routes {

    /**
     * Private constructor.
     */
    private Routes() { }

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
    }

    /**
     * Convert the body from a request to attributes that can be accessed easily.
     */
    private static void convertJson() {
        before(((request, response) -> {
            HashMap<String, String> map = new Gson().fromJson(request.body(), HashMap.class);
            for (String k:map.keySet()) {
                request.attribute(k, map.get(k));
            }
        }));
    }

    /**
     * Setup the route for new tokens and intercept all other requests that don't come with a proper deviceID and token.
     */
    @SuppressWarnings("checkstyle:magicnumber") // 401 is an error code.
    private static void setupTokenValidation() {
        post("/token", (request, response) -> {
            String deviceID =request.attribute("deviceID");
            if (!hasToken(deviceID)) {
                String token = Validation.createToken(deviceID.toString());

                JsonObject responseObject = new JsonObject();
                responseObject.addProperty("token", token);
                return responseObject;
            } else {
                halt(401, "Already have an authentication token.");
            }
            return null;
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri()
                    + ", body: " + request.body());
            if (!request.uri().equals("/token")) {

                // TODO: 17/12/16 Check parameters for SQL injection

                if(!authenticateDevice(request.attribute("deviceID"), request.attribute("token"))) {
                    halt(401, "Invalid Token or deviceID.");
                }
            }
        });

        after(((request, response) -> {
            Logger.getGlobal().log(Level.INFO, "Response: " + response.raw());
        }));
    }

    /**
     * Setup the route that allows the creation of a new session.
     */
    private static void setupCreateSessionRoute() {
        post("/session/create", (request, response) -> {
            return createSession(request.attribute("deviceID"));
        });
    }

    /**
     * Setup the route that allows clients to join a session.
     */
    private static void setupJoinSessionRoutes() {
        post("/session/join", (request, response) -> {
            Player player = getNewPlayerOfSession(request.attribute("joinToken"));
            addNewPlayer(player,request.attribute("deviceID"));

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("sessionID", player.getSessionID());
            responseObject.addProperty("playerID",player.getPlayerID());
            responseObject.addProperty("role", player.getRole());
            return responseObject;
        });
    }

    /**
     * Setup the route that allows clients to get their scores.
     */
    private static void setupGetScoreRoute() {
        get("/session/:sessionID/score", (request, response) -> {
            String sessionID = request.params("sessionID");
            String playerID = request.attribute("playerID");

            // TODO: 17/12/16 Read Score from Database
            // TODO: 17/12/16 Return correct values

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("playerID", playerID);
            responseObject.addProperty("score", "");
            return responseObject;
        });
    }

    /**
     * Setup the route that allows the use of websockets.
     */
    private static void setupWebsocketRoutes() {
        webSocket("/echo", EchoWebSocket.class);
    }
}

