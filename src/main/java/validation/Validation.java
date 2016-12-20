package validation;

import java.util.UUID;

import static database.SessionQueries.insertAuthorizationToken;
import static database.SessionQueries.selectAuthorizationToken;

/**
 * Validation of authentication token.
 */
public final class Validation {
    /**
     * Private constructor.
     */
    private Validation() { }

    /**
     * Checks whether the device has a registered token.
     * @param deviceID The device of the token.
     * @return Whether it already has a registered token.
     */
    public static boolean hasToken(String deviceID) {
        return selectAuthorizationToken(deviceID) != null;
    }

    /**
     * Validate the deviceID and authToken combination supplied by the client.
     * @param deviceID the device ID
     * @param authToken the authentication token
     * @return true if the token and deviceID match the ones stored in the database, false otherwise
     */
    public static boolean authenticateDevice(String deviceID, String authToken) {
        String authTokenDB = selectAuthorizationToken(deviceID);
        return authTokenDB != null && authTokenDB.equals(authToken);
    }

    /**
     * Generate a new token, then store it in the DB and return it.
     * @param deviceID the device ID
     * @return the newly generated authToken
     */
    public static String createToken(String deviceID) {
        String authToken = UUID.randomUUID().toString();
        insertAuthorizationToken(deviceID, authToken);
        return authToken;
    }
}
