package application;

import framework.Aldo;
import http.Routes;
import mundus.ExpeditionMundus;

import static spark.Spark.port;

/**
 * The App itself.
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
        ExpeditionMundus.create();
        Routes.setupRoutes();
        Aldo.start(); //Executes the http routes defined by expedition Mundus
        // All web sockets should have been defined before.
    }

    /**
     * Get the assigned Heroku port, or the default if there is none.
     *
     * @return The port number.
     */
    @SuppressWarnings("checkstyle:magicnumber") //4567 is a port number
    protected static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
