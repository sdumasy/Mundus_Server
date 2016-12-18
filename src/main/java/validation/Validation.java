package validation;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.UUID;

import static database.Database.excecuteSearchQuery;
import static database.Database.excecuteUpdateQuery;

/**
 * Created by Thomas on 18-12-2016.
 */
public class Validation {

    /**
     * Validate the deviceID and authToken combination supplied by the client
     * @param deviceID the device ID
     * @param authToken the authentication token
     * @return true if the token and deviceID match the ones stored in the database, false otherwise
     */
    public static boolean authenticateDevice(String deviceID, String authToken) {
        String result = excecuteSearchQuery("SELECT auth_token FROM device WHERE device_id='" + deviceID + "'");
        JsonObject[] jArray = new Gson().fromJson(result, JsonObject[].class);

        return jArray.length > 0 && jArray[0].get("auth_token").getAsString().equals(authToken);
    }

    /**
     * Generate a new token, then store it in the DB and return it
     * @param deviceID the device ID
     * @return the newly generated authToken
     */
    public static String createToken(String deviceID) {
        String authToken = UUID.randomUUID().toString();
        excecuteUpdateQuery("INSERT INTO device VALUES ('" + deviceID + "','" + authToken + "');");

        return authToken;
    }
}
