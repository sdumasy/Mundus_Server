package models;

import database.CreateUniqueIDs;
import database.DatabaseTest;
import database.PlayerQueries;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 2-1-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({PlayerQueries.class, CreateUniqueIDs.class})
@PowerMockIgnore("javax.net.ssl.*")
public class PlayerMockTest {

    /**
     * Test addition of a new Player to a session.
     */
    @Test
    public void newPlayerMockTest() {
        PowerMockito.mockStatic(PlayerQueries.class);
        PowerMockito.mockStatic(CreateUniqueIDs.class);
        when(CreateUniqueIDs.generateUniqueID(any(), any())).thenReturn("1");
        when(PlayerQueries.addNewPlayer(any())).thenReturn(false);
        Player newPlayer = Player.newPlayer(
                new Session(DatabaseTest.SESSION_ID, DatabaseTest.PLAYER_ID, 1, LocalDateTime.now()), 0,
                new Device(DatabaseTest.DEVICE_ID, DatabaseTest.TOKEN), 42, DatabaseTest.USERNAME);
        assertNull(newPlayer);
    }
}
