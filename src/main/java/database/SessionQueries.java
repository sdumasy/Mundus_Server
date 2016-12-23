package database;

import com.google.gson.JsonObject;
import models.Device;
import models.Player;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Timestamp;
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
        Database.executeManipulationQuery(query, userToken, sessionID, 2);

        query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?)";
        Database.executeManipulationQuery(query, playerID, device.getDeviceID(), sessionID, 0, 0);

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
     * @param device    The device of the user joining the session.
     * @param username  Username of a player.
     * @return The new player.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static Player playerJoinSession(String joinToken, Device device, String username) {
        String query = "SELECT `join_token`, `session_id`, `role_id` FROM `session_token` WHERE `join_token` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, joinToken);
        if (result.size() == 1) {
            Map<String, Object> map = result.get(0);
            return Player.newPlayer(Session.getSession(map.get("session_id").toString()),
                    (int) map.get("role_id"), device, 0, username);
        } else if (result.size() == 0) {
            halt(HttpStatus.UNAUTHORIZED_401, "Invalid joinToken");
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Identical joinTokens in database");
        }
        return null;
    }

    /**
     * Gets the session by ID.
     *
     * @param sessionID Id of the session.
     * @return The session.
     */
    public static Session getSession(String sessionID) {
        String query = "SELECT * FROM `session` WHERE `session_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID);
        if (result.size() == 1) {
            Map<String, Object> map = result.get(0);
            return new Session(sessionID, map.get("player_id").toString(), (Integer) map.get("status"),
                    Timestamp.valueOf(map.get("created").toString()).toLocalDateTime());
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
     * @return A boolean value indicating succes.
     */
    public static boolean updateSessionStatus(Player player, int status) {
        String query = "UPDATE `session` SET `status` = ? WHERE `session_id` = ? AND `player_id` = ? ";
        if (status != 0) {
            query += " AND NOT `status` = 0";
        }
        return Database.executeManipulationQuery(query, status, player.getSession().getSessionID(),
                player.getPlayerID());
    }

    /**
     * Inserts the Authorization token of a device requesting a token in the database.
     *
     * @param id    the UUID of the device
     * @param token the generated authorization token for the device
     * @return <code>true</code> if successfully added, otherwise <code>false</code>
     */
    protected static boolean insertAuthorizationToken(String id, String token) {
        String sql = "INSERT INTO `device` VALUES(?, ?)";
        return executeManipulationQuery(sql, id, token);
    }

    /**
     * Get a device token by ID.
     *
     * @param deviceID The device which token should be recovered
     * @return The token
     */
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
