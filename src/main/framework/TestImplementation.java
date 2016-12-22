package framework;

import com.google.gson.JsonObject;

/**
 * Temporary test class.
 */
public final class TestImplementation {

    /**
     * Temporary class to test the implementation of the user.
     */
    private TestImplementation() {
        //Empty by design.
    }

    /**
     * Temporary echo path. Sends the message back with some additional text.
     */
    public static void test() {
        Aldo.post("/echo", (player, map) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response", map.get("message") + ": 42");
            return jsonObject;
        });
    }
}
