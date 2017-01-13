package mundus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.Player;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
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
     * Retrieves all the questionIDS.
     *
     * @return A list of questionIDs.
     */
    public static List<String> getAllQuestionIDs() {
        String query = "SELECT `question_id` FROM `question`";
        List<Map<String, Object>> result = executeSearchQuery(query);
        List<String> questionIDs = new ArrayList<>();
        for (Map<String, Object> map : result) {
            questionIDs.add(map.get("question_id").toString());
        }
        return questionIDs;
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

    /**
     * Submit an answer to a question.
     *
     * @param player     The player answering the question.
     * @param questionID The question being answered.
     * @param answer     The answer to the question.
     */
    public static void submitAnswer(Player player, String questionID, String answer) {
        if (isAssigned(player.getPlayerID(), questionID)) {
            String query = "UPDATE `session_question` SET `answer` = ?, `reviewed` = -1 "
                    + "WHERE `player_id` = ? AND `question_id` = ?";
            executeManipulationQuery(query, answer, player.getPlayerID(), questionID);
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "This question was not assigned to your playerID.");
        }
    }

    /**
     * Whether the specified question is assigned to the specified player.
     * @param playerID The player.
     * @param questionID The id of the question.
     * @return Whether the question is assigned to the player.
     */
    public static boolean isAssigned(String playerID, String questionID) {
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
            String query = "SELECT `q`.`question`, `q`.`question_id`, `q`.`correct_answer`, "
                    + "`sq`.`answer` FROM `session_question` `sq` INNER JOIN `question` `q` "
                    + "ON `sq`.`question_id`=`q`.`question_id`"
                    + "WHERE `sq`.`session_id` = ? AND `sq`.`reviewed` = -1";
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


    /**
     * Submit a review of a question.
     * @param player The player reviewing the question.
     * @param questionID The question being reviewed.
     * @param review The review of the question.
     */
    public static void submitReview(Player player, String questionID, int review) {
        if (player.isAdmin() || player.isModerator()) {
            String query = "UPDATE `session_question` SET `reviewed` = ? "
                    + "WHERE `session_id` = ? AND `question_id` = ?";
            executeManipulationQuery(query, review, player.getSession().getSessionID(), questionID);
            if (review == 1) {
                increaseScore(questionID, player.getSession().getSessionID());
            }
        } else {
            halter(HttpStatus.UNAUTHORIZED_401, "You are not an admin or moderator of this session.");
        }
    }

    /**
     * Increase a players score by questionID that has been approved.
     * @param questionID The question that has been approved
     * @param sessionID The session that the player is in.
     */
    public static void increaseScore(String questionID, String sessionID) {
        String query = "SELECT `player_id` FROM  `session_question` WHERE `question_id` = ? AND `session_id` = ?";
        List<Map<String, Object>> result = executeSearchQuery(query, questionID, sessionID);
        Map<String, Object> map = result.get(0);
        String playerID = map.get("player_id").toString();

        query = "SELECT `score` FROM  `session_player` WHERE `player_id` = ?";
        result = executeSearchQuery(query, playerID);
        map = result.get(0);
        int score = Integer.parseInt(map.get("score").toString());
        score = score + 1;
        query = "UPDATE `session_player` SET  `score` = ? WHERE `player_id` = ?";
        executeManipulationQuery(query, score, playerID);
    }

    /**
     * Returns all published (correct) answers in a session to a player.
     * @param player The player that wants the answers
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject getPublications(Player player) {
        String query = "SELECT `q`.`question`, `q`.`question_id`, `q`.`correct_answer`, "
                + "`sq`.`answer` FROM `session_question` `sq` INNER JOIN `question` `q` "
                + "ON `sq`.`question_id`=`q`.`question_id`"
                + "WHERE `sq`.`session_id` = ? AND `sq`.`reviewed` = 1";
        return answersToJson(executeSearchQuery(query, player.getSession().getSessionID()));
    }

    /**
     * Get all player scores and publications in a session.
     *
     * @param sessionID    the id of the session.
     * @return A JsonArray containing the scores of all players within a session.
     */
    public static JsonObject getPlayersPublications(String sessionID) {
        String query = "SELECT `player_id`, `username`, `score` FROM `session_player`  WHERE `session_id`=? ";
        JsonArray jsonArray = new JsonArray();
        List<Map<String, Object>> result = executeSearchQuery(query, sessionID);
        for (Map<String, Object> aResult : result) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerID", aResult.get("player_id").toString());
            jsonObject.addProperty("username", aResult.get("username").toString());
            jsonObject.addProperty("score", aResult.get("score").toString());
            jsonObject.add("publications", getPlayerPublications(aResult.get("player_id").toString()));

            jsonArray.add(jsonObject);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("players", jsonArray);
        return jsonObject;
    }

    /**
     * Get all publications made by a playerID and return in a JsonArray.
     * @param playerID The playerID
     * @return A JsonArray with the publications
     */
    private static JsonArray getPlayerPublications(String playerID) {
        String query = "SELECT `q`.`question`, `q`.`question_id`, `q`.`correct_answer`, "
                + "`sq`.`answer` FROM `session_question` `sq` INNER JOIN `question` `q` "
                + "ON `sq`.`question_id`=`q`.`question_id`"
                + "WHERE `sq`.`player_id` = ? AND `sq`.`reviewed` = 1";
        JsonObject jsonObject = answersToJson(executeSearchQuery(query, playerID));
        return jsonObject.get("answers").getAsJsonArray();
    }

    /**
     * Returns all questions that have been assigned to a player.
     * @param player The player that wants the questions assigned to him
     * @return A JsonObject that contains the new questions id and text.
     */
    public static JsonObject getAssignedQuestions(Player player) {
        String query = "SELECT `q`.`question`, `q`.`question_id` FROM `session_question` `sq` "
                + "INNER JOIN `question` `q` ON `sq`.`question_id`=`q`.`question_id`"
                + "WHERE `sq`.`player_id` = ? AND (`sq`.`reviewed` != 1 OR `sq`.`reviewed` IS NULL)";
        JsonArray jsonArray = new JsonArray();
        List<Map<String, Object>> result = executeSearchQuery(query, player.getPlayerID());
        for (Map<String, Object> aResult : result) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("question_id", aResult.get("question_id").toString());
            jsonObject.addProperty("question", aResult.get("question").toString());
            jsonArray.add(jsonObject);
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("questions", jsonArray);
        return jsonObject;
    }
}
