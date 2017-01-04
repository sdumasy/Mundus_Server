package database;

import org.eclipse.jetty.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static database.Database.executeManipulationQuery;
import static database.Database.executeSearchQuery;
import static util.Halt.halter;

/**
 * Database queries associated with the authentication token.
 */
public final class AuthenticationTokenQueries {

    /**
     * Private constructor.
     */
    private AuthenticationTokenQueries() {
        //Empty by design
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
        if (result.size() > 1) {
            halter(HttpStatus.INTERNAL_SERVER_ERROR_500, "DeviceID not unique.");
        }
        if (result.size() == 1) {
            halter(HttpStatus.UNAUTHORIZED_401, "Already have an authentication token.");
        }
        return result.get(0).get("auth_token").toString();
    }
}
