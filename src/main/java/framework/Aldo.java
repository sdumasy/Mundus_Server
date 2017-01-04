package framework;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import database.PlayerQueries;
import http.Routes;
import http.SubscriptionWebSocket;
import org.eclipse.jetty.http.HttpStatus;
import spark.Route;
import spark.Spark;

import java.util.LinkedList;
import java.util.List;

/**
 * Connection class to framework.
 */
public final class Aldo {
    private static boolean runningGameLoop;
    private static Aldo instance = new Aldo();
    private List<Runnable> routes = new LinkedList<>();

    private Aldo() {
        //Empty by design.
    }

    /**
     * Singleton getter for the instance of Aldo.
     *
     * @return The active instance of Aldo.
     */
    public static Aldo getInstance() {
        return instance;
    }

    /**
     * Prepares the routes.
     */
    public static void setupRoutes() {
        addRoute(() -> Spark.before("/Aldo/player/:playerID/*", (request, response) ->
                Routes.validatePlayer(request.attribute("device"), request.params("playerID"))));
    }

    /**
     * Allows users to subscribe to paths, they wil then receive the same result as people querying those paths.
     *
     * @param path                 The path to subscribe to.
     * @param paths                The paths that the subscription will inform on.
     * @param subscriptionVerifier A verifier that defines who is allowed to subscribe.
     */
    public static void subscribe(String path, String[] paths, SubscriptionVerifier subscriptionVerifier) {
        SubscriptionWebSocket webSocket = new SubscriptionWebSocket("/subscribe/" + simplifyPath(path) + "/");
        Spark.webSocket("/subscribe/" + simplifyPath(path) + "/*", webSocket);
        addRoute(() -> Spark.before("/subscribe/" + simplifyPath(path) + "/:sessionID", (request, response) -> {
            // TODO: 03/01/17 Get player and sessionID.
            if (!subscriptionVerifier.handle(null, request.params("sessionID"))) {
                Spark.halt(HttpStatus.UNAUTHORIZED_401, "You are unauthorized");
            }
        }));

        for (String s : paths) {
            addRoute(() -> Spark.after("/Aldo/player/:playerID/" + simplifyPath(s), (request, response) ->
                    webSocket.send(PlayerQueries.getPlayer(request.params("playerID")).getSession().getSessionID(),
                            response.body())));
        }
    }

    /**
     * Registers all the defined routes.
     */
    public static void start() {
        for (Runnable runnable : getInstance().routes) {
            runnable.run();
        }
    }

    /**
     * Adds a route to be executed when start is called.
     *
     * @param runnable The route.
     */
    protected static void addRoute(Runnable runnable) {
        getInstance().routes.add(runnable);
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
     * @param requestHandler The defined route
     * @return The spark route.
     */
    protected static Route toRoute(RequestHandler requestHandler) {
        return (request, response) -> {
            JsonObject jsonObject = new JsonObject();
            if (!request.body().equals("")) {
                jsonObject = new JsonParser().parse(request.body()).getAsJsonObject();
            }
            response.body(requestHandler.handle(PlayerQueries.getPlayer(request.params("playerID")),
                    jsonObject).toString());
            return null;
        };
    }

    /**
     * Removes slash if present.
     *
     * @param path The path to simplify.
     * @return The simplified path.
     */
    protected static String simplifyPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

    /**
     * A post request.
     *
     * @param path           The path of the post request.
     * @param implementation The implementation by the user.
     */
    public static void post(String path, RequestHandler implementation) {
        addRoute(() -> Spark.post("/Aldo/player/:playerID/" + simplifyPath(path), toRoute(implementation)));

    }
}
