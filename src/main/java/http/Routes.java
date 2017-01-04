package http;

import database.PlayerQueries;
import models.Device;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.util.logging.Level;
import java.util.logging.Logger;

import static spark.Spark.*;

/**
 * Declares the API routes.
 */
public final class Routes {

    /**
     * Private constructor.
     */
    private Routes() {
    }

    /**
     * Setup all route hooks.
     */
    public static void setupRoutes() {
        setupTokenRoute();
        setupTokenValidation();

        RoutesSession.setupCreateSessionRoute();
        RoutesSession.setupJoinSessionRoute();
        RoutesSession.setupSessionRoutes();
        RoutesSession.setupGetSessionRoute();
        RoutesSession.setupGetSessionPlayersRoute();
        RoutesSession.setupSessionManagementRoutes();

        RoutesPlayer.setupGetAllPlayersOfDevice();
        RoutesPlayer.setupPlayerRoutes();
        RoutesPlayer.setupGetPlayerRoute();
        RoutesPlayer.setupChangeUsername();
    }

    /**
     * Creates route to get a token.
     */
    private static void setupTokenRoute() {
        post("/token", (request, response) -> {
            Device device = Device.newDevice(request.headers("Authorization").split(":")[0]);
            if (device != null) {
                response.body(device.toJson().toString());
                return response.body();
            } else {
                halt(HttpStatus.UNAUTHORIZED_401, "Already have an authentication token.");
                return null;
            }
        });
    }

    /**
     * Setup the route for new tokens and intercept all other requests that don't come with a proper deviceID and token.
     */
    private static void setupTokenValidation() {
        before((request, response) -> {
            Logger.getGlobal().log(Level.INFO, request.requestMethod() + ": " + request.uri());
            if (!request.uri().equals("/token")) {
                String[] authorizationValues = request.headers("Authorization").split(":");
                // TODO: 04/01/17 Halt if there is no authorization header
                Device device = new Device(authorizationValues[0], authorizationValues[1]);
                if (!device.authenticate()) {
                    halt(HttpStatus.UNAUTHORIZED_401, "Invalid Token or deviceID.");
                } else {
                    request.attribute("device", device);
                }
                if (authorizationValues.length == 3) {
                    Player player = PlayerQueries.getPlayer(authorizationValues[2]);
                    if (player.getDevice().equals(device)) {
                        request.attribute("player", player);
                    } else {
                        halt(HttpStatus.UNAUTHORIZED_401, "PlayerID does not match deviceID.");
                    }
                }
            }
        });
    }

}

