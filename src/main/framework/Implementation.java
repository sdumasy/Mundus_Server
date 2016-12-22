package framework;

import com.google.gson.JsonObject;
import models.Player;

import java.util.Map;

/**
 * Created by Ben on 22/12/16.
 */
public interface Implementation {
    JsonObject handle(Player player, Map<String, Object> map);
}
