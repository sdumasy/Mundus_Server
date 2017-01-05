package http;

import com.google.gson.JsonElement;
import models.Device;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;
import spark.Request;
import spark.Route;

import static spark.Spark.post;
import static util.Halt.halter;

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

        RoutesSession.setupCreateSessionRoute();
        RoutesSession.setupJoinSessionRoute();
        RoutesSession.setupGetSessionRoute();
        RoutesSession.setupGetSessionPlayersRoute();
        RoutesSession.setupSessionManagementRoutes();

        RoutesPlayer.setupGetAllPlayersOfDevice();
        RoutesPlayer.setupGetPlayerRoute();
        RoutesPlayer.setupChangeUsername();
    }

    /**
     * Creates route to get a token.
     */
    private static void setupTokenRoute() {
        post("/token", (request, response) -> {
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || authorizationHeader.equals("")) {
                halter(HttpStatus.BAD_REQUEST_400, "No authorization header.");
            }

            Device device = Device.newDevice(authorizationHeader.split(":")[0]);
            if (device == null) {
                halter(HttpStatus.UNAUTHORIZED_401, "You already have a authorization token.");
            }

            response.body(device.toJson().toString());
            return response.body();
        });
    }

    /**
     * Returns a route that validates the device and handles the given ValidDeviceRoute.
     *
     * @param validDeviceRoute The ValidDeviceRoute to be executed with the validated device within the route.
     * @return Returns a Route.
     */
    public static Route validateDevice(ValidDeviceRoute validDeviceRoute) {
        return (request, response) -> {
            String authorizationHeader = request.headers("Authorization");
            if (authorizationHeader == null || authorizationHeader.equals("")) {
                halter(HttpStatus.BAD_REQUEST_400, "No authorization header.");
            }

            String[] authorizationValues = authorizationHeader.split(":");
            if (authorizationValues.length < 2) {
                halter(HttpStatus.BAD_REQUEST_400, "Authorization header must be of the form: <deviceID>:<token>.");
            }

            Device device = new Device(authorizationValues[0], authorizationValues[1]);
            if (!device.authenticate()) {
                halter(HttpStatus.UNAUTHORIZED_401, "Invalid Token or deviceID.");
            }

            request.attribute("device", device);

            JsonElement result = validDeviceRoute.handle(request, device);
            if (result != null) {
                response.body(result.toString());
            }
            return response.body();
        };
    }

    /**
     * Returns a route that validates the player and handles the given ValidPlayerRoute.
     *
     * @param validPlayerRoute The ValidPlayerRoute to be executed with the validated player within the route.
     * @return Returns a Route.
     */
    public static Route validatePlayer(ValidPlayerRoute validPlayerRoute) {
        return validateDevice((request, device) -> {
            String[] authorizationValues = request.headers("Authorization").split(":");
            if (authorizationValues.length < 3) {
                halter(HttpStatus.BAD_REQUEST_400, "Authorization header must be of the form: "
                        + "<deviceID>:<token>:<playerID>.");
            }

            Player player = Player.getPlayer(authorizationValues[2]);
            if (!player.getDevice().equals(device)) {
                halter(HttpStatus.UNAUTHORIZED_401, "PlayerID does not match deviceID.");
            }
            request.attribute("player", player);
            return validPlayerRoute.handle(request, player);
        });
    }


    /**
     * A route that gets handled with a validated device.
     */
    public interface ValidDeviceRoute {
        /**
         * Gets handled by a http request with a valid Device.
         *
         * @param request The request.
         * @param device  A validated device.
         * @return Json response for the http request.
         */
        JsonElement handle(Request request, Device device);
    }

    /**
     * A route that gets handled with a validated player.
     */
    public interface ValidPlayerRoute {
        /**
         * Gets handled by a http request with a valid Player.
         *
         * @param request The request.
         * @param player  A validated player.
         * @return Json response for the http request.
         */
        JsonElement handle(Request request, Player player);
    }
}

