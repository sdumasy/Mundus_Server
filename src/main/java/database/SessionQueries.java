package database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

import static database.Database.excecuteSearchQuery;

/**
 * Created by Thomas on 19-12-2016.
 */
public class SessionQueries {

    public static JsonObject createSession(String deviceID){
        String sessionID = generateUniqueID("SELECT session_id FROM session WHERE session_id='id_placeholder'");
        String playerID = generateUniqueID("SELECT player_id FROM session_player WHERE player_id='id_placeholder'");

        String query = "INSERT INTO session (session_id, player_id, status, created) VALUES ('" + sessionID +
                "','" + playerID + "','" + 1 + "','" + LocalDateTime.now() + "')";
        Database.excecuteUpdateQuery(query);

        String modToken = generateUniqueJoinToken();
        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + modToken +
                "','" + sessionID + "', '" + 1 + "')";
        Database.excecuteUpdateQuery(query);

        String userToken = generateUniqueJoinToken();
        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + userToken +
                "','" + sessionID + "', '" + 2 + "')";
        Database.excecuteUpdateQuery(query);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modToken", modToken);
        jsonObject.addProperty("userToken", userToken);
        return jsonObject;
    }

    public static String generateUniqueID(String query){
        while(true) {
            String id = UUID.randomUUID().toString();
            query = query.replace("id_placeholder", id);
            String result = excecuteSearchQuery(query);
            JsonObject[] jArray = new Gson().fromJson(result, JsonObject[].class);
            if (jArray.length == 0) {
                return id;
            }
        }
    }

    public static String generateUniqueJoinToken(){
        while(true) {
            Random rand = new Random();
            String joinToken = Integer.toHexString(rand.nextInt()).substring(0,5);
            String result = excecuteSearchQuery("SELECT join_token FROM session_token WHERE join_token='" + joinToken + "'");
            JsonObject[] jArray = new Gson().fromJson(result, JsonObject[].class);
            if (jArray.length == 0) {
                return joinToken;
            }
        }
    }
}
