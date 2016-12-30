package framework;

import com.google.gson.JsonObject;
import models.Player;

import java.util.Map;

/**
 * Interface for the methods the user should implement.
 */
public interface Callback {
    /**
     * A method that is called when the corresponding path is called.
     *
     * @param player The Player who makes the call.
     * @param map    A map of the given data in the body.
     * @return A Json response.
     */
    JsonObject execute(Player player, Map<String, Object> map);
}
