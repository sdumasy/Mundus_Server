package mundus;

import com.google.gson.JsonObject;
import framework.Aldo;
import org.eclipse.jetty.http.HttpStatus;

import static mundus.MundusQueries.*;
import static util.Halt.halter;


/**
 * Temporary class for Expedition Mundus implementation, eventually this should gets its own project.
 */
public final class ExpeditionMundus {

    /**
     * Implementation of Expedition Mundus.
     */
    private ExpeditionMundus() {
        //Empty by design.
    }

    /**
     * Creates http routes.
     */
    public static void create() {
        Aldo.subscribe("/answer", new String[]{"/question/:questionID/answer"}, (player, sessionID) ->
                player.isAdmin() || player.isModerator());
        questionPaths();
        moderatorQuestionPaths();
    }

    /**
     * Creates routes associated with research questions.
     */
    protected static void questionPaths() {
        Aldo.get("/question", (player, json) -> getQuestion(player));

        Aldo.post("/question/:questionID/answer", (player, json) -> {
            String questionID = json.get(":questionid").getAsString();
            if (!json.has("answer")) {
                halter(HttpStatus.BAD_REQUEST_400, "'answer' not specified in json body");
            }
            String answer = json.get("answer").getAsString();
            submitAnswer(player, questionID, answer);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerID", player.getPlayerID());
            jsonObject.addProperty("questionID", questionID);
            jsonObject.addProperty("answer", answer);
            return jsonObject;
        });

        Aldo.get("/publications", (player, json) -> getPublications(player));
    }

    /**
     * Question paths only accessible for moderator and admin.
     */
    protected static void moderatorQuestionPaths() {
        Aldo.get("/submitted", (player, json) -> getSubmitted(player));

        Aldo.put("/question/:questionID/review", (player, json) -> {
            String questionID = json.get(":questionid").getAsString();
            if (!json.has("reviewed")) {
                halter(HttpStatus.BAD_REQUEST_400, "'reviewed' not specified in json body");
            }
            String review = json.get("reviewed").getAsString();
            submitReview(player, questionID, review);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerID", player.getPlayerID());
            jsonObject.addProperty("questionID", questionID);
            jsonObject.addProperty("review", review);
            return jsonObject;
        });
    }
}
