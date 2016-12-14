package http;

import com.google.gson.Gson;
import database.Database;
import models.GameSession;
import models.User;

import java.util.List;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by macbookpro on 05/12/2016.
 */
public class Routes {

    public static void setupRoutes() {
        setupWebsocketRoutes();
        setupJoinRoutes();

    }

    public static void setupJoinRoutes() {
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

    public static void setupWebsocketRoutes() {
        webSocket("/echo", EchoWebSocket.class);
    }
}

