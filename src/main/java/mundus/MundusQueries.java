package mundus;

import com.google.gson.JsonObject;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static database.Database.executeManipulationQuery;
import static database.Database.executeSearchQuery;
import static util.Halt.halter;

/**
 * Created by Thomas on 4-1-2017.
 */
public final class MundusQueries {

    /**
     * Private constructor.
     */
    private MundusQueries() {
        //Empty by design
    }

    /**
     * Get a new question for a player if it has no more than two questions at the moment and there still are new
     * questions available.
     * @param player The player that wants a new question
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject getQuestion(Player player) {
        if(assignedQuestions(player.getPlayerID()) < 3) {
            return verifyAssignQuestion(player.getPlayerID(), player.getSession().getSessionID());
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "You have reached the maximum amount of assigned questions.");
            return null;
        }
    }

    /**
     * Get the amount of questions that has been assigned to a player.
     * @param playerID The ID of the player that needs to be checked.
     * @return The amount of questions assigned to the player with playerID
     */
    public static int assignedQuestions(String playerID) {
        String query = "SELECT `question_id` FROM `session_question` WHERE `player_id` = ? AND `reviewed` IS NOT 1 ";
        List<Map<String, Object>> result = executeSearchQuery(query, playerID);
        return result.size();
    }

    /**
     * Verify if there are still new questions available within a session, if so assign one.
     * @param playerID The player that wants a new question
     * @param sessionID The id of the session that the player is part of.
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject verifyAssignQuestion(String playerID, String sessionID) {
        String query = "SELECT * FROM `question` WHERE `question`.`question_id` NOT IN ( " +
                "SELECT `question_id` FROM `session_question` WHERE `session_id = ?)";
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID);
        Random r = new Random();
        if(result.size() > 0) {
            int i = r.nextInt(result.size());
            Map<String, Object> m = result.get(i);
            return assignQuestion(m, playerID, sessionID);
        }
        halter(HttpStatus.NOT_FOUND_404, "There are no more questions.");
        return null;
    }

    /**
     * Add a new question and player assignment to the database and return the question as JsonObject.
     * @param m The question information.
     * @param playerID The player id.
     * @param sessionID The id of the session that the player is part of.
     * @return A JsonObject containing the player information.
     */
    public static JsonObject assignQuestion(Map<String, Object> m, String playerID, String sessionID) {
        String questionID = m.get("question_id").toString();
        String query = "INSERT INTO `session_question` (question_id, player_id, session_id) VALUES(?, ?, ?)";
        executeManipulationQuery(query, questionID, playerID, sessionID);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("question_id", questionID);
        jsonObject.addProperty("text", m.get("text").toString());
        return jsonObject;
    }
}
