package database;

import models.Device;
import models.Player;
import models.Role;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static database.Database.executeSearchQuery;
import static spark.Spark.halt;

/**
 * Queries needed for teh Player class.
 */
public final class PlayerQueries {

    /**
     * Private constructor.
     */
    private PlayerQueries() {
        //empty on purpose
    }

    /**
     * Checks whether a player exists in the database.
     *
     * @param player The player to be checked.
     * @return Returns if the player exists.
     */
    protected static boolean playerExists(Player player) {
        String query = "SELECT * FROM `session_player` WHERE `device_id` = ? AND `session_id` = ? AND `role_id` = ?";
        return singleResult(executeSearchQuery(query, player.getDevice().getDeviceID(),
                player.getSession().getSessionID(), player.getRoleID()));
    }

    /**
     * Checks whether a playerID exists in the database.
     *
     * @param playerID The playerID to be checked.
     * @return Returns if the playerID exists.
     */
    protected static boolean playerIDExists(String playerID) {
        String query = "SELECT * FROM `session_player` WHERE `player_id` = ?";
        return singleResult(executeSearchQuery(query, playerID));
    }

    /**
     * Checks whether a username exists within a session in the database.
     *
     * @param username  The username.
     * @param sessionID The session.
     * @return Whether it exists.
     */
    protected static boolean usernameExists(String username, String sessionID) {
        String query = "SELECT * FROM `session_player` WHERE `session_id` = ? AND `username` = ?";
        return singleResult(executeSearchQuery(query, sessionID, username));
    }

    /**
     * Helper class to whether a single result is given.
     *
     * @param result The database result.
     * @return Whether there is a single result.
     */
    protected static boolean singleResult(List<Map<String, Object>> result) {
        if (result.size() == 0) {
            return false;
        } else if (result.size() == 1) {
            return true;
        } else {
            halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "Multiple identical database entries exist.");
            return true;
        }
    }

    /**
     * Adds a new player to the database.
     *
     * @param player The player to be added to the database.
     * @return whether the player was successfully added.
     */
    public static boolean addNewPlayer(Player player) {
        if (!playerExists(player) && !playerIDExists(player.getPlayerID())) {
            if (!usernameExists(player.getUsername(), player.getSession().getSessionID())) {
                String query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?, ?)";
                return Database.executeManipulationQuery(query, player.getPlayerID(), player.getDevice().getDeviceID(),
                        player.getSession().getSessionID(), player.getRoleID(),
                        player.getScore(), player.getUsername());
            } else {
                halt(HttpStatus.UNAUTHORIZED_401, "Username already used.");
                return false;
            }
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Player already created.");
            return false;
        }
    }

    /**
     * Retrieve a player from the database based of the playerID.
     *
     * @param playerID The playerID.
     * @return The player.
     */
    public static Player getPlayer(String playerID) {
        String query = "SELECT * FROM `session_player` WHERE `player_id` = ?";
        return createPlayer(executeSearchQuery(query, playerID));
    }


    /**
     * Retrieve a player from the database based of the sessionID and username.
     *
     * @param sessionID The sessionID.
     * @param username  The username.
     * @return The player.
     */
    public static Player getPlayer(String sessionID, String username) {
        String query = "SELECT * FROM `session_player` WHERE `session_id` = ? AND `username` = ?";
        return createPlayer(executeSearchQuery(query, sessionID, username));
    }

    /**
     * Creates a new player from a database response.
     *
     * @param list The database response.
     * @return The player.
     */
    protected static Player createPlayer(List<Map<String, Object>> list) {
        if (list.size() == 1) {
            Map<String, Object> map = list.get(0);
            return new Player(map.get("player_id").toString(), Session.getSession(map.get("session_id").toString()),
                    Device.getDevice(map.get("device_id").toString()),
                    Role.getById((int) map.get("role_id")), (int) map.get("score"), map.get("username").toString());
        } else if (list.size() == 0) {
            halt(HttpStatus.UNAUTHORIZED_401, "No player found.");
        } else {
            halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "Player not unique.");
        }
        return null;
    }
}
