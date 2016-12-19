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

    public static void setupRoutes() {
        setupWebsocketRoutes();
        covertJson();
        setupTokenValidation();
        setupCreateSessionRoute();
        setupJoinRoutes();
        setupGetScoreRoute();
    }

    private static void covertJson() {
        before(((request, response) -> {
            HashMap<String,String> map = new Gson().fromJson(request.body(),HashMap.class);
            for (String k:map.keySet()) {
                request.attribute(k,map.get(k));
            }
        }));
    }

    private static void setupTokenValidation() {
        post("/requestToken", (request, response) -> {
            String token = Validation.createToken(request.attribute("deviceID").toString());

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("token", token);
            return responseObject;
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri() + ", body: " + request.body());
            if (!request.uri().equals("/requestToken")) {

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

    private static void setupCreateSessionRoute() {
        post("/createSession", (req, res) -> {
            String deviceID = req.attribute("deviceID");

            User user = new Gson().fromJson(req.body(), User.class);
            return createSession(user.getID());

        });
    }

    private static void setupGetScoreRoute() {
        get("/getScore", (req, res) -> {
            String deviceID = req.attribute("deviceID");
            String playerID = req.attribute("playerID");

            // TODO: 17/12/16 Read Score from Database
            // TODO: 17/12/16 Return correct values

            JsonObject responseObject = new JsonObject();
            responseObject.addProperty("playerID", playerID);
            responseObject.addProperty("score", "");
            return responseObject;
        });
    }

    private static void setupJoinRoutes() {
        post("/user", (req, res) -> {
            User user = new Gson().fromJson(req.body(), User.class);
            String query = "insert into User (name) values ('" + user.getName() + "')";
            List<Map<String, Object>> queryRes = Database.excecuteUpdateQuery(query);
            user.setId(queryRes.get(0).get("GENERATED_KEY").toString());
            return new Gson().toJson(user);
        });
    }

    private static void setupWebsocketRoutes() {
        webSocket("/echo", EchoWebSocket.class);
    }
}

