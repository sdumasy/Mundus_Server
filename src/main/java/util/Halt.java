package util;

import com.google.gson.JsonObject;

import static spark.Spark.halt;

/**
 * Created by Thomas on 4-1-2017.
 */
public class Halt {

    public static void halter(int status, String errorMessage) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("halt", errorMessage);
        halt(status, jsonObject.toString());
    }
}
