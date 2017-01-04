package mundus;

import com.google.gson.JsonObject;
import framework.Aldo;


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
//        Aldo.setupGameLoop(() -> Logger.getGlobal().log(Level.INFO, "Game loop is running!"),
//                ((int) TimeUnit.SECONDS.toMillis((long) 1))); //No magic number :D
        Aldo.subscribe("/1", new String[]{"/echo1"}, (player, sessionID) -> true);
        Aldo.subscribe("/2", new String[]{"/echo2"}, (player, sessionID) -> true);
        questions();
        // TODO: 23/12/16 Subscribe?
    }

    /**
     * Creates routes associated with research questions.
     */
    public static void questions() {
        Aldo.get("/player/:playerID/question", (player, json) -> {
            // TODO: 23/12/16 Assign/Get question.

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });

        Aldo.post("/player/:playerID/question/:questionID/answer", (player, json) -> {
            // TODO: 23/12/16 Answer question.

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", json.toString() + player.getPlayerID());
            return jsonObject;
        });

        Aldo.get("/player/:playerID/publications", (player, json) -> {
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
