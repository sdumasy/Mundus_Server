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
        questions();
        // TODO: 23/12/16 Subscribe?
    }

    /**
     * Creates routes associated with research questions.
     */
    public static void questions() {
        // TODO: 23/12/16 Assign/Get question.
        // TODO: 23/12/16 Answer question.
        // TODO: 23/12/16 Get answered questions.

        Aldo.post("/echo", (player, map) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", map.get("message") + ": 42");
            return jsonObject;
        });
    }
}
