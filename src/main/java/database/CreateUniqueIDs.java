package database;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static database.Database.executeSearchQuery;
import static database.SessionQueries.insertAuthorizationToken;

/**
 * Creates unique tokens.
 */
public class CreateUniqueIDs {

    /**
     * Private constructor.
     */
    private CreateUniqueIDs() {
        //empty on purpose
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

    /**
     * Generates a unique UUID.
     *
     * @param table  Table to find a new id in.
     * @param column column name of the id.
     * @return A unique ID.
     */
    public static String generateUniqueID(String table, String column) {
        String query = "SELECT `" + column + "` FROM `" + table + "` WHERE `" + column + "` = ?";
        String id;
        List<Map<String, Object>> result;
        do {
            id = UUID.randomUUID().toString();
            result = executeSearchQuery(query, id);
        } while (result.size() != 0);
        return id;
    }

    /**
     * Generates a unique hexadecimal join token.
     *
     * @return The unique join token.
     */
    @SuppressWarnings("checkstyle:magicnumber") //Five is the length of our string.
    protected static String generateUniqueJoinToken() {
        while (true) {
            Random rand = new Random();
            String joinToken = Integer.toHexString(rand.nextInt()).substring(0, 5);
            String sql = "SELECT `join_token` FROM `session_token` WHERE `join_token` = ?";
            List<Map<String, Object>> result =
                    executeSearchQuery(sql, joinToken);
            if (result.size() == 0) {
                return joinToken;
            }
        }
    }
}
