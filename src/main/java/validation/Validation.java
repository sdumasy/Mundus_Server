package validation;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static database.Database.executeManipulationQuery;
import static database.Database.executeSearchQuery;

/**
 * Validation of authentication token.
 */
public final class Validation {
    /**
     * Private constructor.
     */
    private Validation() {
    }

    /**
     * Checks whether the device has a registered token.
     *
     * @param deviceID The device of the token.
     * @return Whether it already has a registered token.
     */
    public static boolean hasToken(String deviceID) {
        String sql = "SELECT COUNT(`device_id`) AS 'count' FROM `device` WHERE `device_id`= ?";
        List<Map<String, Object>> result = executeSearchQuery(sql, deviceID);

        int count = Integer.valueOf(result.get(0).get("count").toString());
        return result.size() > 0 && count == 1;
    }

    /**
     * Validate the deviceID and authToken combination supplied by the client.
     *
     * @param deviceID  the device ID
     * @param authToken the authentication token
     * @return true if the token and deviceID match the ones stored in the database, false otherwise
     */
    public static boolean authenticateDevice(String deviceID, String authToken) {
        String sql = "SELECT COUNT(`device_id`) AS 'count' FROM `device` WHERE `device_id`= ? AND `auth_token` = ?";
        List<Map<String, Object>> result = executeSearchQuery(sql, deviceID, authToken);

        int count = Integer.valueOf(result.get(0).get("count").toString());
        return result.size() > 0 && count == 1;
    }

    /**
     * Generate a new token, then store it in the DB and return it.
     *
     * @param deviceID the device ID
     * @return the newly generated authToken
     */
    public static String createToken(String deviceID) {
        String sql = "INSERT INTO `device` VALUES(?, ?)";
        String authToken = UUID.randomUUID().toString();
        executeManipulationQuery(sql, deviceID, authToken);
        return authToken;
    }
}
