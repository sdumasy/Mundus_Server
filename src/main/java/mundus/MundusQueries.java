package mundus;

import com.google.gson.JsonArray;
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
 * Mundus queries.
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
        if (assignedQuestions(player.getPlayerID()) < 3) {
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
        String query = "SELECT `question_id` FROM `session_question` "
                + "WHERE `player_id` = ? AND (`reviewed` != 1 OR `reviewed` IS NULL) ";
        return executeSearchQuery(query, playerID).size();
    }

    /**
     * Verify if there are still new questions available within a session, if so assign one.
     * @param playerID The player that wants a new question
     * @param sessionID The id of the session that the player is part of.
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject verifyAssignQuestion(String playerID, String sessionID) {
        String query = "SELECT `question_id`, `question` FROM `question` WHERE `question_id` NOT IN ( "
                + "SELECT `question_id` FROM `session_question` WHERE `session_id` = ?);";
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID);
        Random r = new Random();
        if (result.size() > 0) {
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
        jsonObject.addProperty("question", m.get("question").toString());
        return jsonObject;
    }

    public static void submitAnswer(Player player, String questionID, JsonObject jsonObject) {
        if(isAssigned(player.getPlayerID(), questionID)) {
            String answer = jsonObject.get("answer").getAsString();
            String query = "UPDATE `session_question` SET `answer` = ?, `reviewed` = -1 "
                    + "WHERE `player_id` = ? AND `question_id` = ?";
            executeManipulationQuery(query, answer, player.getPlayerID(), questionID);
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "This question was not assigned to your playerID.");
        }
    }

    private static boolean isAssigned(String playerID, String questionID) {
        String query = "SELECT `question_id` FROM `session_question` WHERE `player_id` = ? AND `question_id` = ? ";
        return executeSearchQuery(query, playerID, questionID).size() == 1;
    }

    /**
     * Returns all unreviewed answers in a session to an admin or moderator.
     * @param player The player that wants the answers
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject getSubmitted(Player player) {
        if (player.isAdmin() || player.isModerator()) {
            String query = "SELECT `question`.`question`, `question`.`question_id`, `question`.`correct_answer`, "
                    + "`session_question`.`answer` FROM `session_question` INNER JOIN `question` "
                    + "ON `session_question`.`question_id`=`question`.`question_id`"
                    + "WHERE `session_question`.`session_id` = ? AND `session_question`.`reviewed` = -1";
            return answersToJson(executeSearchQuery(query, player.getSession().getSessionID()));
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "You are not an admin or moderator of your session.");
            return null;
        }
    }

    /**
     * Converts the answers to a JsonObject.
     * @param maps The answers in maps.
     * @return The converted JsonObject.
     */
    private static JsonObject answersToJson(List<Map<String, Object>> maps) {
        JsonArray jsonArray = new JsonArray();
        for (Map<String, Object> result : maps) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("answer", result.get("answer").toString());
            jsonObject.addProperty("correct_answer", result.get("correct_answer").toString());
            jsonObject.addProperty("question_id", result.get("question_id").toString());
            jsonObject.addProperty("question", result.get("question").toString());
            jsonArray.add(jsonObject);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("answers", jsonArray);
        return jsonObject;
    }


    public static void submitReview(Player player, String questionID, JsonObject jsonObject) {
        if (player.isAdmin() || player.isModerator()) {
            String review = jsonObject.get("reviewed").getAsString();
            String query = "UPDATE `session_question` SET `reviewed` = ?"
                    + "WHERE `session_id` = ? AND `question_id` = ?";
            executeManipulationQuery(query, review, player.getSession().getSessionID(), questionID);
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "You are not an admin or moderator of this session.");
        }
    }

    /**
     * Returns all published (correct) answers in a session to a player.
     * @param player The player that wants the answers
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject getPublications(Player player) {
        String query = "SELECT `question`.`question`, `question`.`question_id`, `question`.`correct_answer`, "
                + "`session_question`.`answer` FROM `session_question` INNER JOIN `question` "
                + "ON `session_question`.`question_id`=`question`.`question_id`"
                + "WHERE `session_question`.`session_id` = ? AND `session_question`.`reviewed` = 1";
        return answersToJson(executeSearchQuery(query, player.getSession().getSessionID()));
    }
}
