package framework;

import com.google.gson.JsonObject;

/**
 * Created by Ben on 22/12/16.
 */
public class TestImplementation {
    public static void test() {
        Aldo.post("/echo",(player, map) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("response",map.get("message")+ ": bla");
            return jsonObject;
        });
    }
}
