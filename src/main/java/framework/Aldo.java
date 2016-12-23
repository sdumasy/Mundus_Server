package framework;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Player;
import spark.Route;
import spark.Spark;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Connection class to framework.
 */
public final class Aldo {

    private Aldo() {
        // Empty on purpose.
    }

    // TODO: 23/12/16 Calls for only active sessions and calls for all kinds of sessions.

    /**
     * Converts a route defined by the user to a route that spark understands.
     *
     * @param callback The defined route
     * @return The spark route.
     */
    protected static Route toRoute(Callback callback) {
        return (request, response) -> {
            Player player = request.attribute("player");
            Type type = new TypeToken<HashMap<String, Object>>() { }.getType();
            HashMap<String, Object> map = new Gson().fromJson(request.attribute("container").toString(), type);
            return callback.execute(player, map);
        };
    }

    /**
     * A post request.
     *
     * @param path           The path of the post request.
     * @param implementation The implementation by the user.
     */
    public static void post(String path, Callback implementation) {
        Spark.post("/Aldo" + path, toRoute(implementation));
    }
}
