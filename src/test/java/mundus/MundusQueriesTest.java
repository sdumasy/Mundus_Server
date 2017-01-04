package mundus;

import com.google.gson.JsonObject;
import database.Database;
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
import java.util.*;

import static database.MockHelper.*;
import static mundus.MundusQueries.getQuestion;
import static mundus.MundusQueries.verifyAssignQuestion;
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
        map.put("text", "Is this a question?");
        getEmpty().add(map);
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult()).thenReturn(getEmpty());
        when(Database.executeManipulationQuery(any(), any(), any(), any())).thenReturn(true);

        JsonObject jsonObject = getQuestion(getPlayer());
        assertEquals("42", jsonObject.get("question_id").getAsString());
        assertEquals("Is this a question?", jsonObject.get("text").getAsString());
    }

    /**
     * Verify that an exception is thrown when all questions are already assigned.
     */
    @Test
    public void getQuestionFailureTest() {
        beforeMock();
        Map<String, Object> map = new HashMap<>();
        map.put("question_id", "42");
        map.put("text", "Is this a question?");
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
}