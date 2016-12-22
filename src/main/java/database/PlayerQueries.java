package database;

import models.Device;
import models.Player;
import models.Role;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static database.Database.executeSearchQuery;
import static spark.Spark.halt;

/**
 * Queries needed for teh Player class.
 */
public class PlayerQueries {

    /**
     * Private constructor.
     */
    private PlayerQueries() {
        //empty on purpose
    }

    /**
     * Checks whether a player exists in the database.
     * @param player The player to be checked.
     * @return Returns if the player exists.
     */
    public static boolean playerExists(Player player) {
        String query = "SELECT * FROM `session_player` WHERE `device_id` = ? AND `session_id` = ? AND `role_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, player.getDevice().getDeviceID(),
                player.getSessionID(), player.getRoleID());
        if (result.size() == 0) {
            return false;
        } else if (result.size() == 1) {
            return true;
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Multiple identical players exist.");
            return true;
        }
    }

    /**
     * Checks whether a playerID exists in the database.
     * @param playerID The playerID to be checked.
     * @return Returns if the playerID exists.
     */
    public static boolean playerIDExists(String playerID) {
        String query = "SELECT * FROM `session_player` WHERE `player_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, playerID);
        if (result.size() == 0) {
            return false;
        } else if (result.size() == 1) {
            return true;
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "PlayerID is not unique.");
            return true;
        }
    }

    /**
     * Adds a new player to the database.
     * @param player The player to be added to the database.
     * @return whether the player was successfully added.
     */
    public static boolean addNewPlayer(Player player) {
        if (!playerExists(player) && !playerIDExists(player.getPlayerID())) {
            String query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?)";
            return Database.executeManipulationQuery(query, player.getPlayerID(), player.getDevice().getDeviceID(),
                    player.getSessionID(), player.getRoleID(), player.getScore());
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Player already created.");
            return false;
        }
    }

    /**
     * Retrieve all the player data in the database based of the playerID.
     *
     * @param playerID The playerID.
     * @return A map of the data.
     */
    public static Player getPlayer(String playerID) {
        String query = "SELECT * FROM `session_player` WHERE `player_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, playerID);
        if (result.size() == 1) {
            Map<String, Object> map = result.get(0);
            return new Player(map.get("playerID").toString(), map.get("session_id").toString(),
                    Device.getDevice(map.get("device_id").toString()),
                    Role.getById((int) map.get("role_id")), (int) map.get("score"));
        } else if (result.size() == 0) {
            halt(HttpStatus.UNAUTHORIZED_401, "No player found.");
        } else {
            halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "PlayerID not unique.");
        }
        return null;
    }
}
