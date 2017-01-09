package util;

import com.google.gson.JsonObject;
import spark.HaltException;

import static spark.Spark.halt;

/**
 * Halts the http request.
 */
public final class Halt {

    /**
     * Private constructor.
     */
    private Halt() {
    }

    /**
     * Halts the http request.
     *
     * @param status       Status of the halt.
     * @param errorMessage Message for the error.
     */
    public static void halter(int status, String errorMessage) throws HaltException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("halt", errorMessage);
        halt(status, jsonObject.toString());
    }
}
