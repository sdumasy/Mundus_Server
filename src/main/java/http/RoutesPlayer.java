package http;

import com.google.gson.JsonArray;
import database.PlayerQueries;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import static http.Routes.validateDevice;
import static http.Routes.validatePlayer;
import static models.Player.getPlayer;
import static spark.Spark.get;
import static spark.Spark.put;
import static util.Halt.halter;

/**
 * Could not be prevented easily.
 */
@SuppressWarnings("PMD.TooManyStaticImports")


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
        get("/player/all", validateDevice((request, device) -> {
            JsonArray jsonArray = new JsonArray();
            for (Player p : PlayerQueries.getAllPlayers(device)) {
                jsonArray.add(p.toJson());
            }
            return jsonArray;
        }));
    }

    /**
     * Setup the route that allows clients to get their player information.
     */
    static void setupGetPlayerRoute() {
        get("/player", validatePlayer((request, player) -> player.toJson()));
    }

    /**
     * Changes the username of the player.
     */
    static void setupChangeUsername() {
        put("/player/username/:username", validatePlayer((request, player) -> {
            if (PlayerQueries.setUsername(player, request.params("username"))) {
                return getPlayer(player.getPlayerID()).toJson();
            } else {
                halter(HttpStatus.INTERNAL_SERVER_ERROR_500, "Failed to set username.");
                return null;
            }
        }));
    }
}
