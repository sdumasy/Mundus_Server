package validation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static database.Database.executeSearchQuery;
import static database.Database.executeUpdateQuery;

/**
 * Created by Thomas on 18-12-2016.
 */
public final class Validation {
    /**
     * Private constructor.
     */
    private Validation() { }

    /**
     * Validate the deviceID and authToken combination supplied by the client.
     * @param deviceID the device ID
     * @param authToken the authentication token
     * @return true if the token and deviceID match the ones stored in the database, false otherwise
     */
    public static boolean authenticateDevice(String deviceID, String authToken) {
        List<Map<String, Object>> result =
                executeSearchQuery("SELECT auth_token FROM device WHERE device_id='" + deviceID + "'");

        return result.size() > 0 && result.get(0).get("auth_token").toString().equals(authToken);
    }

    /**
     * Generate a new token, then store it in the DB and return it.
     * @param deviceID the device ID
     * @return the newly generated authToken
     */
    public static String createToken(String deviceID) {
        String authToken = UUID.randomUUID().toString();
        executeUpdateQuery("INSERT INTO device VALUES ('" + deviceID + "','" + authToken + "');");

        return authToken;
    }
}
