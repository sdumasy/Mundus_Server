package database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static database.CreateUniqueIDs.generateUniqueID;
import static database.CreateUniqueIDs.generateUniqueJoinToken;
import static database.MockHelper.beforeMock;
import static database.MockHelper.getEmpty;
import static database.MockHelper.getResult;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 4-1-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class})
public class CreateUniqueIDsMockTest {

    /**
     * Test whether a new ID is generated when the first one already exists.
     */
    @Test
    public void generateUniqueIDMultipleTimesTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(
                getResult()).thenReturn(getResult()).thenReturn(getEmpty());

        assertNotNull(generateUniqueID("", ""));
    }

    /**
     * Test whether a new token is generated when the first one already exists.
     */
    @Test
    public void generateUniqueJoinTokenMultipleTimesTest() {
        beforeMock();
        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(
                getResult()).thenReturn(getResult()).thenReturn(getEmpty());

        assertNotNull(generateUniqueJoinToken());
    }

}
