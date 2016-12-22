package database;

import com.google.gson.JsonObject;
import models.Device;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static database.CreateUniqueIDs.generateUniqueID;
import static database.CreateUniqueIDs.generateUniqueJoinToken;
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
     * @param device The device that creates the session.
     * @return A JsonObject that contains the generated playerID, modToken and userToken
     */
    public static JsonObject createSession(Device device) {
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
        Database.executeManipulationQuery(query, playerID, device.getToken(), sessionID, 0, 0);

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
     * @param joinToken The join token of the session to join.
     * @param deviceID  The device of the user joining the session.
     * @return The new player.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static Player playerJoinSession(String joinToken, String deviceID) {
        String query = "SELECT `join_token`, `session_id`, `role_id` FROM `session_token` WHERE `join_token` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, joinToken);
        if (result.size() == 1) {
            Map<String, Object> map = result.get(0);
            return Player.newPlayer(map.get("session_id").toString(), (int) map.get("role_id"), deviceID, 0);
        } else if (result.size() == 0) {
            halt(HttpStatus.UNAUTHORIZED_401, "Invalid joinToken");
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Identical joinTokens in database");
        }
        return null;
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
