package framework;

import com.google.gson.JsonObject;
import models.Player;

/**
 * Interface for the methods the user should implement.
 */
public interface RequestHandler {
    /**
     * A method that is called when the corresponding path is called.
     *
     * @param player The Player who makes the call.
     * @param json   The json object from the body of the request.
     * @return A Json response.
     */
    JsonObject handle(Player player, JsonObject json);
}
