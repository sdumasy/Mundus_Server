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
        setupJoinRoutes();

    }

    private static void setupTokenValidation() {
        post("/requestToken", (request, response) -> {
            // TODO: 17/12/16 Get deviceID from request
            // TODO: 17/12/16 Generate unique token
            // TODO: 17/12/16 Store deviceID and token combination
            return request.body();
        });

        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO,request.requestMethod() + ": " + request.uri());
            if (request.uri() != "/requestToken") {
                // TODO: 17/12/16 Validate token and deviceId with stored values
                if (false) {
                    halt(401,"Invalid Token or deviceID.");
                }
            }
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

