package mundus;

import com.google.gson.JsonObject;
import framework.Aldo;
import http.SubscriptionWebSocket;
import models.Session;

import java.util.concurrent.TimeUnit;

import static mundus.MundusQueries.*;


/**
 * Temporary class for Expedition Mundus implementation, eventually this should gets its own project.
 */
public final class ExpeditionMundus {

    /**
     * Implementation of Expedition Mundus.
     */
    private ExpeditionMundus() {
        //Empty by design.
    }

    /**
     * Creates http routes.
     */
    public static void create() {
        SubscriptionWebSocket webSocket = Aldo.subscribe("/demo",
                new String[]{"/echo1", "/echo2"}, (player, sessionID) -> true);
        Aldo.subscribe("/2", new String[]{"/echo2"}, (player, sessionID) -> true);
        Aldo.setupGameLoop(() -> {
            for (Session session : Aldo.getSessions()) {
                webSocket.send(session.getSessionID(), "Game loop notifies you.");
            }
        }, (int) TimeUnit.MILLISECONDS.toMillis((long) 10));
        questions();
    }

    /**
     * Creates routes associated with research questions.
     */
    protected static void questions() {
        Aldo.get("/question", (player, json) -> getQuestion(player));

        Aldo.post("/question/:questionID/answer", (player, json) -> {
            submitAnswer(player, json.get(":questionid").getAsString(), json);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });

        Aldo.get("/publications", (player, json) -> getPublications(player));

        Aldo.get("/submitted", (player, json) -> getSubmitted(player));

        Aldo.put("/question/:questionID/review", (player, json) -> {
            submitReview(player, json.get(":questionid").getAsString(), json);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });

        Aldo.post("/echo1", (player, json) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });

        Aldo.post("/echo2", (player, json) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });
    }
}
