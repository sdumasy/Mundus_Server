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

    /**
     * Converts a route defined by the user to a route that spark understands.
     *
     * @param implementation The defined route
     * @return The spark route.
     */
    protected static Route toRoute(Implementation implementation) {
        return (request, response) -> {
            Player player = request.attribute("player");
            Type type = (new TypeToken<HashMap<String, Object>>() { }).getType();
            HashMap<String, Object> map = new Gson().fromJson(request.attribute("container").toString(), type);
            return implementation.handle(player, map);
        };
    }

    /**
     * A post request.
     *
     * @param path           The path of the post request.
     * @param implementation The implementation by the user.
     */
    public static void post(String path, Implementation implementation) {
        Spark.post("/session/:sessionID" + path, toRoute(implementation));
    }
}
