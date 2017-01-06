package mundus;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import database.Database;
import models.Device;
import models.Player;
import models.Role;
import models.Session;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.HaltException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static database.MockHelper.*;
import static mundus.MundusQueries.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by Thomas on 4-1-2017.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class})
public class MundusQueriesTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test whether constructor is private and does not raise any exceptions.
     * @throws NoSuchMethodException The method must be there.
     * @throws IllegalAccessException The method must be accessible.
     * @throws InvocationTargetException The method must be invocable
     * @throws InstantiationException The method must be instantiationable.
     */
    @Test
    public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        Constructor<MundusQueries> constructor = MundusQueries.class.getDeclaredConstructor();
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    /**
     * Verify that an exception is raised when a player already has three or more questions assigned and asks for a new
     * one.
     */
    @Test
    public void getQuestionTooManyTest() {
        beforeMock();
        getResult().add(getM());
        getResult().add(getM());

        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        getQuestion(getPlayer());
    }

    /**
     * Test that a question is assigned when the player requesting has nog more than 2 questions and there still is a
     * question available.
     */
    @Test
    public void getQuestionSuccessTest() {
        beforeMock();
        Map<String, Object> map = new HashMap<>();
        map.put("question_id", "42");
        map.put("question", "Is this a question?");
        getEmpty().add(map);
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult()).thenReturn(getEmpty());
        when(Database.executeManipulationQuery(any(), any(), any(), any())).thenReturn(true);

        JsonObject jsonObject = getQuestion(getPlayer());
        assertEquals("42", jsonObject.get("question_id").getAsString());
        assertEquals("Is this a question?", jsonObject.get("question").getAsString());
    }

    /**
     * Verify that an exception is thrown when all questions are already assigned.
     */
    @Test
    public void getQuestionFailureTest() {
        beforeMock();
        Map<String, Object> map = new HashMap<>();
        map.put("question_id", "42");
        map.put("question", "Is this a question?");
        getEmpty().add(map);
        PowerMockito.mockStatic(Database.class);
        List<Map<String, Object>> none = new ArrayList<>();
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult()).thenReturn(none);
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getEmpty());
        when(Database.executeManipulationQuery(any(), any(), any(), any())).thenReturn(true);

        exception.expect(HaltException.class);
        getQuestion(getPlayer());
    }

    /**
     * Verify that an exception is thrown when there are no more questions available to assign.
     */
    @Test
    public void verifyAssignQuestionFailureTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any())).thenReturn(getEmpty());

        exception.expect(HaltException.class);
        verifyAssignQuestion("", "");
    }

    /**
     * Verify that an exception is thrown when you try to answer a question that does not belong to you.
     */
    @Test
    public void submitAnswerSuccesTest() {
        try {
            beforeMock();
            PowerMockito.mockStatic(Database.class);
            when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getResult());
            when(Database.executeManipulationQuery(any(), any(), any(), any())).thenReturn(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("answer", "42");
            submitAnswer(getPlayer(), "", jsonObject);
        } catch (Exception e) {
            Assert.fail("Submitting an answer should succeed, but it failed: " + e.getLocalizedMessage());
        }

    }

    /**
     * Verify that you can answer a question that belongs to you.
     */
    @Test
    public void submitAnswerFailureTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getEmpty());
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("answer", "42");

        exception.expect(HaltException.class);
        submitAnswer(getPlayer(), "", jsonObject);
    }

    /**
     * Verify that you can get all non reviewed questions in your session as a moderator or administrator.
     */
    @Test
    public void getSubmittedSuccessTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        Map<String, Object> map = new HashMap<>();
        map.put("question_id", "42");
        map.put("question", "?");
        map.put("answer", "answer");
        map.put("correct_answer", "correct");
        getEmpty().add(map);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());

        JsonObject jsonObjectList = getSubmitted(getPlayer());
        JsonArray jsonArray = jsonObjectList.getAsJsonArray("answers");
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertEquals("42", jsonObject.get("question_id").getAsString());
        assertEquals("?", jsonObject.get("question").getAsString());
        assertEquals("answer", jsonObject.get("answer").getAsString());
        assertEquals("correct", jsonObject.get("correct_answer").getAsString());
    }


    /**
     * Verify that you can't get submitted answers if you are not a moderator or an admin.
     */
    @Test
    public void getSubmittedFailureTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);

        exception.expect(HaltException.class);
        getSubmitted(new Player("ID", new Session("", "", 1, LocalDateTime.now()),
                new Device("", ""), Role.User, 0, "username"));
    }

    /**
     * Verify that you can submit a review for questions in your session as a moderator or administrator.
     */
    @Test
    public void submitReviewSuccessTest() {
        try {
            beforeMock();
            PowerMockito.mockStatic(Database.class);
            when(Database.executeManipulationQuery(any(), any(), any())).thenReturn(true);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("reviewed", "1");
            submitReview(getPlayer(), "", jsonObject);

        } catch (Exception e) {
            Assert.fail("Submitting a review should succeed, but it failed: " + e.getLocalizedMessage());
        }
    }

    /**
     * Verify that you cannot submit a review for questions in your session if you are a user.
     */
    @Test
    public void submitReviewFailureTest() {
        beforeMock();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("reviewed", "1");
        submitReview(getPlayer(), "", jsonObject);

        exception.expect(HaltException.class);
        submitReview(new Player("ID", new Session("", "", 1, LocalDateTime.now()),
                new Device("", ""), Role.User, 0, "username"), "", jsonObject);
    }

    /**
     * Verify that you can get all non reviewed questions in your session as a moderator or administrator.
     */
    @Test
    public void getPublicationsSuccessTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        Map<String, Object> map = new HashMap<>();
        map.put("question_id", "42");
        map.put("question", "?");
        map.put("answer", "answer");
        map.put("correct_answer", "correct");
        getEmpty().add(map);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());

        JsonObject jsonObjectList = getPublications(getPlayer());
        JsonArray jsonArray = jsonObjectList.getAsJsonArray("answers");
        JsonObject jsonObject = jsonArray.get(0).getAsJsonObject();
        assertEquals("42", jsonObject.get("question_id").getAsString());
        assertEquals("?", jsonObject.get("question").getAsString());
        assertEquals("answer", jsonObject.get("answer").getAsString());
        assertEquals("correct", jsonObject.get("correct_answer").getAsString());
    }
}