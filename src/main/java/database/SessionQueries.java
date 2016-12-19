package database;

import com.google.gson.JsonObject;
import models.Player;
import spark.http.matching.Halt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static database.Database.executeSearchQuery;
import static spark.Spark.halt;

/**
 * Created by Thomas on 19-12-2016.
 */
public class SessionQueries {

    public static JsonObject createSession(String deviceID){
        String sessionID = generateUniqueID("SELECT session_id FROM session WHERE session_id='id_placeholder'");
        String playerID = generateUniqueID("SELECT player_id FROM session_player WHERE player_id='id_placeholder'");

        String query = "INSERT INTO session (session_id, player_id, status, created) VALUES ('" + sessionID +
                "','" + playerID + "','" + 1 + "','" + LocalDateTime.now() + "')";
        Database.executeUpdateQuery(query);

        String modToken = generateUniqueJoinToken();
        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + modToken +
                "','" + sessionID + "', '" + 1 + "')";
        Database.executeUpdateQuery(query);

        String userToken = generateUniqueJoinToken();
        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + userToken +
                "','" + sessionID + "', '" + 2 + "')";
        Database.executeUpdateQuery(query);

        query = "INSERT INTO session_player (player_id, device_id, session_id, role_id, score) VALUES ('" + playerID +
                "','" + deviceID + "','" + sessionID + "','" + 0 + "','" + 0 + "')";
        Database.executeUpdateQuery(query);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modToken", modToken);
        jsonObject.addProperty("userToken", userToken);
        return jsonObject;
    }

    public static Player retrieveSessionToken(String joinToken) {
        String query = "SELECT join_token,session_id,role_id FROM session_token WHERE join_token='" + joinToken + "'";
        List<Map<String, Object>> result = executeSearchQuery(query);
        if (result.size()==1) {
            return new Player(result.get(0).get("session_id").toString(),(int) result.get(0).get("role_id"));
        } else if (result.size()==0){
            halt(401,"Invalid joinToken");
        } else {
            halt(401,"Identical joinTokens in database");
        }
        return null;
    }

    public static void addNewPlayer(Player player, String deviceID) {
        String query = "SELECT * FROM session_player WHERE device_id='" + deviceID
                + "' AND session_id='" + player.getSessionID()
                + "' AND role_id='" + player.getRoleID() + "'";
        List<Map<String, Object>> result = executeSearchQuery(query);
        if (result.size()==0) {
            Database.executeUpdateQuery("INSERT INTO session_player (player_id, device_id, session_id, role_id, score) VALUES ('"
                    + player.getPlayerID() + "','" + deviceID + "','"
                    + player.getSessionID() + "','" + player.getRoleID() + "','" + +player.getScore() + "')");
        } else {
            halt(401,"Player already created.");
        }
    }


    public static String generateUniqueID(String query){
        while(true) {
            String id = UUID.randomUUID().toString();
            query = query.replace("id_placeholder", id);
            List<Map<String, Object>> result = executeSearchQuery(query);
            if (result.size() == 0) {
                return id;
            }
        }
    }

    public static String generateUniqueJoinToken(){
        while(true) {
            Random rand = new Random();
            String joinToken = Integer.toHexString(rand.nextInt()).substring(0,5);
            List<Map<String, Object>> result = executeSearchQuery("SELECT join_token FROM session_token WHERE join_token='" + joinToken + "'");
            if (result.size() == 0) {
                return joinToken;
            }
        }
    }
}
