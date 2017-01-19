package application;

import database.Database;
import framework.Aldo;
import http.Routes;
import mundus.ExpeditionMundus;
import spark.Spark;

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
//        String url = "jdbc:mysql://gi6kn64hu98hy0b6.chr7pe7iynqr.eu-west-1.rds.amazonaws.com:3306/"
//                + "z7vnfv6y27vhnelm?useSSL=false";
//        String user = "yum29ckgulepk404";
//        String password = "xp5oc6vwuz4tijx4";
//        Database.setConnection(url, user, password);

        port(getHerokuAssignedPort());
        ExpeditionMundus.create();
        Routes.setupRoutes();
        Aldo.start(); //Executes the http routes defined by expedition Mundus
        // All web sockets should have been defined before.
    }

    /**
     * Kills the Aldo game loop thread and database thread.
     */
    public static void stop() {
        Database.closeDatabase();
        Aldo.stopGameLoop();
        Spark.stop();
    }

    /**
     * Get the assigned Heroku port, or the default if there is none.
     *
     * @return The port number.
     */
    @SuppressWarnings("checkstyle:magicnumber") //4567 is a port number
    private static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
