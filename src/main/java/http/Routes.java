package http;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import database.Database;
import models.User;
import validation.Validation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.SessionQueries.createSession;
import static spark.Spark.*;
import static validation.Validation.authenticateDevice;

/**
 * Declares the API routes
 */
public class Routes {

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
            HashMap<String,String> map = new Gson().fromJson(request.body(),HashMap.class);
            for (String k:map.keySet()) {
                request.attribute(k,map.get(k));
            }
        }));
    }

    /**
     * Setup the route for new tokens and intercept all other requests that don't come with a proper deviceID and token.
     */
    private static void setupTokenValidation() {
        post("/token", (request, response) -> {
            String token = Validation.createToken(request.attribute("deviceID").toString());

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("token", token);
            return responseObject;
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri() +
                    ", body: " + request.body());
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
        post("/session/create", (req, res) -> {
            String deviceID = req.attribute("deviceID");
            return createSession(deviceID);
        });
    }

    /**
     * Setup the route that allows clients to join a session
     */
    private static void setupJoinSessionRoutes() {
        post("/session/join", (req, res) -> {
            User user = new Gson().fromJson(req.body(), User.class);
            String query = "insert into User (name) values ('" + user.getName() + "')";
            List<Map<String, Object>> queryRes = Database.excecuteUpdateQuery(query);
            user.setId(queryRes.get(0).get("GENERATED_KEY").toString());
            return new Gson().toJson(user);
        });
    }

    /**
     * Setup the route that allows clients to get their scores.
     */
    private static void setupGetScoreRoute() {
        get("/session/:sessionID/score", (req, res) -> {
            String sessionID = req.params("sessionID");
            String playerID = req.attribute("playerID");

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

