package database;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.Device;
import models.Player;
import models.Role;
import models.Session;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static database.CreateUniqueIDs.generateUniqueID;
import static database.CreateUniqueIDs.generateUniqueJoinToken;
import static database.Database.executeManipulationQuery;
import static database.Database.executeSearchQuery;
import static util.Halt.halter;

/**
 * Contains the sql queries for the sessions.
 */
@SuppressWarnings("PMD.TooManyStaticImports")
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
     * @param username The username of the admin.
     * @return A JsonObject that contains the generated playerID, modToken and userToken
     */
    public static JsonObject createSession(Device device, String username) {
        String sessionID = generateUniqueID("session", "session_id");
        String playerID = generateUniqueID("session_player", "player_id");
        int status = 1;
        LocalDateTime created = LocalDateTime.now();

        String query = "INSERT INTO `session` VALUES (?, ?, ?, ?)";
        executeManipulationQuery(query, sessionID, playerID, status, created);

        String modToken = generateUniqueJoinToken();
        query = "INSERT INTO `session_token` VALUES (?, ?, ?)";
        executeManipulationQuery(query, modToken, sessionID, 1);

        String userToken = generateUniqueJoinToken();
        executeManipulationQuery(query, userToken, sessionID, 2);

        query = "INSERT INTO `session_player` VALUES (?, ?, ?, ?, ?, ?)";
        executeManipulationQuery(query, playerID, device.getDeviceID(), sessionID, 0, 0, username);

        Session session = new Session(sessionID, playerID, status, created);
        Player player = new Player(playerID, session, device, Role.Admin, 0, username);

        JsonObject jsonTokens = new JsonObject();
        jsonTokens.addProperty("moderator", modToken);
        jsonTokens.addProperty("user", userToken);

        JsonObject json = player.toJson();
        json.getAsJsonObject("session").add("tokens", jsonTokens);
        return json;
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
            halter(HttpStatus.UNAUTHORIZED_401, "Invalid joinToken");
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "Identical joinTokens in database");
        }
        return null;
    }

    /**
     * Checks whether the given device is a member of the given session.
     *
     * @param sessionID The sessionID.
     * @param device    The device.
     * @return Whether the device is a member of the session.
     */
    public static boolean isMember(String sessionID, Device device) {
        String query = "SELECT * FROM `session_player` WHERE `session_id` = ? AND `device_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID, device.getDeviceID());

        return result.size() > 0;
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
        if (result.size() < 1) {
            halter(HttpStatus.NOT_FOUND_404, "No session found.");
        }
        if (result.size() > 1) {
            halter(HttpStatus.INTERNAL_SERVER_ERROR_500, "SessionID not unique.");
        }

        Map<String, Object> map = result.get(0);
        return new Session(sessionID, map.get("player_id").toString(), (Integer) map.get("status"),
                    Timestamp.valueOf(map.get("created").toString()).toLocalDateTime());
    }

    /**
     * Gets the tokens of a session by using the ID of a session.
     *
     * @param sessionID the ID of the session
     * @return an instance of <code>JsonObject</code> containing the tokens
     */
    public static JsonObject getSessionTokens(String sessionID) {
        String query = "SELECT `role_id`, `join_token` FROM `session_token`"
                + "WHERE `session_id` = ? ORDER BY `role_id` ASC";
        String[] keys = {"moderator", "user"};
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID);
        JsonObject json = new JsonObject();
        if (result.size() < 2) {
            halter(HttpStatus.NOT_FOUND_404, "No session tokens found.");
        }
        if (result.size() > 2) {
            halter(HttpStatus.INTERNAL_SERVER_ERROR_500, "SessionID not unique.");
        }

        for (int i = 0; i < result.size(); i++) {
            Map<String, Object> row = result.get(i);
            json.addProperty(keys[i], row.get("join_token").toString());
        }
        return json;
    }

    /**
     * Changes the session status.
     *
     * @param sessionID The sessionID.
     * @param status The status to change is to.
     * @return A boolean value indicating succes.
     */
    public static boolean updateSessionStatus(String sessionID, int status) {
        String query = "UPDATE `session` SET `status` = ? WHERE `session_id` = ? ";
        if (status != 0) {
            query += " AND NOT `status` = 0";
        }
        return executeManipulationQuery(query, status, sessionID);
    }

    /**
     * Get all scores in a session.
     *
     * @param sessionID    the id of the session.
     * @return A JsonArray containing the scores of all players within a session.
     */
    public static JsonObject getPlayers(String sessionID) {
        String query = "SELECT `player_id`, `username`, `score` FROM `session_player`  WHERE `session_id`=? ";
        JsonArray jsonArray = new JsonArray();
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID);
        for (Map<String, Object> aResult : result) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerID", aResult.get("player_id").toString());
            jsonObject.addProperty("username", aResult.get("username").toString());
            jsonObject.addProperty("score", aResult.get("score").toString());
            jsonArray.add(jsonObject);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("players", jsonArray);
        return jsonObject;
    }

    /**
     * Retrieve all sessions from the database.
     *
     * @return All sessions in the database.
     */
    public static List<Session> getAllSession() {
        String query = "SELECT * FROM `session`";
        List<Map<String, Object>> result = executeSearchQuery(query);

        List<Session> sessions = new LinkedList<>();
        for (Map<String, Object> map : result) {
            sessions.add(new Session(map.get("session_id").toString(), map.get("player_id").toString(),
                    (Integer) map.get("status"), Timestamp.valueOf(map.get("created").toString()).toLocalDateTime()));
        }
        return sessions;
    }
}
