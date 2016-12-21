package database;

import com.google.gson.JsonObject;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static database.Database.executeManipulationQuery;
import static database.Database.executeSearchQuery;
import static spark.Spark.halt;

/**
 * Contains the sql queries for the sessions.
 */
public final class SessionQueries {

    /**
     * Private constructor.
     */
    private SessionQueries() {
    }

    /**
     * Creates a new session in the database, generates two join codes for users and moderators, and finally adds the
     * user as the administrator to the session.
     *
     * @param deviceID The device that creates the session.
     * @return A JsonObject that contains the generated playerID, modToken and userToken
     */
    public static JsonObject createSession(String deviceID) {
        String sessionID = generateUniqueID("session", "session_id");
        String playerID = generateUniqueID("session_player", "player_id");

        String query = "INSERT INTO `session` VALUES (?, ?, ?, ?)";
        Database.executeManipulationQuery(query, sessionID, playerID, 1, LocalDateTime.now());

        String modToken = generateUniqueJoinToken();
        query = "INSERT INTO `session_token` VALUES (?, ?, ?)";
        Database.executeManipulationQuery(query, modToken, sessionID, 1);

        String userToken = generateUniqueJoinToken();
        Database.executeManipulationQuery(query, generateUniqueJoinToken(), sessionID, 2);

        query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?)";
        Database.executeManipulationQuery(query, playerID, deviceID, sessionID, 0, 0);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("sessionID", sessionID);
        jsonObject.addProperty("modToken", modToken);
        jsonObject.addProperty("userToken", userToken);
        jsonObject.addProperty("playerID", playerID);
        return jsonObject;
    }

    /**
     * Creates and adds a new player of the session corresponding with the join token.
     *
     * @param joinToken The join token to fetch the data of.
     * @param deviceID  The device of the user joining the session.
     * @return The new player.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static Player getNewPlayerOfSession(String joinToken, String deviceID) {
        String query = "SELECT `join_token`, `session_id`, `role_id` FROM `session_token` WHERE `join_token` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, joinToken);
        if (result.size() == 1) {
            Map<String, Object> map = result.get(0);
            Player player = Player.newPlayer(map.get("session_id").toString(), (int) map.get("role_id"), deviceID);
            addNewPlayer(player);
            return player;
        } else if (result.size() == 0) {
            halt(HttpStatus.UNAUTHORIZED_401, "Invalid joinToken");
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Identical joinTokens in database");
        }
        return null;
    }

    /**
     * Adds a new player to a session.
     *
     * @param player The player to be added to the database.
     */
    protected static void addNewPlayer(Player player) {
        String query = "SELECT * FROM `session_player` WHERE `device_id` = ? AND `session_id` = ? AND `role_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, player.getDeviceID(), player.getSessionID(), player.getRoleID());
        if (result.size() == 0) {
            query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?)";
            Database.executeManipulationQuery(query, player.getPlayerID(), player.getDeviceID(),
                    player.getSessionID(), player.getRoleID(), player.getScore());
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Player already created.");
        }
    }

    /**
     * Helper class to retrieve all the player data in the database.
     *
     * @param player The player.
     * @return A map of the data.
     */
    protected static Map<String, Object> getPlayerData(Player player) {
        String query = "SELECT * FROM `session_player` WHERE `player_id` = ? AND `device_id` = ? AND `session_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, player.getPlayerID(), player.getDeviceID(), player.getSessionID());
        if (result.size() == 1) {
            return result.get(0);
        } else if (result.size() == 0) {
            halt(HttpStatus.UNAUTHORIZED_401, "No player found.");
        } else {
            halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "PlayerID not unique.");
        }
        return null;
    }

    /**
     * Gets the roleID of the player.
     *
     * @param player The player to retrieve tht roleID for.
     * @return The roleID
     */
    public static Integer getRoleId(Player player) {
        return (int) getPlayerData(player).get("role_id");
    }

    /**
     * Gets the players score.
     *
     * @param player The player.
     * @return The score.
     */
    public static Integer getScore(Player player) {
        return (int) getPlayerData(player).get("score");
    }

    /**
     * Gets the session status.
     *
     * @param player Player profile of a player is the session.
     * @return Session status.
     */
    public static Integer getSessionStatus(Player player) {
        String query = "SELECT `status` FROM `session` WHERE `session_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, player.getSessionID());
        if (result.size() == 1) {
            return (int) result.get(0).get("status");
        } else if (result.size() == 0) {
            halt(HttpStatus.NOT_FOUND_404, "No session found.");
        } else {
            halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "SessionID not unique.");
        }
        return null;
    }

    /**
     * Changes the session status.
     *
     * @param player The player profile of the creator.
     * @param status The status to change is to.
     */
    public static boolean updateSessionStatus(Player player, int status) {
        String query = "UPDATE `session` SET `status` = ? WHERE `session_id` = ? AND `player_id` = ? ";
        if (status != 0) {
            query += " AND NOT `status` = 0";
        }
        return Database.executeManipulationQuery(query, status, player.getSessionID(), player.getPlayerID());
    }

    /**
     * Generates a unique UUID.
     *
     * @param table  Table to find a new id in.
     * @param column column name of the id.
     * @return A unique ID.
     */
    public static String generateUniqueID(String table, String column) {
        String query = "SELECT `" + column + "` FROM `" + table + "` WHERE `" + column + "` = ?";
        String id;
        List<Map<String, Object>> result;
        do {
            id = UUID.randomUUID().toString();
            result = executeSearchQuery(query, id);
        } while (result.size() != 0);
        return id;
    }

    /**
     * Generates a unique hexadecimal join token.
     *
     * @return The unique join token.
     */
    @SuppressWarnings("checkstyle:magicnumber") //Five is the length of our string.
    protected static String generateUniqueJoinToken() {
        while (true) {
            Random rand = new Random();
            String joinToken = Integer.toHexString(rand.nextInt()).substring(0, 5);
            String sql= "SELECT `join_token` FROM `session_token` WHERE `join_token` = ?";
            List<Map<String, Object>> result =
                    executeSearchQuery(sql, joinToken);
            if (result.size() == 0) {
                return joinToken;
            }
        }
    }

    /**
     * Inserts the Authorization token of a device requesting a token in the database.
     *
     * @param id    the UUID of the device
     * @param token the generated authorization token for the device
     * @return <code>true</code> if successfully added, otherwise <code>false</code>
     */
    public static boolean insertAuthorizationToken(String id, String token) {
        String sql = "INSERT INTO `device` VALUES(?, ?)";
        return executeManipulationQuery(sql, id, token);
    }

    public static String selectAuthorizationToken(String deviceID) {
        String query = "SELECT `auth_token` FROM `device` WHERE `device_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, deviceID);
        if (result.size() == 1) {
            return result.get(0).get("auth_token").toString();
        } else if (result.size() == 0) {
            return null;
        } else {
            halt(HttpStatus.INTERNAL_SERVER_ERROR_500, "DeviceID not unique.");
        }
        return null;
    }
}
