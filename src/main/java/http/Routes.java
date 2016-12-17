package http;

import com.google.gson.Gson;
import database.Database;
import models.GameSession;
import models.User;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

/**
 * Declares the API routes
 */
public class Routes {

    public static void setupRoutes() {
        setupWebsocketRoutes();
        setupTokenValidation();
        setupCreateSessionRoute();
        setupJoinRoutes();
        setupGetScoreRoute();
        setupUpdateScoreRoute();
    }

    private static void setupTokenValidation() {
        post("/requestToken", (request, response) -> {
            // TODO: 17/12/16 Get deviceID from request
            // TODO: 17/12/16 Generate unique token
            // TODO: 17/12/16 Store deviceID and token combination
            return request.body();
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri());
            if (request.uri().equals("/requestToken")) {

                // TODO: 17/12/16 Check parameters for SQL injection
                // TODO: 17/12/16 Validate Device ID & Authentication Token

                halt(401, "Invalid Token or deviceID.");
            }
        });
    }

    private static void setupCreateSessionRoute() {
        post("/device/:device-id/token/:auth-token/create-session", (req, res) -> {
            String deviceId = req.params("device-id");
            String authToken = req.params("auth-token");

            // TODO: 17/12/16 Check parameters for SQL injection
            // TODO: 17/12/16 Validate Device ID & Authentication Token
            // TODO: 17/12/16 Create new session
            // TODO: 17/12/16 Return correct values

            return "{\"Device ID\": \"" + deviceId + "\", \"Token\": \"" + authToken + "\"}";
        });
    }

    private static void setupGetScoreRoute() {
        get("/device/:device-id/token/:auth-token/player/:player-id/score", (req, res) -> {
            String deviceId = req.params("device-id");
            String authToken = req.params("auth-token");
            String playerId = req.params("player-id");

            // TODO: 17/12/16 Validate Player ID & Device ID
            // TODO: 17/12/16 Read Score from Database
            // TODO: 17/12/16 Return correct values

            return "{\"Session ID\": \"" + playerId + "\", \"Score\": 2}";
        });
    }

    private static void setupUpdateScoreRoute() {
        put("/device/:device-id/token/:auth-token/player/:player-id/score/:score", (req, res) -> {
            String deviceId = req.params("device-id");
            String authToken = req.params("auth-token");
            String playerId = req.params("player-id");
            String scoreString = req.params("score");
            int score = 0;

            if (scoreString.matches("-?\\d+(\\.\\d+)?")) {
                score = Integer.parseInt(req.params("score"));
            }

            // TODO: 17/12/16 Validate Player ID & Device ID
            // TODO: 17/12/16 Update Score in Database
            // TODO: 17/12/16 Return correct values

            return "{\"Result\": \"" + (score == 0) + "\", \"error\": \"\"}";
        });
    }

    private static void setupJoinRoutes() {
        get("/hello", (req, res) -> "Hello World");

        post("/gamesession", (req, res) -> {
            GameSession gameSession = new Gson().fromJson(req.body(), GameSession.class);
            String query = "insert into GameSession (adminUserId, adminJoinToken, userJoinToken) values ('" + gameSession.getAdminUserId() + "', '"
                    + gameSession.getAdminJoinToken() + "', '" + gameSession.getUserJoinToken() + "')";
            Database.excecuteUpdateQuery(query);
            return req.body();
        });

        post("/user", (req, res) -> {
            System.out.println("user makkke");
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

