package database;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import spark.HaltException;

import static database.AuthenticationTokenQueries.selectAuthorizationToken;
import static database.MockHelper.beforeMock;
import static database.MockHelper.getM;
import static database.MockHelper.getResult;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by Thomas on 4-1-2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Database.class})
public class AuthenticationTokenQueriesMockTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    /**
     * Test whether the method throws an exception when a token is not unique.
     */
    @Test
    public void selectNonUniqueTokenTest() {
        beforeMock();
        getResult().add(getM());

        PowerMockito.mockStatic(Database.class);
        when(Database.executeSearchQuery(any(), any())).thenReturn(getResult());
        exception.expect(HaltException.class);
        selectAuthorizationToken("");
    }
}
