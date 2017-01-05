package mundus;

import com.google.gson.JsonObject;
import framework.Aldo;
import http.SubscriptionWebSocket;

import static mundus.MundusQueries.getQuestion;


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
//        Aldo.setupGameLoop(() -> {
//            for (Session session : Aldo.getSessions()) {
//                webSocket.send(session.getSessionID(), "Game loop notifies you.");
//            }
//        }, (int) TimeUnit.MINUTES.toMillis((long) 1));
        questions();
    }

    /**
     * Creates routes associated with research questions.
     */
    protected static void questions() {
        Aldo.get("/question", (player, json) -> getQuestion(player));
        Aldo.post("/question/:questionID/answer", (player, json) -> {
            // TODO: 23/12/16 Answer question.

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });

        Aldo.get("/publications", (player, json) -> {
            // TODO: 23/12/16 Get an overview of all approved answers.

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
