package mundus;

import com.google.gson.JsonObject;
import framework.Aldo;
import http.SubscriptionWebSocket;
import org.eclipse.jetty.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

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
        List<SubscriptionWebSocket> sockets = new ArrayList<>();
        sockets.add(Aldo.subscribe("/answer", new String[]{"/question/:questionID/answer"},
                (player, sessionID) -> player.isAdmin() || player.isModerator()));
        for (String questionID : getAllQuestionIDs()) {
            sockets.add(Aldo.subscribe("/question/" + questionID,
                    new String[]{"/question/" + questionID + "/review"}, (player, sessionID) ->
                            isAssigned(player.getPlayerID(), questionID)));
        }

        // Keep web socket alive.
        Aldo.setupGameLoop(() -> {
            for (SubscriptionWebSocket socket : sockets) {
                socket.sendAll("");
            }
        }, 30000);

        questionPaths();
        moderatorQuestionPaths();
    }

    /**
     * Creates routes associated with research questions.
     */
    protected static void questionPaths() {
        Aldo.get("/question", (player, json) -> getQuestion(player));

        Aldo.get("/assigned", (player, json) -> getAssignedQuestions(player));

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

        Aldo.get("publications", (player, json) -> getPublications(player));

        Aldo.get("/players", (player, json) -> getPlayersPublications(player.getSession().getSessionID()));
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
