package framework;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Device;
import spark.Route;
import spark.Spark;

import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Connection class to framework.
 */
public final class Aldo {
    private static boolean runningGameLoop;

    private Aldo() {
        // Empty on purpose.
    }

    /**
     * Create game loop.
     *
     * @param gameLoop the game loop to be run.
     * @param interval The interval to wait between rerunning the game loop.
     */
    public static void setupGameLoop(Runnable gameLoop, int interval) {
        if (interval > 0) {
            (new Thread(() -> {
                runningGameLoop = true;
                while (runningGameLoop) {
                    gameLoop.run();
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            })).start();
        } else {
            throw new IllegalArgumentException("The interval must be larger than 0.");
        }
    }

    /**
     * Stops the game loop.
     */
    public static void stopGameLoop() {
        runningGameLoop = false;
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
            Device device = request.attribute("device");
            Type type = new TypeToken<HashMap<String, Object>>() { }.getType();
            HashMap<String, Object> map = new Gson().fromJson(request.attribute("container").toString(), type);
            return callback.execute(device, map);
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
