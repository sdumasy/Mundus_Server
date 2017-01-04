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
import static database.PlayerQueries.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 4-1-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class})
public class PlayerQueriesMockTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test whether this method correctly indicates the amount of results in a map.
     */
    @Test
    public void singleResultTrueTest() {
        beforeMock();
        assertTrue(singleResult(getResult()));
    }

    /**
     * Test whether this method correctly indicates the amount of results in a map.
     */
    @Test
    public void singleResultFalseTest() {
        beforeMock();
        assertFalse(singleResult(getEmpty()));
    }

    /**
     * Test whether this method correctly indicates the amount of results in a map.
     */
    @Test
    public void singleResultHaltTest() {
        beforeMock();
        getResult().add(getM());
        exception.expect(HaltException.class);
        singleResult(getResult());
    }

    /**
     * Test whether we can add a new player given there are no duplicate entries.
     */
    @Test
    public void addNewPlayerTrueTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any(), any(), any())).thenReturn(getEmpty());
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getEmpty());
        when(Database.executeManipulationQuery(any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        assertTrue(addNewPlayer(getPlayer()));
    }

    /**
     * Test whether we can add a new player given there are duplicate entries.
     */
    @Test
    public void addNewPlayerHaltTest1() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any(), any(), any())).thenReturn(getEmpty());
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        addNewPlayer(getPlayer());
    }

    /**
     * Test whether we can add a new player given there are duplicate entries.
     */
    @Test
    public void addNewPlayerHaltTest2() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any(), any(), any())).thenReturn(getEmpty());
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        addNewPlayer(getPlayer());
    }

    /**
     * Test whether we can add a new player given there are duplicate entries.
     */
    @Test
    public void addNewPlayerHaltTest3() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any(), any(), any())).thenReturn(getResult());
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        addNewPlayer(getPlayer());
    }

    /**
     * Test whether we can add a new player given there are duplicate entries.
     */
    @Test
    public void addNewPlayerHaltTest4() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any(), any(), any())).thenReturn(getResult());
        when(Database.executeSearchQuery(any(), any())).thenReturn(getEmpty());
        when(Database.executeSearchQuery(any(), any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        addNewPlayer(getPlayer());
    }

    /**
     * Test whether this method correctly indicates the amount of results in a map.
     */
    @Test
    public void createSinglePlayerHaltTest1() {
        beforeMock();
        exception.expect(HaltException.class);
        createSinglePlayer(getEmpty());
    }

    /**
     * Test whether this method correctly indicates the amount of results in a map.
     */
    @Test
    public void createSinglePlayerHaltTest2() {
        beforeMock();
        getResult().add(getM());
        exception.expect(HaltException.class);
        createSinglePlayer(getResult());
    }
}
