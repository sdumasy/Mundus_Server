import http.Routes;

import static spark.Spark.port;

/**
 * Created by macbookpro on 29/11/2016.
 */
public final class App {
    /**
     * Private constructor.
     */
    private App() {
    }

    /**
     * Main method.
     *
     * @param args Runtime args.
     */
    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        Routes.setupRoutes();
    }

    public static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
