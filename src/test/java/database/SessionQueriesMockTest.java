package database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.HaltException;

import static database.MockHelper.*;
import static database.SessionQueries.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 4-1-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class})
public class SessionQueriesMockTest {


    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     *  Test whether joining a non existing session raises an exception.
     */
    @Test
    public void playerJoinSessionEmptyTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());
        exception.expect(HaltException.class);
        playerJoinSession("", getDevice(), "");
    }

    /**
     *  Test whether joining a duplicate session raises an exception.
     */
    @Test
    public void playerJoinSessionDuplicateTest() {
        beforeMock();
        getResult().add(getM());
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        playerJoinSession("", getDevice(), "");
    }

    /**
     * Test whether getting a non existing session raises an exception.
     */
    @Test
    public void getSessionEmptyTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());
        exception.expect(HaltException.class);
        getSession("");
    }

    /**
     * Test whether getting a duplicate session raises an exception.
     */
    @Test
    public void getSessionDuplicateTest() {
        beforeMock();
        getResult().add(getM());
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        getSession("");
    }

    /**
     * Test whether getting tokens for a non existing session raises an exception.
     */
    @Test
    public void getSessionTokensEmptyTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());
        exception.expect(HaltException.class);
        getSessionTokens("");
    }

    /**
     * Test whether getting more than two tokens raises an exception.
     */
    @Test
    public void getSessionTokensMultipleTest() {
        beforeMock();
        getResult().add(getM());
        getResult().add(getM());
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        getSessionTokens("");
    }

    /**
     * Test changing the session status to zero.
     */
    @Test
    public void updateSessionStatusZeroTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeManipulationQuery(any(), any(), any())).thenReturn(true);
        assertTrue(updateSessionStatus("", 0));
    }

    /**
     * Test changing the session status to not zero.
     */
    @Test
    public void upateSessionStatusNonZeroTest() {
        beforeMock();
        getResult().add(getM());
        PowerMockito.mockStatic(Database.class);
        when(Database.executeManipulationQuery(any(), any(), any())).thenReturn(true);
        assertTrue(updateSessionStatus("", 1));
    }
}
