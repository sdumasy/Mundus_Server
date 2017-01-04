package http;

import com.google.gson.JsonArray;
import database.PlayerQueries;
import models.Device;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;

import static models.Player.getPlayer;
import static spark.Spark.*;

/**
 * Routes for player.
 */
public final class RoutesPlayer {

    /**
     * Private constructor.
     */
    private RoutesPlayer() {
    }

    /**
     * Returns all the players of the given device.
     */
    static void setupGetAllPlayersOfDevice() {
        get("/player/all", (request, response) -> {
            List<Player> list = PlayerQueries.getAllPlayers(request.attribute("device"));

            JsonArray jsonArray = new JsonArray();
            for (Player player : list) {
                jsonArray.add(player.toJson());
            }
            return jsonArray;
        });
    }

    /**
     * Validates the playerID and stores it in the request attributed for the specific path.
     */
    static void setupPlayerRoutes() {
        before("/player/:playerID/*", (request, response) -> request.attribute("player",
                validatePlayer(request.attribute("device"), request.params("playerID"))));
    }

    /**
     * Validates that the given playerID is from the given device.
     *
     * @param device   The users device.
     * @param playerID The given playerID.
     * @return Whether the playerID corresponds with the device.
     */
    public static Player validatePlayer(Device device, String playerID) {
        Player player = getPlayer(playerID);
        if (player.getDevice().equals(device)) {
            return player;
        } else {
            halt(HttpStatus.BAD_REQUEST_400, "You are trying to access a player that is not yours.");
        }
        //Unreachable code, halt() will stop request.
        return null;
    }

    /**
     * Setup the route that allows clients to get their scores.
     * The client needs to provide a player ID.
     * There is no role verification as everyone should be able to request any players score.
     */
    static void setupGetPlayerRoute() {
        get("/player/:playerID", (request, response) -> {
            Player player = validatePlayer(request.attribute("device"), request.params("playerID"));

            assert player != null;
            return player.toJson();
        });
    }

    /**
     * Changes the username of the player.
     */
    static void setupChangeUsername() {
        put("/player/:playerID/username/:username", (request, response) -> {
            Player player = request.attribute("player");
            if (PlayerQueries.setUsername(player, request.params("username"))) {
                return getPlayer(player.getPlayerID()).toJson();
            } else {
                halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "Failed to set username.");
                return null;
            }
        });
    }
}
