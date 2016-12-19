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

    public static JsonObject createSession(String playerID){
        String sessionID = generateUniqueSessionID();

        String query = "INSERT INTO session (session_id, player_id, status, created) VALUES ('" + sessionID +
                "','" + playerID + "','" + 1 + "','" + LocalDateTime.now() + "')";
        Database.excecuteUpdateQuery(query);

        String modToken = generateUniqueJoinToken();
        String userToken = generateUniqueJoinToken();

        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + modToken +
                "','" + sessionID + "', '" + 1 + "')";
        Database.excecuteUpdateQuery(query);

        query = "INSERT INTO session_token (join_token, session_id, role_id) VALUES ('" + userToken +
                "','" + sessionID + "', '" + 1 + "')";
        Database.excecuteUpdateQuery(query);

        //TODO: Add player_id with administrator role

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("modToken", modToken);
        jsonObject.addProperty("userToken", userToken);
        return jsonObject;

    }

    public void inserToken(String sessionID, String token){

    }

    public static String generateUniqueSessionID(){
        while(true) {
            String sessionID = UUID.randomUUID().toString();
            String result = excecuteSearchQuery("SELECT session_id FROM session WHERE session_id='" + sessionID + "'");
            JsonObject[] jArray = new Gson().fromJson(result, JsonObject[].class);

            if (jArray.length == 0) {
                return sessionID;
            }
        }
    }

    public static String generateUniqueJoinToken(){
        while(true) {
            Random rand = new Random();
            String joinToken = Integer.toHexString(rand.nextInt(10000)).substring(0,5);
            String result = excecuteSearchQuery("SELECT join_token FROM session_token WHERE join_token='" + joinToken + "'");
            JsonObject[] jArray = new Gson().fromJson(result, JsonObject[].class);

            if (jArray.length == 0) {
                return joinToken;
            }
        }
    }
}
