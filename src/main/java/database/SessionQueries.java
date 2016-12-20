package database;

import com.google.gson.JsonObject;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.*;

import static database.Database.executeSearchQuery;
import static spark.Spark.halt;

/**
 * Created by Thomas on 19-12-2016.
 */
public final class SessionQueries {

    /**
     * Private constructor.
     */
    private SessionQueries() { }

    /**
     * Creates a new session in the database, generates two join codes for users and moderators, and finally adds the
     * user as the administrator to the session.
     * @param deviceID The device that creates the session.
     * @return A JsonObject that contains the generated playerID, modToken and userToken
     */
    public static JsonObject createSession(String deviceID) {
        String sessionID = generateUniqueID("SELECT session_id FROM session WHERE session_id='id_placeholder'");
        String playerID = generateUniqueID("SELECT player_id FROM session_player WHERE player_id='id_placeholder'");

        String query = "INSERT INTO session (session_id, player_id, status, created) VALUES ('" + sessionID
                + "','" + playerID + "','" + 1 + "','" + LocalDateTime.now() + "')";
        Database.executeUpdateQuery(query);

        String modToken = generateUniqueJoinToken();
        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + modToken
                + "','" + sessionID + "', '" + 1 + "')";
        Database.executeUpdateQuery(query);

        String userToken = generateUniqueJoinToken();
        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + userToken
                + "','" + sessionID + "', '" + 2 + "')";
        Database.executeUpdateQuery(query);

        query = "INSERT INTO session_player (player_id, device_id, session_id, role_id, score) VALUES ('" + playerID
                + "','" + deviceID + "','" + sessionID + "','" + 0 + "','" + 0 + "')";
        Database.executeUpdateQuery(query);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modToken", modToken);
        jsonObject.addProperty("userToken", userToken);
        jsonObject.addProperty("playerID", playerID);
        jsonObject.addProperty("sessionID", sessionID);
        return jsonObject;
    }

    /**
     * Creates and adds a new player of the session corresponding with the join token.
     * @param joinToken The join token to fetch the data of.
     * @param deviceID The device of the user joining the session.
     * @return The new player.
     */
    @SuppressWarnings("checkstyle:magicnumber")
    public static Player getNewPlayerOfSession(String joinToken, String deviceID) {
        String query = "SELECT join_token,session_id,role_id FROM session_token WHERE join_token='" + joinToken + "'";
        List<Map<String, Object>> result = executeSearchQuery(query);
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
     * @param player The player to be added to the database.
     */
    protected static void addNewPlayer(Player player) {
        String query = "SELECT * FROM session_player WHERE device_id='" + player.getDeviceID()
                + "' AND session_id='" + player.getSessionID()
                + "' AND role_id='" + player.getRoleID() + "'";
        List<Map<String, Object>> result = executeSearchQuery(query);
        if (result.size() == 0) {
            Database.executeUpdateQuery("INSERT INTO "
                    + "session_player (player_id, device_id, session_id, role_id, score) VALUES ('"
                    + player.getPlayerID() + "','" + player.getDeviceID() + "','"
                    + player.getSessionID() + "','" + player.getRoleID() + "','" + +player.getScore() + "')");
        } else {
            halt(HttpStatus.UNAUTHORIZED_401, "Player already created.");
        }
    }

    /**
     * Helper class to retrieve all the player data in the database.
     * @param player The player.
     * @return A map of the data.
     */
    protected static Map<String, Object> getPlayerData(Player player) {
        String query = "SELECT * FROM session_player WHERE player_id='" + player.getPlayerID()
                + "' AND device_id='" + player.getDeviceID()
                + "' AND session_id='" + player.getSessionID() + "'";
        List<Map<String, Object>> result = executeSearchQuery(query);
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
     * @param player The player to retrieve tht roleID for.
     * @return The roleID
     */
    public static Integer getRoleId(Player player) {
        return (int) getPlayerData(player).get("role_id");
    }

    /**
     * Gets the players score.
     * @param player The player.
     * @return The score.
     */
    public static Integer getScore(Player player) {
        return (int) getPlayerData(player).get("score");
    }

    /**
     * Gets the session status.
     * @param player Player profile of a player is the session.
     * @return Session status.
     */
    public static Integer getSessionStatus(Player player) {
        String query = "SELECT status FROM session WHERE session_id='" + player.getSessionID() + "'";
        List<Map<String, Object>> result = executeSearchQuery(query);
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
     * @param player The player profile of the creator.
     * @param status The status to change is to.
     */
    public static void updateSessionStatus(Player player, int status) {
        String query = "UPDATE session SET status='" + status + "' WHERE session_id='" + player.getSessionID()
                + "' AND player_id='" + player.getPlayerID() + "'";
        if (status != 0) {
            query += " AND NOT status='0'";
        }
        Database.executeUpdateQuery(query);
    }

    /**
     * Generates a unique UUID.
     * @param query A query that contains the table and column that should be checked.
     * @return A unique ID.
     */
    public static String generateUniqueID(String query) {
        while(true) {
            String id = UUID.randomUUID().toString();
            query = query.replace("id_placeholder", id);
            List<Map<String, Object>> result = executeSearchQuery(query);
            if (result.size() == 0) {
                return id;
            }
        }
    }

    /**
     * Generates a unique hexadecimal join token.
     * @return The unique join token.
     */
    @SuppressWarnings("checkstyle:magicnumber") //Five is the length of our string.
    public static String generateUniqueJoinToken() {
        while(true) {
            Random rand = new Random();
            String joinToken = Integer.toHexString(rand.nextInt()).substring(0, 5);
            List<Map<String, Object>> result =
                    executeSearchQuery("SELECT join_token FROM session_token WHERE join_token='" + joinToken + "'");
            if (result.size() == 0) {
                return joinToken;
            }
        }
    }
}
